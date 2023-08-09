package br.com.gomining.schoolsimulator.common.exception;

public class ActivityAlreadyAddedException extends RuntimeException {
    public ActivityAlreadyAddedException(String message) {
        super(message);
    }
}
