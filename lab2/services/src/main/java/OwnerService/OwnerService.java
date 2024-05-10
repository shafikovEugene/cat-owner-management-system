package OwnerService;

import daos.OwnerDao.IOwnerDao;
import models.Cat;
import models.Owner;

import java.time.LocalDate;
import java.util.List;

public class OwnerService implements IOwnerService {
    private final IOwnerDao ownerDao;

    public OwnerService(IOwnerDao ownerDao) {
        this.ownerDao = ownerDao;
    }

    @Override
    public void createOwner(String name, LocalDate birthDate, List<Cat> cats) {
        ownerDao.create(new Owner(name, birthDate, cats));
    }

    @Override
    public Owner getOwner(long id) {
        return ownerDao.read(id);
    }

    @Override
    public void deleteOwner(long id) {
        Owner owner = ownerDao.read(id);
        if (owner != null) {
            ownerDao.delete(owner);
        }
    }
}
