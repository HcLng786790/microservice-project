package com.huuduc.orderservice.config;

import com.huuduc.orderservice.service.activity.OrderActivityImpl;
import com.huuduc.orderservice.service.workflow.OrderWorkflowImpl;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
@RequiredArgsConstructor
public class WorkerRunner implements CommandLineRunner {

    private final WorkerFactory workerFactory;
    private final OrderActivityImpl orderActivity;

    @Override
    public void run(String... args){

        Worker registrationWorker = workerFactory.newWorker("OrderTaskQueue");
        registrationWorker.registerWorkflowImplementationTypes(OrderWorkflowImpl.class);
        registrationWorker.registerActivitiesImplementations(orderActivity);

        workerFactory.start();
    }
}
