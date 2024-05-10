package org.example;

import CatService.CatService;
import CatService.ICatService;
import OwnerService.IOwnerService;
import OwnerService.OwnerService;
import daos.CatDao.CatDao;
import daos.CatDao.ICatDao;
import daos.OwnerDao.IOwnerDao;
import daos.OwnerDao.OwnerDao;
import models.Cat;
import models.Owner;

import java.time.LocalDate;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        IOwnerDao ownerDao = new OwnerDao();
        IOwnerService ownerService = new OwnerService(ownerDao);
        ownerService.createOwner("Петя", LocalDate.now(), new ArrayList<Cat>());
        ownerService.createOwner("Дима", LocalDate.now(), new ArrayList<Cat>());

        ICatDao catDao = new CatDao();
        ICatService catService = new CatService(catDao);

        Owner owner = ownerService.getOwner(2);

        Cat cat1 = catService.createCat("Барсик", LocalDate.parse("2019-05-23"), "мэйн-кун", "черный", null, new ArrayList<Cat>());
        Cat cat2 = catService.createCat("Мурзик", LocalDate.parse("2019-05-23"), "рэгдолл", "белый", null, new ArrayList<Cat>());


        catService.setOwner(cat1, owner);
        catService.setOwner(cat2, owner);

        catService.makeFriends(cat1, cat2);

    }
}