package daos.OwnerDao;

import models.Owner;

public interface IOwnerDao {
    Owner create(Owner owner);
    Owner read(Long id);
    void update(Owner owner);
    void delete(Owner owner);
}
