package org.juang.test.springboot.app.service;

import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.juang.test.springboot.app.models.Owner;
import org.juang.test.springboot.app.models.Wine;
import org.juang.test.springboot.app.repository.OwnerRepository;
import org.juang.test.springboot.app.repository.WineRepository;
import org.juang.test.springboot.app.response.WineResponseRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotEmpty;

@Service
public class WineServiceImpl implements WineService {

    @Autowired
    private WineRepository wineRepository;

    @Autowired
    private OwnerRepository ownerRepository;


    public WineServiceImpl() {
    }

    public ResponseEntity<List<Wine>> getAllWines() {
        List<Wine> wines = wineRepository.findAll();
        return new ResponseEntity<>(wines, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Wine> getWineById(String id) {
        Optional<Wine> optionalWine = wineRepository.findById(id);
        if (optionalWine.isPresent()) {
            Wine wine = optionalWine.get();
            return new ResponseEntity<>(wine, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<WineResponseRest> save(Wine wine) {
        WineResponseRest response = new WineResponseRest();
        try {
        if (wine.getId() != null) {
            response.setMetadata("Response Status BAD_REQUEST", "400", "ID field should not be sent");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (wine.getName() == null) {
            response.setMetadata("Response Status BAD_REQUEST", "400", "Invalid name field");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (wine.getWinery() == null) {
            response.setMetadata("Response Status BAD_REQUEST", "400", "Invalid winery field");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (wine.getAño() == 0 || wine.getAño() <= 1601 || wine.getAño() >=2023) {
            response.setMetadata("Response Status BAD_REQUEST", "400", "Invalid year field : year field has to be present and greater than year 1601 but less than current year");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        List<Owner> owners = wine.getOwners();

        if (owners == null || owners.isEmpty()) {
                response.setMetadata("Response Status BAD_REQUEST", "400", "All Wines must have a owner");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        List<Wine> list = new ArrayList<>();

        for (Owner owner : owners) {
            if (owner.getName() == null || owner.getName().isEmpty()) {
                response.setMetadata("Response Status BAD_REQUEST", "400", "Invalid name field in Owner");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            if (owner.getApellido() == null || owner.getApellido().isEmpty()) {
                response.setMetadata("Response Status BAD_REQUEST", "400", "Invalid apellido field in Owner");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            if (owner.getId() == null){
                response.setMetadata("Response Status BAD_REQUEST", "400", "the id must be sent to create a new one or to use an existing one");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            //owner.setWine(wineGuardado); // Establecemos la relación con la entidad Wine que acabamos de guardar
            wineRepository.save(wine);
            ownerRepository.save(owner); // Guardamos el objeto Owner en la base de datos
        }
            if (wine != null) {
                list.add(wine);
                response.getWineResponse().setWine(list);
                response.setMetadata("Response Status OK", "200", "Successfully saved wine in database");
            }
        } catch (Exception e) {
            e.getStackTrace();
            response.setMetadata("Response Status INTERNAL_SERVER_ERROR", "500", "INTERNAL_SERVER_ERROR");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @Override
    public ResponseEntity<WineResponseRest> deleteWineById(String id) {
        WineResponseRest response = new WineResponseRest();
        List<Wine> list = new ArrayList<>();
        try {
            Optional<Wine> wineBuscado = wineRepository.findById(id);
            if (wineBuscado.isPresent()) {
                list.add(wineBuscado.get());
                wineRepository.deleteById(id);
                // Eliminar los Owners relacionados con el Wine a borrar
                List<Owner> owners = wineBuscado.get().getOwners();
                for (Owner owner : owners) {
                    ownerRepository.deleteById(owner.getId());
                }
                response.getWineResponse().setWine(list);
                response.setMetadata("Response Status Ok", "200", "Successfully Response");
            } else {
                response.setMetadata("Response Status NOT_FOUND","404" , "Could not delete wine id");
                return new ResponseEntity<WineResponseRest>(response, HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            e.getStackTrace();
            response.setMetadata("Response Status INTERNAL_SERVER_ERROR","500" , "INTERNAL_SERVER_ERROR");

            return new ResponseEntity<WineResponseRest>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<WineResponseRest>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<WineResponseRest> updateWineById(String id, Wine wineRequest) {
        WineResponseRest response = new WineResponseRest();
        List<Wine> list = new ArrayList<>();
        try {

            Optional<Wine> wineBuscado = wineRepository.findById(id);

            if (wineBuscado.isPresent()) {
                Wine wine = wineBuscado.get();
                if (wineRequest.getName() == null) {
                    response.setMetadata("Response Status BAD_REQUEST", "400", "Invalid name field");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
                if (wineRequest.getWinery() == null) {
                    response.setMetadata("Response Status BAD_REQUEST", "400", "Invalid winery field");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
                if (wineRequest.getAño() == 0 || wineRequest.getAño() <= 1601 || wineRequest.getAño() >=2023) {
                    response.setMetadata("Response Status BAD_REQUEST", "400", "Invalid year field : field year must be present and greater than 1601 and less than the current year");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
                if (wineRequest.getId() != null) {
                    response.setMetadata("Response Status BAD_REQUEST", "400", "ID field should not be sent");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }

                List<Owner> owners = wine.getOwners();

                if (owners == null || owners.isEmpty()) {
                    response.setMetadata("Response Status BAD_REQUEST", "400", "All Wines must have a owner");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }

                wine.setName(wineRequest.getName());
                wine.setWinery(wineRequest.getWinery());
                wine.setAño(wineRequest.getAño());
                // Actualizar la relación con los Owners

                List<Owner> ownersRequest = wineRequest.getOwners();

                for (Owner owner : ownersRequest) {
                        if (owner.getName() == null || owner.getApellido() == null) {
                            response.setMetadata("Response Status BAD_REQUEST", "400", "Invalid name/apellido field in Owner");
                            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                        }
                        if (owner.getId() == null){
                          response.setMetadata("Response Status BAD_REQUEST", "400", "the id must be sent to create a new one or to use an existing one");
                          return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                        }
                        owner.setId(owner.getId());
                        owner.setName(owner.getName());
                        owner.setApellido(owner.getApellido());
                       // owner.setWine(wine);
                    ownerRepository.save(owner);
                }
                    wineRepository.save(wine);
                    list.add(wine);
                    response.getWineResponse().setWine(list);
                    response.setMetadata("Response Status Ok", "200", "Successfully Response");
            } else {
                response.setMetadata("Response Status NOT_FOUND", "404", "Could not update wine id");
                return new ResponseEntity<WineResponseRest>(response, HttpStatus.NOT_FOUND);
            }
        } catch(Exception e){
            e.getStackTrace();
            response.setMetadata("Response Status INTERNAL_SERVER_ERROR", "500", "INTERNAL_SERVER_ERROR");
            return new ResponseEntity<WineResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<WineResponseRest>(response, HttpStatus.OK);
    }

}