package ru.solution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.solution.models.Cat;

@Repository
public interface CatRepository extends JpaRepository<Cat, Long> {
}
