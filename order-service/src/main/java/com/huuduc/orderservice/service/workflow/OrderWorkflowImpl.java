package com.huuduc.orderservice.service.workflow;

import com.huuduc.orderservice.dto.OrderRequest;
import com.huuduc.orderservice.dto.OrderResponse;
import com.huuduc.orderservice.service.activity.OrderActivity;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;

import java.time.Duration;

//@Component
public class OrderWorkflowImpl implements OrderWorkflow {

    private final OrderActivity orderActivity;

    public OrderWorkflowImpl(){
        this.orderActivity = Workflow.newActivityStub(OrderActivity.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofMinutes(5))
                        .setScheduleToCloseTimeout(Duration.ofMinutes(10))
                        .build());
    }

    @Override
    public OrderResponse createOrderWorkflow(OrderRequest orderRequest, String token) {

        Saga saga = new Saga(new Saga.Options.Builder().setParallelCompensation(false).build());

        try {

            // Thực hiện activity lấy thông tin user
            var user = this.orderActivity.getUser2(token);

            // Tiến hành mua hàng và giảm số lượng sản phẩm
            var purchaseResponse = orderActivity.purchase2(orderRequest.purchaseRequestList(), token);

            // Nếu sau quá trình này có lỗi xảy ra sẽ thực hiện trả lại số lượng đã trừ
            saga.addCompensation(()-> orderActivity.cancelPurchase(orderRequest.purchaseRequestList(),token));

            // Lưu thông tin đơn hàng
            var order = this.orderActivity.saveOrder(orderRequest,user,purchaseResponse);

            // Gửi thông báo Notification
            return this.orderActivity.sendNotification(order,user,purchaseResponse);

        }catch (Exception e) {

            Workflow.getLogger(OrderWorkflowImpl.class).info("Non-retryable error: {}", e.getMessage());
            saga.compensate();
            throw Workflow.wrap(e);
        }
    }
}
