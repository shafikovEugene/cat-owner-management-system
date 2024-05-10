package CatService;

import models.Cat;
import models.Owner;

import java.time.LocalDate;
import java.util.List;

public interface ICatService {
    Cat createCat(String name, LocalDate birthDate, String breed, String color, Owner owner, List<Cat> friends);
    Cat getCat(long id);
    void deleteCat(long id);
    void setOwner(Cat cat, Owner owner);
    void makeFriends(Cat cat1, Cat cat2);
}
