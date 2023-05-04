package org.juang.test.springboot.app.repository;

import org.juang.test.springboot.app.models.Wine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.*;

import java.util.Optional;

@Repository
public interface WineRepository extends JpaRepository<Wine, Long> {
    @Query("SELECT w FROM Wine w JOIN FETCH w.owners")
    List<Wine> findAllWithOwners();

    Optional<Wine> findByName(String name);
}
