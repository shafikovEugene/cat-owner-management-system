package ru.solution.OwnerService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.solution.RoleService.RoleService;
import ru.solution.dtos.EntityToDtoMapper;
import ru.solution.dtos.OwnerDto;
import ru.solution.dtos.RegistrationOwnerDto;
import ru.solution.models.Owner;
import ru.solution.repository.OwnerRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {
    private final OwnerRepository ownerRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;


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
        optionalOwner.ifPresent(ownerRepository::delete);
    }

    public Optional<Owner> findByName(String name) {
        return ownerRepository.findByName(name);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Owner owner = findByName(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Owner '%s' not found", username)
        ));

        return new User(
                owner.getName(),
                owner.getPassword(),
                owner.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }

    public Owner createNewOwner(RegistrationOwnerDto registrationOwnerDto) {
        Owner owner = new Owner();
        owner.setName(registrationOwnerDto.getName());
        owner.setPassword(passwordEncoder.encode(registrationOwnerDto.getPassword()));
        owner.setRoles(List.of(roleService.getUserRole()));
        return ownerRepository.save(owner);
    }
}
