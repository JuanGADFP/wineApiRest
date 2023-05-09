package org.juang.test.springboot.app.repository;

import org.juang.test.springboot.app.models.Wine;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

import java.util.Optional;


@Repository
public interface WineRepository extends MongoRepository<Wine, String> {
/*
    List<Wine> findAll();

    Optional<Wine> findByName(String name);

 */
}

/*
@Repository
public interface WineRepository extends MongoRepository<Wine,Long> {
   @Query("SELECT w FROM Wine w JOIN FETCH w.owners")
    List<Wine> findAllWithOwners();

    Optional<Wine> findByName(String name);


}
 */
