package org.sleepless_artery.lesson_service.exception;

public class LessonAlreadyExistsException extends RuntimeException {
    public LessonAlreadyExistsException(String message) {
        super(message);
    }
}
