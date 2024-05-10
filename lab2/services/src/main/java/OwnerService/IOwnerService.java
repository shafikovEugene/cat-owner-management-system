package OwnerService;

import models.Cat;
import models.Owner;

import java.time.LocalDate;
import java.util.List;

public interface IOwnerService {
    void createOwner(String name, LocalDate birthDate, List<Cat> cats);
    Owner getOwner(long id);
    void deleteOwner(long id);
}
