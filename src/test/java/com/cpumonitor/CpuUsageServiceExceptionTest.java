package com.cpumonitor;

import org.junit.jupiter.api.Test;

import com.cpumonitor.exception.CpuUsageServiceException;

import static org.junit.jupiter.api.Assertions.*;

public class CpuUsageServiceExceptionTest {

    @Test
    public void testCpuUsageServiceExceptionMessage() {
        String errorMessage = "CPU usage service failed";
        CpuUsageServiceException exception = assertThrows(CpuUsageServiceException.class, () -> {
            throw new CpuUsageServiceException(errorMessage);
        });

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    public void testCpuUsageServiceExceptionMessageAndCause() {
        String errorMessage = "CPU usage service failed";
        Throwable cause = new RuntimeException("Underlying cause of the error");
        CpuUsageServiceException exception = assertThrows(CpuUsageServiceException.class, () -> {
            throw new CpuUsageServiceException(errorMessage, cause);
        });

        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
