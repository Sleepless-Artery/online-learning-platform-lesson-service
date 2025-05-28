package org.sleepless_artery.lesson_service.config.grpc;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.sleepless_artery.lesson_service.CourseVerificationServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GrpcClientConfig {

    @GrpcClient("course-service")
    private CourseVerificationServiceGrpc.CourseVerificationServiceBlockingStub blockingStub;

    @Bean
    public CourseVerificationServiceGrpc.CourseVerificationServiceBlockingStub blockingStub() {
        return blockingStub;
    }
}
