package org.juang.test.springboot.app;


import java.util.*;


import org.juang.test.springboot.app.models.Owner;
import org.juang.test.springboot.app.models.Wine;

import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import javax.validation.constraints.Null;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;




import java.util.ArrayList;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WineControllerTestSpringWebTest {

	@Autowired
	private WebTestClient client;


	@Test
	@Order(1)
	@DisplayName("Prueba GET.*")
	void getWines() {
		client.get().uri("/v1/wines/")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$[0].name").isEqualTo("Espumante, CASA BOHER")
				.jsonPath("$[0].winery").isEqualTo("CASA BOHER")
				.jsonPath("$[0].año").isEqualTo(2015);

	}

	@Test
	@Order(2)
	@DisplayName("Prueba GET/{id}")
	void consultarPorId() {
		client.get().uri("/v1/wine/1")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.name").isEqualTo("Espumante, CASA BOHER")
				.jsonPath("$.winery").isEqualTo("CASA BOHER")
				.jsonPath("$.año").isEqualTo(2015)
				.jsonPath("$.owners[0].id").isEqualTo(1)
				.jsonPath("$.owners[0].name").isEqualTo("Juan")
				.jsonPath("$.owners[0].apellido").isEqualTo("Perez");

		client.get().uri("/v1/wine/999")
				.exchange()
				.expectStatus().isNotFound().expectBody().isEmpty();

	}

    @Test
    @Order(3)
    @DisplayName("Prueba POST")
    void crear() {
		List<Owner> owners = new ArrayList<>();
		owners.add(new Owner("Marcos", "John Smith", 1L));

		// Agregar más objetos Owner a la lista si es necesario

		Wine wine = new Wine(null, "Espumante de Prueba PUT name", "otro Put winery", 2000, owners);



        client.post().uri("/v1/wine/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(wine)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.wineResponse.wine[0].name").isEqualTo("Espumante de Prueba PUT name")
                .jsonPath("$.wineResponse.wine[0].winery").isEqualTo("otro Put winery")
                .jsonPath("$.wineResponse.wine[0].año").isEqualTo(2000)
                .jsonPath("$.wineResponse.wine[0].id").isEqualTo(2)
				.jsonPath("$.wineResponse.wine[0].owners[0].id").isEqualTo(1L)
				.jsonPath("$.wineResponse.wine[0].owners[0].name").isEqualTo("Marcos")
				.jsonPath("$.wineResponse.wine[0].owners[0].apellido").isEqualTo("John Smith");

    }



	@Test
	@Order(4)
	@DisplayName("Prueba GET despues de Postear en base")
	void testAfterPOST() {
		client.get().uri("/v1/wine/2")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.name").isEqualTo("Espumante de Prueba PUT name")
				.jsonPath("$.winery").isEqualTo("otro Put winery")
				.jsonPath("$.año").isEqualTo(2000)
				.jsonPath("$.id").isEqualTo(2)
				.jsonPath("$.owners[0].id").isEqualTo(1L)
				.jsonPath("$.owners[0].name").isEqualTo("Marcos")
				.jsonPath("$.owners[0].apellido").isEqualTo("John Smith");

		client.get().uri("/v1/wines/")
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$").value(hasSize(2));
	}

	@Test
	@Order(5)
	@DisplayName("Probando metodo PUT")
	void actualizar() {
		List<Owner> owners = new ArrayList<>();
		owners.add(new Owner("Marcos", "John Smith", 1L));

		// Agregar más objetos Owner a la lista si es necesario

		Wine wine = new Wine(null, "Espumante de Prueba PUT name", "otro Put winery", 2000, owners);
       // Aquí creas un nuevo objeto Wine sin un ID específico y con la lista de owners creada anteriormente

		client.put().uri("/v1/wine/2")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.wineResponse.wine[0].name").isEqualTo("PUT name")
				.jsonPath("$.wineResponse.wine[0].winery").isEqualTo("winery")
				.jsonPath("$.wineResponse.wine[0].año").isEqualTo(2010)
				.jsonPath("$.wineResponse.wine[0].id").isEqualTo(2)
				.jsonPath("$.wineResponse.wine[0].owners[0].id").isEqualTo(1L)
				.jsonPath("$.wineResponse.wine[0].owners[0].name").isEqualTo("2")
				.jsonPath("$.wineResponse.wine[0].owners[0].apellido").isEqualTo("h")
				.jsonPath("$.wineResponse.wine[0].owners[1].id").isEqualTo(2L)
				.jsonPath("$.wineResponse.wine[0].owners[1].name").isEqualTo("3")
				.jsonPath("$.wineResponse.wine[0].owners[1].apellido").isEqualTo("e");

		// wine2 = new Wine(null, null, null, 0);
		client.put().uri("/v1/wine/999")
				.exchange()
				.expectStatus().isNotFound()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.metadata[0].tipo").isEqualTo("Response Status NOT_FOUND")
				.jsonPath("$.metadata[0].codigo").isEqualTo("404")
				.jsonPath("$.metadata[0].dato").isEqualTo("Could not consult wine id");

	}




	@Test
	@Order(6)
	@DisplayName("Probando metodo DELETE")
	void eliminar() {
		client.get().uri("/v1/wines/")
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$").value(hasSize(2));

		client.delete().uri("/v1/wine/2")
				.exchange()
				.expectStatus().isOk();

		client.get().uri("/v1/wine/2")
				.exchange()
				.expectStatus().isNotFound().expectBody().isEmpty();

		client.get().uri("/v1/wines/")
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$").value(hasSize(1));

	}
}