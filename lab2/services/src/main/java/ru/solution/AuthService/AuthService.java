package ru.solution.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import ru.solution.dtos.JwtRequest;
import ru.solution.dtos.RegistrationOwnerDto;

public interface AuthService {
    ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest);
    ResponseEntity<?> createNewOwner(@RequestBody RegistrationOwnerDto registrationOwnerDto);
}
