package com.cpumonitor.exception;

public class CpuUsageServiceException extends RuntimeException {
    public CpuUsageServiceException(String message) {
        super(message);
    }

    public CpuUsageServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}