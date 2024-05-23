package ru.solution.OwnerService;

import ru.solution.dtos.OwnerDto;
import ru.solution.models.Owner;

import java.util.List;

public interface OwnerService {
    List<OwnerDto> findAllOwners();
    OwnerDto saveOwner(Owner owner);
    OwnerDto findOwner(long id);
    OwnerDto updateOwner(Owner owner);
    void deleteOwner(long id);
}
