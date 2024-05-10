package CatService;

import daos.CatDao.ICatDao;
import models.Cat;
import models.Owner;

import java.time.LocalDate;
import java.util.List;

public class CatService implements ICatService {
    private final ICatDao catDao;

    public CatService(ICatDao catDao) {
        this.catDao = catDao;
    }

    @Override
    public Cat createCat(String name, LocalDate birthDate, String breed, String color, Owner owner, List<Cat> friends) {
        return catDao.create(new Cat(name, birthDate, breed, color, owner, friends));
    }

    @Override
    public Cat getCat(long id) {
        return catDao.read(id);
    }

    @Override
    public void deleteCat(long id) {
        Cat cat = catDao.read(id);
        if (cat != null) {
            catDao.delete(cat);
        }
    }

    @Override
    public void setOwner(Cat cat, Owner owner) {
        catDao.setOwner(cat, owner);
    }

    @Override
    public void makeFriends(Cat cat1, Cat cat2) {
        catDao.makeFriends(cat1, cat2);
    }
}
