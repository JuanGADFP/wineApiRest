package org.juang.test.springboot.app.repository;

import org.juang.test.springboot.app.models.Owner;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerRepository extends MongoRepository<Owner,Long> {

}

