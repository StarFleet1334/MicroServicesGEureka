package com.micro.workload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication()
@EnableDiscoveryClient
public class WorkloadApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkloadApplication.class, args);
    }

}

/*

Tasks to do:
1.TransactionId should be obtained from the main microservice, not generated in the second one.
2.TrainingAdded and trainingDeleted should be the one endpoint like 'updateWorkload' This microservice should not save trainings only trainer workload.
3.For controller test please use MockMvc (mockMvc.perform() method) ( DONE )
4.Use dto objects for response data ( DONE )
5. try/catch block should not be use in tests ( DONE )


 */