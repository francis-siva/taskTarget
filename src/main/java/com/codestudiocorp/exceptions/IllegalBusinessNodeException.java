package com.codestudiocorp.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class IllegalBusinessNodeException extends Exception {
    public IllegalBusinessNodeException(String message) {
        super(message);
    }
}
