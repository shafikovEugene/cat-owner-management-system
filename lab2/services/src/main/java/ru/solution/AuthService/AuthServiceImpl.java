package ru.solution.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.solution.OwnerService.OwnerService;
import ru.solution.dtos.JwtRequest;
import ru.solution.dtos.JwtResponse;
import ru.solution.dtos.OwnerDto;
import ru.solution.dtos.RegistrationOwnerDto;
import ru.solution.exceptions.CreateOwnerError;
import ru.solution.models.Owner;
import ru.solution.utils.JwtTokenUtils;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static ru.solution.dtos.EntityToDtoMapper.toCatDto;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private OwnerService ownerService;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;
    @Autowired
    private AuthenticationManager authenticationManager;

    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new CreateOwnerError(HttpStatus.UNAUTHORIZED.value(), "Wrong login or password"), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = ownerService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    public ResponseEntity<?> createNewOwner(@RequestBody RegistrationOwnerDto registrationOwnerDto) {
        if (!registrationOwnerDto.getPassword().equals(registrationOwnerDto.getConfirmPassword())) {
            return new ResponseEntity<>(new CreateOwnerError(HttpStatus.BAD_REQUEST.value(), "Passwords don't match"), HttpStatus.BAD_REQUEST);
        }
        Owner owner = ownerService.createNewOwner(registrationOwnerDto);
        return ResponseEntity.ok(
                new OwnerDto(
                    owner.getId(),
                    owner.getName(),
                    owner.getBirthDate(),
                    owner.getCats() != null ? owner.getCats().stream()
                            .map(cat -> toCatDto(cat, false)) // Avoid recursion
                            .collect(Collectors.toList()) : new ArrayList<>()
                )
        );
    }
}
