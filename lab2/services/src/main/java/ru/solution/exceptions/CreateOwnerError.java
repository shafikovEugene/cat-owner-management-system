package ru.solution.exceptions;

import lombok.Data;

import java.util.Date;

@Data
public class CreateOwnerError {
    private int status;
    private String message;
    private Date timestamp;

    public CreateOwnerError(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }
}
