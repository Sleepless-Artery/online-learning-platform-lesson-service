package org.sleepless_artery.lesson_service.grpc.client;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sleepless_artery.lesson_service.VerifyCourseExistenceRequest;
import org.sleepless_artery.lesson_service.config.grpc.GrpcClientConfig;
import org.sleepless_artery.lesson_service.exception.GrpcProcessingException;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@RequiredArgsConstructor
public class CourseVerificationServiceGrpcClient {

    private final GrpcClientConfig grpcClientConfig;


    public boolean verifyCourseExistence(Long courseId) {
        log.info("Sending gRPC request to verify course existence");

        var request = VerifyCourseExistenceRequest.newBuilder()
                .setId(courseId)
                .build();

        try {
            return grpcClientConfig.blockingStub()
                    .withDeadlineAfter(30, TimeUnit.SECONDS)
                    .verifyCourseExistence(request)
                    .getExistence();
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                return false;
            }
            log.error("gRPC error: {}", e.getStatus());
            throw new GrpcProcessingException("Course verification service unavailable");
        }
    }
}
