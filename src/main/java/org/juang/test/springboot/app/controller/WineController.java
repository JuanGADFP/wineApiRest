package org.juang.test.springboot.app.controller;

import org.juang.test.springboot.app.models.Owner;
import org.juang.test.springboot.app.models.Wine;
import org.juang.test.springboot.app.response.WineResponseRest;
import org.juang.test.springboot.app.service.WineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class WineController {

    @Autowired
    private WineService wineService;

    @GetMapping("/wines/")
    public ResponseEntity<List<Wine>> getAllWines() {
        ResponseEntity<List<Wine>> wines = wineService.getAllWines();
        return ResponseEntity.ok(wines.getBody());
    }

    @GetMapping("/wine/{id}")
    public ResponseEntity<Wine> getWineById(@PathVariable Long id) {
        Wine wine = wineService.getWineById(id).getBody();
        return wine != null ? ResponseEntity.ok(wine) : ResponseEntity.notFound().build();
    }

    @PostMapping("/wine/")
    public ResponseEntity<WineResponseRest> save(@RequestBody Wine wine) {
        return wineService.save(wine);
    }

    @PutMapping("/wine/{id}")
    public ResponseEntity<WineResponseRest> updateWineById(@PathVariable Long id,@RequestBody Wine wine) {
        return wineService.updateWineById(id, wine);
    }

    @DeleteMapping("/wine/{id}")
    public ResponseEntity<WineResponseRest> deleteWineById(@PathVariable Long id) {
        return wineService.deleteWineById(id);
    }
}
