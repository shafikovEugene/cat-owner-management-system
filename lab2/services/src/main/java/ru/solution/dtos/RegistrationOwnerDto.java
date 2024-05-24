package ru.solution.dtos;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;

@Data
@Getter
public class RegistrationOwnerDto {
    private String name;
    private String password;
    private String confirmPassword;
    private LocalDate birthDate;
}
