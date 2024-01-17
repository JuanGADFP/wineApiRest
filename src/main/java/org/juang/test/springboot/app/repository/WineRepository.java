package org.juang.test.springboot.app.repository;

import org.juang.test.springboot.app.models.Wine;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WineRepository extends MongoRepository<Wine, String> {
}

