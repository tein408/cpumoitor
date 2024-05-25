package com.cpumonitor;

import org.junit.jupiter.api.Test;

import com.cpumonitor.exception.DatabaseOperationException;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseOperationExceptionTest {

    @Test
    public void testDatabaseOperationExceptionMessage() {
        String errorMessage = "Database operation failed";
        DatabaseOperationException exception = assertThrows(DatabaseOperationException.class, () -> {
            throw new DatabaseOperationException(errorMessage);
        });

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    public void testDatabaseOperationExceptionMessageAndCause() {
        String errorMessage = "Database operation failed";
        Throwable cause = new RuntimeException("Underlying cause of the error");
        DatabaseOperationException exception = assertThrows(DatabaseOperationException.class, () -> {
            throw new DatabaseOperationException(errorMessage, cause);
        });

        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
