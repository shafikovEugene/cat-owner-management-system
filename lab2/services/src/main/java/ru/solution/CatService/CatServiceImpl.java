package ru.solution.CatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.solution.dtos.CatDto;
import ru.solution.dtos.EntityToDtoMapper;
import ru.solution.models.Cat;
import ru.solution.models.Owner;
import ru.solution.repository.CatRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CatServiceImpl implements CatService {
    @Autowired
    private CatRepository catRepository;

    @Override
    public List<CatDto> findAllCats() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return catRepository.findAll().stream().map(EntityToDtoMapper::toCatDto).collect(Collectors.toList());
        } else {
            return catRepository.findByOwnerName(username).stream().map(EntityToDtoMapper::toCatDto).collect(Collectors.toList());
        }
    }

    @Override
    public CatDto saveCat(Cat cat) {
        return EntityToDtoMapper.toCatDto(catRepository.save(cat));
    }

    @Override
    public CatDto findCat(long id) {
        Optional<Cat> optionalCat = catRepository.findById(id);
        if (optionalCat.isPresent()) {
            return EntityToDtoMapper.toCatDto(optionalCat.get());
        }
        return null;
    }

    @Override
    public CatDto updateCat(Cat cat) {
        return EntityToDtoMapper.toCatDto(catRepository.save(cat));
    }

    @Override
    public String deleteCat(long id) {
        Optional<Cat> optionalCat = catRepository.findById(id);
        if (optionalCat.isPresent()) {
            catRepository.delete(optionalCat.get());
            return "Deleted";
        }
        return "Error";
    }

    @Override
    public void setOwner(Cat cat, Owner owner) {
        cat.setOwner(owner);
        updateCat(cat);
    }

    @Override
    public void makeFriends(Cat cat1, Cat cat2) {
        cat1.getFriends().add(cat2);
        cat2.getFriends().add(cat1);
        updateCat(cat1);
        updateCat(cat2);
    }

    @Override
    public List<CatDto> findCatsByColor(String color) {
        return catRepository.findAll().stream()
                .filter(cat -> color.equals(cat.getColor()))
                .map(EntityToDtoMapper::toCatDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CatDto> findCatsByOwner(long ownerId) {
        return catRepository.findAll().stream()
                .filter(cat -> cat.getOwner() != null && cat.getOwner().getId() == ownerId)
                .map(EntityToDtoMapper::toCatDto)
                .collect(Collectors.toList());
    }
}
