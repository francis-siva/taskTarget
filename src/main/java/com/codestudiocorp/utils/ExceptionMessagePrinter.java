package com.codestudiocorp.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionMessagePrinter {
    Exception exception;

    public void showError() {
        System.err.printf("error: %s%n", exception.getMessage());
    }
    public void showError(String exceptionMessage) {
        System.err.println(exceptionMessage);
    }

    public void showCause() {
        System.err.printf("error cause: %s%n", exception.getClass().getSimpleName());
    }
}