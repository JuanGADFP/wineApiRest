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
    public ResponseEntity<Wine> getWineById(Long id) {
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
        if (owners == null) {
            response.setMetadata("Response Status BAD_REQUEST", "400", "All Wines must have a owner");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        for (Owner owner : owners) {
            if (owner.getName() == null || owner.getName().isEmpty()) {
                response.setMetadata("Response Status BAD_REQUEST", "400", "Invalid name field in Owner");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            if (owner.getApellido() == null || owner.getApellido().isEmpty()) {
                response.setMetadata("Response Status BAD_REQUEST", "400", "Invalid apellido field in Owner");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            /*
            if (owner.getId() != null) {
                response.setMetadata("Response Status BAD_REQUEST", "400", "Invalid ID field in Owner");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
             */
        }
        // Saving the Wine entity to the database
        try {
            // Saving the Wine entity to the database
            List<Wine> list = new ArrayList<>();
            Wine wineGuardado = wineRepository.save(wine);

            // Creating the response with the saved Wine entity
            if (wineGuardado != null) {
                list.add(wineGuardado);
                response.getWineResponse().setWine(list);
                response.setMetadata("Response Status OK", "200", "Successfully saved wine in database");

                // Saving the Owners entities related to the Wine entity
                for (Owner owner : owners) {
                    owner.setWine(wineGuardado); // Establecemos la relación con la entidad Wine que acabamos de guardar
                    ownerRepository.save(owner); // Guardamos el objeto Owner en la base de datos
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
            response.setMetadata("Response Status INTERNAL_SERVER_ERROR", "500", "INTERNAL_SERVER_ERROR");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @Override
    public ResponseEntity<WineResponseRest> deleteWineById(Long id) {
        WineResponseRest response = new WineResponseRest();
        List<Wine> list = new ArrayList<>();
        try {
            Optional<Wine> wineBuscado = wineRepository.findById(id);

            if (wineBuscado.isPresent()) {
                list.add(wineBuscado.get());

                // Eliminar los Owners relacionados con el Wine a borrar
                List<Owner> owners = wineBuscado.get().getOwners();
                for (Owner owner : owners) {
                    owner.setWine(null);
                    ownerRepository.save(owner);
                }

                response.getWineResponse().setWine(list);
                wineRepository.deleteById(id);
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
    public ResponseEntity<WineResponseRest> updateWineById(Long id, Wine wineRequest) {
        WineResponseRest response = new WineResponseRest();
        List<Wine> list = new ArrayList<>();
        try {

            Optional<Wine> wineBuscado = wineRepository.findById(id);

            if (wineBuscado.isPresent()) {

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

                Wine wine = wineBuscado.get();
                wine.setName(wineRequest.getName());
                wine.setWinery(wineRequest.getWinery());
                wine.setAño(wineRequest.getAño());
                // Actualizar la relación con los Owners
                //List<Owner> owners = wine.getOwners();
                List<Owner> ownersRequest = wineRequest.getOwners();

                    /* Crear mapa para asociar los ids de los Owners con los objetos Owner
                Map<Long, Owner> ownerMap = new HashMap<>();
                for (Owner owner : owners) {
                    ownerMap.put(owner.getId(), owner);
                }
              */

                for (Owner owner : ownersRequest) {
                        if (owner.getName() == null || owner.getName().isEmpty()) {
                            response.setMetadata("Response Status BAD_REQUEST", "400", "Invalid name field in Owner");
                            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                        }
                        if (owner.getApellido() == null || owner.getApellido().isEmpty()) {
                            response.setMetadata("Response Status BAD_REQUEST", "400", "Invalid apellido field in Owner");
                            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                        }
                        owner.setName(owner.getName());
                        owner.setApellido(owner.getApellido());
                        owner.setWine(wine);
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

/*
    @Override
    public ResponseEntity<WineResponseRest> updateWineById(Long id, Wine wine) {
        WineResponseRest response = new WineResponseRest();
        List<Wine> list = new ArrayList<>();

        try {
            Optional<Wine> wineBuscado = wineRepository.findById(id);

            if (wineBuscado.isPresent()) {
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
                    response.setMetadata("Response Status BAD_REQUEST", "400", "Invalid year field : field year must be present and greater than 1601 or the current year");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
                List<Owner> owners = wine.getOwners();
                for (Owner owner : owners) {
                    if (owner.getName() == null || owner.getName().isEmpty()) {
                        response.setMetadata("Response Status BAD_REQUEST", "400", "Invalid name field in Owner");
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                    if (owner.getApellido() == null || owner.getApellido().isEmpty()) {
                        response.setMetadata("Response Status BAD_REQUEST", "400", "Invalid apellido field in Owner");
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                    if (owner.getId() == null) {
                        response.setMetadata("Response Status BAD_REQUEST", "400", "Field ID must be send to modify Owner");
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                }

                wineBuscado.get();
                wine.setName(wine.getName());
                wine.setWinery(wine.getWinery());
                wine.setAño(wine.getAño());
                Wine wineGuardado = wineRepository.save(wine);


                // Actualizar los Owners relacionados con el Wine

                for (Owner owner : owners) {
                    owner.setWine(wineGuardado);
                    ownerRepository.save(owner);
                }

                list.add(wine);
                response.getWineResponse().setWine(list);
                response.setMetadata("Response Status Ok", "200", "Successfully Response");
            } else {
                response.setMetadata("Response Status NOT_FOUND","404" , "Could not find wine id");
                return new ResponseEntity<WineResponseRest>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMetadata("Response Status INTERNAL_SERVER_ERROR","500" , "INTERNAL_SERVER_ERROR");
            return new ResponseEntity<WineResponseRest>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<WineResponseRest>(response, HttpStatus.OK);
    }

 */