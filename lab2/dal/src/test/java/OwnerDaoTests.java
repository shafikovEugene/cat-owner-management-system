import daos.CatDao.CatDao;
import daos.CatDao.ICatDao;
import daos.OwnerDao.IOwnerDao;
import daos.OwnerDao.OwnerDao;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class OwnerDaoTests {
    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Transaction transaction;
    private IOwnerDao ownerDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        ownerDao = new OwnerDao(sessionFactory);
    }

    @Test
    void testCreateOwner() {
        Owner owner = ownerDao.create(new Owner("Артур", LocalDate.parse("2000-03-11"), new ArrayList<Cat>()));

        assertNotNull(owner);
        verify(session).save(owner);
        verify(session).close();
        verify(transaction).commit();
    }

    @Test
    void testReadOwner() {
        Owner owner = ownerDao.create(new Owner("Артур", LocalDate.parse("2000-03-11"), new ArrayList<Cat>()));
        when(session.get(Owner.class, owner.getId())).thenReturn(owner);

        assertEquals(ownerDao.read(owner.getId()), owner);
        verify(session).save(owner);
        verify(session, times(2)).close();
        verify(transaction).commit();
    }

    @Test
    void testDeleteOwner() {
        Owner owner = ownerDao.create(new Owner("Артур", LocalDate.parse("2000-03-11"), new ArrayList<Cat>()));

        ownerDao.delete(owner);

        assertNull(ownerDao.read(owner.getId()));
        verify(session).save(owner);
        verify(session, times(3)).close();
        verify(transaction, times(2)).commit();
    }

    @Test
    void testUpdateOwner() {
        Owner owner = ownerDao.create(new Owner("Артур", LocalDate.parse("2000-03-11"), new ArrayList<Cat>()));
        when(session.get(Owner.class, owner.getId())).thenReturn(owner);

        owner.setName("Петя");
        ownerDao.update(owner);
        assertEquals("Петя", ownerDao.read(owner.getId()).getName());
        verify(session).save(owner);
        verify(session, times(3)).close();
        verify(transaction, times(2)).commit();
    }
}
