import daos.CatDao.CatDao;
import daos.CatDao.ICatDao;
import models.Cat;
import models.Owner;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CatDaoTests {
    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Transaction transaction;
    private ICatDao catDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        catDao = new CatDao(sessionFactory);
    }

    @Test
    void testCreateCat() {
        Cat cat = catDao.create(new Cat("Барсик", LocalDate.parse("2019-05-23"), "мэйн-кун", "рыжий", new Owner(), new ArrayList<Cat>()));

        assertNotNull(cat);
        verify(session).save(cat);
        verify(session).close();
        verify(transaction).commit();
    }

    @Test
    void testReadCat() {
        Cat cat = catDao.create(new Cat("Барсик", LocalDate.parse("2019-05-23"), "мэйн-кун", "рыжий", new Owner(), new ArrayList<Cat>()));
        when(session.get(Cat.class, cat.getId())).thenReturn(cat);

        assertEquals(catDao.read(cat.getId()), cat);
        verify(session).save(cat);
        verify(session, times(2)).close();
        verify(transaction).commit();
    }

    @Test
    void testDeleteCat() {
        Cat cat = catDao.create(new Cat("Барсик", LocalDate.parse("2019-05-23"), "мэйн-кун", "рыжий", new Owner(), new ArrayList<Cat>()));

        catDao.delete(cat);

        assertNull(catDao.read(cat.getId()));
        verify(session).save(cat);
        verify(session, times(3)).close();
        verify(transaction, times(2)).commit();
    }

    @Test
    void testUpdateCat() {
        Cat cat = catDao.create(new Cat("Барсик", LocalDate.parse("2019-05-23"), "мэйн-кун", "рыжий", new Owner(), new ArrayList<Cat>()));
        when(session.get(Cat.class, cat.getId())).thenReturn(cat);

        cat.setName("Мурзик");
        catDao.update(cat);
        assertEquals("Мурзик", catDao.read(cat.getId()).getName());
        verify(session).save(cat);
        verify(session, times(3)).close();
        verify(transaction, times(2)).commit();
    }

    @Test
    void testSetOwner() {
        Cat cat = catDao.create(new Cat("Барсик", LocalDate.parse("2019-05-23"), "мэйн-кун", "рыжий", new Owner(), new ArrayList<Cat>()));
        when(session.get(Cat.class, cat.getId())).thenReturn(cat);

        Owner owner = new Owner("Артур", LocalDate.parse("2000-03-11"), new ArrayList<Cat>());

        catDao.setOwner(cat, owner);

        assertEquals(owner, catDao.read(cat.getId()).getOwner());
        verify(session).save(cat);
        verify(session, times(3)).close();
        verify(transaction, times(2)).commit();
    }

    @Test
    void testMakeFriends() {
        Cat cat1 = catDao.create(new Cat("Барсик", LocalDate.parse("2019-05-23"), "мэйн-кун", "рыжий", new Owner(), new ArrayList<Cat>()));
        cat1.setId(1L);
        when(session.get(Cat.class, cat1.getId())).thenReturn(cat1);

        Cat cat2 = catDao.create(new Cat("Мурзик", LocalDate.parse("2020-06-13"), "рэгдолл", "черный", new Owner(), new ArrayList<Cat>()));
        cat2.setId(2L);
        when(session.get(Cat.class, cat2.getId())).thenReturn(cat2);

        catDao.makeFriends(cat1, cat2);

        assertEquals(catDao.read(cat1.getId()).getFriends().get(0), cat2);
        assertEquals(catDao.read(cat2.getId()).getFriends().get(0), cat1);
        verify(session).save(cat1);
        verify(session).save(cat2);
        verify(session, times(5)).close();
        verify(transaction, times(3)).commit();
    }
}
