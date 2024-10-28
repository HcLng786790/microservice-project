package com.huuduc.orderservice.service.workflow;

import com.huuduc.orderservice.dto.OrderRequest;
import com.huuduc.orderservice.dto.OrderResponse;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface OrderWorkflow {

    @WorkflowMethod
    OrderResponse createOrderWorkflow(OrderRequest orderRequest, String token);
}
