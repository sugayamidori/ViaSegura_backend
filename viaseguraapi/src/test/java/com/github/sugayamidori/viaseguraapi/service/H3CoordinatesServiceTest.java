package com.github.sugayamidori.viaseguraapi.service;

import com.github.sugayamidori.viaseguraapi.model.H3Coordinates;
import com.github.sugayamidori.viaseguraapi.repository.H3CoordinatesRepository;
import com.github.sugayamidori.viaseguraapi.service.H3CoordinatesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class H3CoordinatesServiceTest {

    @Mock
    private H3CoordinatesRepository repository;

    @InjectMocks
    private H3CoordinatesService service;

    private H3Coordinates h3Coordinates1;
    private H3Coordinates h3Coordinates2;

    @BeforeEach
    void setUp() {
        h3Coordinates1 = new H3Coordinates();
        h3Coordinates1.setId(UUID.randomUUID());
        h3Coordinates1.setH3Cell("898180208d3ffff");
        h3Coordinates1.setLongitude(BigDecimal.valueOf(-8.3586413));
        h3Coordinates1.setLatitude(BigDecimal.valueOf(-35.2357829));
        h3Coordinates1.setNeighborhood("nova descoberta");
        h3Coordinates1.setCreatedAt(LocalDateTime.now());

        h3Coordinates2 = new H3Coordinates();
        h3Coordinates2.setId(UUID.randomUUID());
        h3Coordinates2.setH3Cell("89818065923ffff");
        h3Coordinates2.setLongitude(BigDecimal.valueOf(-8.148732706670044));
        h3Coordinates2.setLatitude(BigDecimal.valueOf(-34.91251065250479));
        h3Coordinates2.setNeighborhood("boa viagem");
        h3Coordinates2.setCreatedAt(LocalDateTime.now());
    }

    // ========== TESTES DO MÉTODO SEARCH ==========

    @Test
    void search_shouldReturnAllCoordinates_whenNoFiltersProvided() {

        Page<H3Coordinates> expectedPage = new PageImpl<>(List.of(h3Coordinates1, h3Coordinates2));

        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<H3Coordinates> result = service.search("", null, null, null, 0, 10);

        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        verify(repository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void search_shouldFilterByH3Cell_whenH3CellProvided() {
        String h3Cell = "898180208d3ffff";
        Page<H3Coordinates> expectedPage = new PageImpl<>(List.of(h3Coordinates1));

        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<H3Coordinates> result = service.search(h3Cell, null, null, null, 0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        assertEquals(h3Cell, result.getContent().getFirst().getH3Cell());
        verify(repository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void search_shouldFilterByLatitude_whenLatitudeProvided() {
        BigDecimal latitude = BigDecimal.valueOf(-35.2357829);
        Page<H3Coordinates> expectedPage = new PageImpl<>(List.of(h3Coordinates1));

        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<H3Coordinates> result = service.search("", latitude, null, null, 0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        assertEquals(latitude, result.getContent().getFirst().getLatitude());
        verify(repository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void search_shouldFilterByLongitude_whenLongitudeProvided() {
        BigDecimal longitude = BigDecimal.valueOf(-8.3586413);
        Page<H3Coordinates> expectedPage = new PageImpl<>(List.of(h3Coordinates1));

        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<H3Coordinates> result = service.search("", null, longitude, null, 0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        assertEquals(longitude, result.getContent().getFirst().getLongitude());
        verify(repository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void search_shouldFilterByNeighborhood_whenNeighborhoodProvided() {
        String neighborhood = "nova descoberta";
        Page<H3Coordinates> expectedPage = new PageImpl<>(List.of(h3Coordinates1));

        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<H3Coordinates> result = service.search("", null, null, neighborhood, 0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        assertEquals(neighborhood, result.getContent().getFirst().getNeighborhood());
        verify(repository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void search_shouldApplyMultipleFilters_whenAllParametersProvided() {
        String h3Cell = "898180208d3ffff";
        BigDecimal latitude = BigDecimal.valueOf(-35.2357829);
        BigDecimal longitude = BigDecimal.valueOf(-8.3586413);
        String neighborhood = "nova descoberta";
        Page<H3Coordinates> expectedPage = new PageImpl<>(List.of(h3Coordinates1));
        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<H3Coordinates> result = service.search(h3Cell, latitude, longitude, neighborhood, 0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        assertEquals(h3Cell, result.getContent().getFirst().getH3Cell());
        assertEquals(latitude, result.getContent().getFirst().getLatitude());
        assertEquals(longitude, result.getContent().getFirst().getLongitude());
        assertEquals(neighborhood, result.getContent().getFirst().getNeighborhood());
        verify(repository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void search_shouldRespectPagination_whenPageAndPageSizeProvided() {

        Page<H3Coordinates> expectedPage = new PageImpl<>(
                List.of(h3Coordinates1),
                PageRequest.of(1, 5),
                10
        );

        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<H3Coordinates> result = service.search("", null, null, null, 1, 5);

        assertEquals(1, result.getNumber()); // página atual
        assertEquals(5, result.getSize());   // tamanho da página
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findAll(any(Specification.class), pageableCaptor.capture());
        assertEquals(1, pageableCaptor.getValue().getPageNumber());
        assertEquals(5, pageableCaptor.getValue().getPageSize());
    }

    @Test
    void search_shouldReturnEmptyPage_whenNoResultsFound() {
        Page<H3Coordinates> emptyPage = new PageImpl<>(List.of());

        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        Page<H3Coordinates> result = service.search("nonexistent", null, null, null, 0, 10);

        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(repository).findAll(any(Specification.class), any(Pageable.class));
    }

    // ========== TESTES DO MÉTODO FINDBYH3CELLSTOHEATMAP ==========

    @Test
    void findByH3CellsToHeatmap_shouldGroupCoordinatesByH3Cell() {
        H3Coordinates coord1 = new H3Coordinates();
        coord1.setH3Cell("898180208d3ffff");
        coord1.setNeighborhood("nova descoberta");

        H3Coordinates coord2 = new H3Coordinates();
        coord2.setH3Cell("898180208d3ffff");
        coord2.setNeighborhood("nova descoberta");

        H3Coordinates coord3 = new H3Coordinates();
        coord3.setH3Cell("898180208d4aaaa");
        coord3.setNeighborhood("recife");

        List<String> h3Cells = List.of("898180208d3ffff", "898180208d4aaaa");

        when(repository.findByH3CellIn(h3Cells))
                .thenReturn(List.of(coord1, coord2, coord3));

        Map<String, List<H3Coordinates>> result = service.findByH3CellsToHeatmap(h3Cells);

        assertEquals(2, result.size());
        assertEquals(2, result.get("898180208d3ffff").size());
        assertEquals(1, result.get("898180208d4aaaa").size());
        verify(repository).findByH3CellIn(h3Cells);
    }

    @Test
    void findByH3CellsToHeatmap_shouldReturnEmptyMap_whenNoCoordinatesFound() {
        List<String> h3Cells = List.of("nonexistent1", "nonexistent2");
        when(repository.findByH3CellIn(h3Cells)).thenReturn(List.of());

        Map<String, List<H3Coordinates>> result = service.findByH3CellsToHeatmap(h3Cells);

        assertTrue(result.isEmpty());
        verify(repository).findByH3CellIn(h3Cells);
    }

    @Test
    void findByH3CellsToHeatmap_shouldHandleEmptyH3CellsList() {
        List<String> emptyList = List.of();
        when(repository.findByH3CellIn(emptyList)).thenReturn(List.of());

        Map<String, List<H3Coordinates>> result = service.findByH3CellsToHeatmap(emptyList);

        assertTrue(result.isEmpty());
        verify(repository).findByH3CellIn(emptyList);
    }

    @Test
    void findByH3CellsToHeatmap_shouldHandleSingleH3Cell() {
        H3Coordinates coord = new H3Coordinates();
        coord.setH3Cell("898180208d3ffff");
        coord.setNeighborhood("nova descoberta");

        List<String> h3Cells = List.of("898180208d3ffff");
        when(repository.findByH3CellIn(h3Cells)).thenReturn(List.of(coord));

        Map<String, List<H3Coordinates>> result = service.findByH3CellsToHeatmap(h3Cells);

        assertEquals(1, result.size());
        assertEquals(1, result.get("898180208d3ffff").size());
        assertEquals("nova descoberta", result.get("898180208d3ffff").getFirst().getNeighborhood());
        verify(repository).findByH3CellIn(h3Cells);
    }
}