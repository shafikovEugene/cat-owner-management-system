package daos.CatDao;

import models.Cat;
import models.Owner;

public interface ICatDao {
    Cat create(Cat cat);
    Cat read(Long id);
    void update(Cat cat);
    void delete(Cat cat);
    void setOwner(Cat cat, Owner owner);
    void makeFriends(Cat cat1, Cat cat2);
}
