package org.juang.test.springboot.app.service;

import org.juang.test.springboot.app.models.Owner;
import org.juang.test.springboot.app.models.Wine;
import org.juang.test.springboot.app.response.WineResponseRest;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface WineService {
    ResponseEntity<List<Wine>> getAllWines();
    ResponseEntity<Wine> getWineById(String id);
    ResponseEntity<WineResponseRest> save(Wine wine);
    ResponseEntity<WineResponseRest> deleteWineById(String id);
    ResponseEntity<WineResponseRest> updateWineById(String id,Wine wine);
}
