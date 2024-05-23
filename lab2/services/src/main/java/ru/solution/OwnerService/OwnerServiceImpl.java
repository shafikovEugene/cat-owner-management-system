package ru.solution.OwnerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.solution.repository.OwnerRepository;
import ru.solution.dtos.EntityToDtoMapper;
import ru.solution.dtos.OwnerDto;
import ru.solution.models.Owner;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OwnerServiceImpl implements OwnerService {
    @Autowired
    private OwnerRepository ownerRepository;

    @Override
    public List<OwnerDto> findAllOwners() {
        return ownerRepository.findAll().stream()
                .map(EntityToDtoMapper::toOwnerDto)
                .collect(Collectors.toList());
    }

    @Override
    public OwnerDto saveOwner(Owner owner) {
        return EntityToDtoMapper.toOwnerDto(ownerRepository.save(owner));
    }

    @Override
    public OwnerDto findOwner(long id) {
        Optional<Owner> optionalOwner = ownerRepository.findById(id);
        return optionalOwner.map(EntityToDtoMapper::toOwnerDto).orElse(null);
    }

    @Override
    public OwnerDto updateOwner(Owner owner) {
        return EntityToDtoMapper.toOwnerDto(ownerRepository.save(owner));
    }

    @Override
    public void deleteOwner(long id) {
        Optional<Owner> optionalOwner = ownerRepository.findById(id);
        if (optionalOwner.isPresent()) {
            ownerRepository.delete(optionalOwner.get());
        }
    }
}
