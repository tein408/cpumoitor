package com.cpumonitor;

import org.junit.jupiter.api.Test;

import com.cpumonitor.exception.InvalidDataException;

import static org.junit.jupiter.api.Assertions.*;

public class InvalidDataExceptionTest {

    @Test
    public void testInvalidDataExceptionMessage() {
        String errorMessage = "Invalid data provided";
        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            throw new InvalidDataException(errorMessage);
        });

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    public void testInvalidDataExceptionMessageAndCause() {
        String errorMessage = "Invalid data provided";
        Throwable cause = new RuntimeException("Cause of the error");
        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            throw new InvalidDataException(errorMessage, cause);
        });

        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
