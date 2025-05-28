package org.sleepless_artery.lesson_service.exception;

public class InvalidCourseIdException extends RuntimeException {
    public InvalidCourseIdException(String message) {
        super(message);
    }
}
