package ru.solution.CatService;

import ru.solution.dtos.CatDto;
import ru.solution.models.Cat;
import ru.solution.models.Owner;

import java.util.List;

public interface CatService {
    List<CatDto> findAllCats();
    CatDto saveCat(Cat cat);
    CatDto findCat(long id);
    CatDto updateCat(Cat cat);
    String deleteCat(long id);
    void setOwner(Cat cat, Owner owner);
    void makeFriends(Cat cat1, Cat cat2);
    List<CatDto> findCatsByColor(String color);
    List<CatDto> findCatsByOwner(long ownerId);
}
