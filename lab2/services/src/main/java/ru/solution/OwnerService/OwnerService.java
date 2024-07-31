package ru.solution.OwnerService;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.solution.dtos.OwnerDto;
import ru.solution.dtos.RegistrationOwnerDto;
import ru.solution.models.Owner;

import java.util.List;
import java.util.Optional;

public interface OwnerService extends UserDetailsService {
    List<OwnerDto> findAllOwners();
    OwnerDto saveOwner(Owner owner);
    OwnerDto findOwner(long id);
    OwnerDto updateOwner(Owner owner);
    void deleteOwner(long id);
    Optional<Owner> findByName(String name);
    Owner createNewOwner(RegistrationOwnerDto registrationOwnerDto);
}
