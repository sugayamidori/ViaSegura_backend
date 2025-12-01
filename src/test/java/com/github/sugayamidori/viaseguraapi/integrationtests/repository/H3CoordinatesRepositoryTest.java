package com.github.sugayamidori.viaseguraapi.integrationtests.repository;

import com.github.sugayamidori.viaseguraapi.integrationtests.testcontainers.RepositoryIntegrationTest;
import com.github.sugayamidori.viaseguraapi.model.H3Coordinates;
import com.github.sugayamidori.viaseguraapi.integrationtests.testcontainers.PostgresTestContainer;
import com.github.sugayamidori.viaseguraapi.repository.H3CoordinatesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RepositoryIntegrationTest
class H3CoordinatesRepositoryTest implements PostgresTestContainer {

    @Autowired
    private H3CoordinatesRepository repository;

    @BeforeEach
    void setUp() {
        insertTestData();
    }

    private void insertTestData() {
        List<H3Coordinates> coords = List.of(
                createCoord("89818020e87ffff",
                        "nova descoberta",
                        -8.365732984307769, -35.227026530899465),
                createCoord("8981806592fffff",
                        "boa viagem",
                        -8.146246992227887,
                        -34.913916635213134)
        );
        repository.saveAll(coords);
    }

    private H3Coordinates createCoord(String h3Cell, String neighborhood,
                                      double lat, double lng) {
        H3Coordinates coord = new H3Coordinates();
        coord.setH3Cell(h3Cell);
        coord.setNeighborhood(neighborhood);
        coord.setLatitude(BigDecimal.valueOf(lat));
        coord.setLongitude(BigDecimal.valueOf(lng));
        return coord;
    }
    @Test
    void findByH3CellIn() {
        List<H3Coordinates> result = repository.findByH3CellIn(
                List.of("89818020e87ffff", "8981806592fffff")
        );

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    @Test
    void findDistinctNeighborhoods() {
        List<String> neighborhoods = repository.findDistinctNeighborhoods();

        assertNotNull(neighborhoods);
        assertFalse(neighborhoods.isEmpty());
        assertEquals(2, neighborhoods.size());
    }

    @Test
    void deveRetornarVazioQuandoNaoEncontrar() {
        List<H3Coordinates> result = repository.findByH3CellIn(
                List.of("CELL_INEXISTENTE")
        );

        assertTrue(result.isEmpty());
    }
}