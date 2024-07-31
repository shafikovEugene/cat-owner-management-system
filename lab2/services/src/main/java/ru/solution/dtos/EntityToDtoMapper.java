package ru.solution.dtos;

import ru.solution.models.Cat;
import ru.solution.models.Owner;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class EntityToDtoMapper {

    public static CatDto toCatDto(Cat cat) {
        if (cat == null) {
            return null;
        }
        return new CatDto(
                cat.getId(),
                cat.getName(),
                cat.getBirthDate(),
                cat.getBreed(),
                cat.getColor(),
                toOwnerDto(cat.getOwner(), false),
                cat.getFriends() != null ? cat.getFriends().stream()
                        .map(friend -> toCatDto(friend, false)) // Avoid recursion
                        .collect(Collectors.toList()) : new ArrayList<>()
        );
    }

    public static CatDto toCatDto(Cat cat, boolean includeOwnerAndFriends) {
        if (includeOwnerAndFriends) {
            return toCatDto(cat);
        }
        if (cat == null) {
            return null;
        }
        return new CatDto(
                cat.getId(),
                cat.getName(),
                cat.getBirthDate(),
                cat.getBreed(),
                cat.getColor(),
                null,
                null
        );
    }

    public static OwnerDto toOwnerDto(Owner owner) {
        if (owner == null) {
            return null;
        }

        return new OwnerDto(
                owner.getId(),
                owner.getName(),
                owner.getBirthDate(),
                owner.getCats() != null ? owner.getCats().stream()
                        .map(cat -> toCatDto(cat, false)) // Avoid recursion
                        .collect(Collectors.toList()) : new ArrayList<>()
        );
    }

    public static OwnerDto toOwnerDto(Owner owner, boolean includeCats) {
        if (includeCats) {
            return toOwnerDto(owner);
        }
        if (owner == null) {
            return null;
        }
        return new OwnerDto(
                owner.getId(),
                owner.getName(),
                owner.getBirthDate(),
                null
        );
    }
}