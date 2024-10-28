package com.huuduc.orderservice.config;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.WorkerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("unused")
@Configuration
public class TemporalConfig {

    private final String temporalServiceAddress = "localhost:7233";
    private final String temporalNamespace = "default";

    @Bean
    public WorkflowServiceStubs workflowServiceStubs(){

        WorkflowServiceStubs workflowServiceStubs = WorkflowServiceStubs.newInstance(
                WorkflowServiceStubsOptions.newBuilder()
                        .setTarget(temporalServiceAddress)
                        .build()
        );
        return workflowServiceStubs;
    }

    @Bean
    public WorkflowClient workflowClient(WorkflowServiceStubs serviceStubs){

        return WorkflowClient.newInstance(
                serviceStubs,
                WorkflowClientOptions.newBuilder().setNamespace(temporalNamespace).build());
    }

    @Bean
    public WorkerFactory workerFactory(WorkflowClient workflowClient){

        return WorkerFactory.newInstance(workflowClient);
    }
}
