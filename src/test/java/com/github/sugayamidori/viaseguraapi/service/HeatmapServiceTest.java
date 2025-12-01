package com.github.sugayamidori.viaseguraapi.service;

import com.github.sugayamidori.viaseguraapi.controller.dto.H3CoordinatesDTO;
import com.github.sugayamidori.viaseguraapi.controller.dto.HeatmapDTO;
import com.github.sugayamidori.viaseguraapi.controller.dto.HeatmapWithCoordinatesDTO;
import com.github.sugayamidori.viaseguraapi.controller.mappers.H3CoordinatesMapper;
import com.github.sugayamidori.viaseguraapi.controller.mappers.HeatmapMapper;
import com.github.sugayamidori.viaseguraapi.model.H3Coordinates;
import com.github.sugayamidori.viaseguraapi.model.Heatmap;
import com.github.sugayamidori.viaseguraapi.repository.HeatmapRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HeatmapServiceTest {

    @Mock
    private HeatmapRepository repository;

    @Mock
    private H3CoordinatesService h3CoordinatesService;

    @Mock
    private HeatmapMapper mapper;

    @Mock
    private H3CoordinatesMapper h3CoordinatesMapper;

    @InjectMocks
    private HeatmapService service;

    private Heatmap heatmap1;
    private Heatmap heatmap2;
    private H3Coordinates h3Coordinates1;
    private H3Coordinates h3Coordinates2;
    private HeatmapDTO heatmapDTO1;
    private HeatmapDTO heatmapDTO2;
    private H3CoordinatesDTO h3CoordinatesDTO1;
    private H3CoordinatesDTO h3CoordinatesDTO2;

    @BeforeEach
    void setUp() {
        heatmap1 = new Heatmap();
        heatmap1.setId(UUID.randomUUID());
        heatmap1.setH3Cell("898180208d3ffff");
        heatmap1.setYear(2024);
        heatmap1.setMonth(11);
        heatmap1.setNumCasualties(BigDecimal.valueOf(5));
        heatmap1.setCreatedAt(LocalDateTime.now());

        heatmap2 = new Heatmap();
        heatmap2.setId(UUID.randomUUID());
        heatmap2.setH3Cell("898180208d4aaaa");
        heatmap2.setYear(2024);
        heatmap2.setMonth(10);
        heatmap2.setNumCasualties(BigDecimal.valueOf(3));
        heatmap2.setCreatedAt(LocalDateTime.now());

        h3Coordinates1 = new H3Coordinates();
        h3Coordinates1.setId(UUID.randomUUID());
        h3Coordinates1.setH3Cell("898180208d3ffff");
        h3Coordinates1.setLatitude(BigDecimal.valueOf(-35.2357829));
        h3Coordinates1.setLongitude(BigDecimal.valueOf(-8.3586413));
        h3Coordinates1.setNeighborhood("nova descoberta");
        h3Coordinates1.setCreatedAt(LocalDateTime.now());

        h3Coordinates2 = new H3Coordinates();
        h3Coordinates2.setId(UUID.randomUUID());
        h3Coordinates2.setH3Cell("898180208d4aaaa");
        h3Coordinates2.setLatitude(BigDecimal.valueOf(-35.2357900));
        h3Coordinates2.setLongitude(BigDecimal.valueOf(-8.3586500));
        h3Coordinates2.setNeighborhood("boa viagem");
        h3Coordinates2.setCreatedAt(LocalDateTime.now());

        heatmapDTO1 = new HeatmapDTO(heatmap1.getH3Cell(),
                heatmap1.getYear(), heatmap1.getMonth(),
                heatmap1.getNumCasualties(), heatmap1.getCreatedAt());

        heatmapDTO2 = new HeatmapDTO(heatmap2.getH3Cell(),
                heatmap2.getYear(), heatmap2.getMonth(),
                heatmap2.getNumCasualties(), heatmap2.getCreatedAt());

        h3CoordinatesDTO1 = new H3CoordinatesDTO(h3Coordinates1.getH3Cell(),
                h3Coordinates1.getLatitude(), h3Coordinates1.getLongitude(),
                h3Coordinates1.getNeighborhood(), h3Coordinates1.getCreatedAt());

        h3CoordinatesDTO2 = new H3CoordinatesDTO(h3Coordinates2.getH3Cell(),
                h3Coordinates2.getLatitude(), h3Coordinates2.getLongitude(),
                h3Coordinates2.getNeighborhood(), h3Coordinates2.getCreatedAt());
    }

    // ========== TESTES DO MÉTODO SEARCHWITHCOORDINATES ==========

    @Test
    void searchWithCoordinates_shouldReturnPageWithCoordinates_whenHeatmapsFound() {
        Page<Heatmap> heatmapPage = new PageImpl<>(List.of(heatmap1, heatmap2));

        Map<String, List<H3Coordinates>> coordinatesMap = Map.of(
                "898180208d3ffff", List.of(h3Coordinates1),
                "898180208d4aaaa", List.of(h3Coordinates2)
        );

        when(repository.findAll(ArgumentMatchers.<Specification<Heatmap>>any(), any(Pageable.class)))
                .thenReturn(heatmapPage);
        when(h3CoordinatesService.findByH3CellsToHeatmap(anyList()))
                .thenReturn(coordinatesMap);
        when(mapper.toDTO(heatmap1)).thenReturn(heatmapDTO1);
        when(mapper.toDTO(heatmap2)).thenReturn(heatmapDTO2);
        when(h3CoordinatesMapper.toDTO(List.of(h3Coordinates1)))
                .thenReturn(List.of(h3CoordinatesDTO1));
        when(h3CoordinatesMapper.toDTO(List.of(h3Coordinates2)))
                .thenReturn(List.of(h3CoordinatesDTO2));

        Page<HeatmapWithCoordinatesDTO> result = service.searchWithCoordinates(
                null, null, null, null,
                null, null, null, 0, 10
        );

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(heatmapDTO1, result.getContent().get(0).heatmap());
        assertEquals(heatmapDTO2, result.getContent().get(1).heatmap());
        assertNotNull(result.getContent().get(0).coordinates());
        assertNotNull(result.getContent().get(1).coordinates());
        assertEquals(1, result.getContent().get(0).coordinates().size());
        assertEquals(1, result.getContent().get(1).coordinates().size());

        verify(repository).findAll(ArgumentMatchers.<Specification<Heatmap>>any(), any(Pageable.class));
        verify(h3CoordinatesService).findByH3CellsToHeatmap(anyList());
        verify(mapper, times(2)).toDTO(any(Heatmap.class));
        verify(h3CoordinatesMapper, times(2)).toDTO(anyList());
    }

    @Test
    void searchWithCoordinates_shouldReturnEmptyPage_whenNoHeatmapsFound() {
        Page<Heatmap> emptyPage = Page.empty(PageRequest.of(0, 10));
        when(repository.findAll(ArgumentMatchers.<Specification<Heatmap>>any(), any(Pageable.class)))
                .thenReturn(emptyPage);

        Page<HeatmapWithCoordinatesDTO> result = service.searchWithCoordinates(
                null, null, null, null,
                null,null, null, 0, 10
        );

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());

        verify(repository).findAll(ArgumentMatchers.<Specification<Heatmap>>any(), any(Pageable.class));
        verify(h3CoordinatesService, never()).findByH3CellsToHeatmap(anyList());
        verify(mapper, never()).toDTO(any(Heatmap.class));
        verify(h3CoordinatesMapper, never()).toDTO(anyList());
    }

    @Test
    void searchWithCoordinates_shouldHandleHeatmapWithoutCoordinates() {
        Page<Heatmap> heatmapPage = new PageImpl<>(List.of(heatmap1));
        Map<String, List<H3Coordinates>> emptyCoordinatesMap = Map.of();

        when(repository.findAll(ArgumentMatchers.<Specification<Heatmap>>any(), any(Pageable.class)))
                .thenReturn(heatmapPage);
        when(h3CoordinatesService.findByH3CellsToHeatmap(anyList()))
                .thenReturn(emptyCoordinatesMap);
        when(mapper.toDTO(heatmap1)).thenReturn(heatmapDTO1);
        when(h3CoordinatesMapper.toDTO(Collections.emptyList()))
                .thenReturn(Collections.emptyList());

        Page<HeatmapWithCoordinatesDTO> result = service.searchWithCoordinates(
                null, null, null, null,
                null, null, null,0, 10
        );

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(heatmapDTO1, result.getContent().getFirst().heatmap());
        assertTrue(result.getContent().getFirst().coordinates().isEmpty());

        verify(repository).findAll(ArgumentMatchers.<Specification<Heatmap>>any(), any(Pageable.class));
        verify(h3CoordinatesService).findByH3CellsToHeatmap(anyList());
        verify(mapper).toDTO(heatmap1);
        verify(h3CoordinatesMapper).toDTO(Collections.emptyList());
    }

    @Test
    void searchWithCoordinates_shouldFilterByH3Cell() {
        String h3Cell = "898180208d3ffff";
        Page<Heatmap> heatmapPage = new PageImpl<>(List.of(heatmap1));
        Map<String, List<H3Coordinates>> coordinatesMap = Map.of(
                "898180208d3ffff", List.of(h3Coordinates1)
        );

        when(repository.findAll(ArgumentMatchers.<Specification<Heatmap>>any(), any(Pageable.class)))
                .thenReturn(heatmapPage);
        when(h3CoordinatesService.findByH3CellsToHeatmap(anyList()))
                .thenReturn(coordinatesMap);
        when(mapper.toDTO(heatmap1)).thenReturn(heatmapDTO1);
        when(h3CoordinatesMapper.toDTO(List.of(h3Coordinates1)))
                .thenReturn(List.of(h3CoordinatesDTO1));

        Page<HeatmapWithCoordinatesDTO> result = service.searchWithCoordinates(
                h3Cell, null, null, null,
                null, null, null, 0, 10
        );

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(h3Cell, result.getContent().getFirst().heatmap().h3Cell());

        verify(repository).findAll(ArgumentMatchers.<Specification<Heatmap>>any(), any(Pageable.class));
    }

    @Test
    void searchWithCoordinates_shouldFilterByYear() {
        Integer year = 2024;
        Page<Heatmap> heatmapPage = new PageImpl<>(List.of(heatmap1));
        Map<String, List<H3Coordinates>> coordinatesMap = Map.of(
                "898180208d3ffff", List.of(h3Coordinates1)
        );

        when(repository.findAll(ArgumentMatchers.<Specification<Heatmap>>any(), any(Pageable.class)))
                .thenReturn(heatmapPage);
        when(h3CoordinatesService.findByH3CellsToHeatmap(anyList()))
                .thenReturn(coordinatesMap);
        when(mapper.toDTO(heatmap1)).thenReturn(heatmapDTO1);
        when(h3CoordinatesMapper.toDTO(anyList()))
                .thenReturn(List.of(h3CoordinatesDTO1));

        Page<HeatmapWithCoordinatesDTO> result = service.searchWithCoordinates(
                null, year, null, null,
                null,null, null, 0, 10
        );

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(year, result.getContent().getFirst().heatmap().year());

        verify(repository).findAll(ArgumentMatchers.<Specification<Heatmap>>any(), any(Pageable.class));
    }

    @Test
    void searchWithCoordinates_shouldFilterByMonth() {
        Integer month = 11;
        Page<Heatmap> heatmapPage = new PageImpl<>(List.of(heatmap1));
        Map<String, List<H3Coordinates>> coordinatesMap = Map.of(
                "898180208d3ffff", List.of(h3Coordinates1)
        );

        when(repository.findAll(ArgumentMatchers.<Specification<Heatmap>>any(), any(Pageable.class)))
                .thenReturn(heatmapPage);
        when(h3CoordinatesService.findByH3CellsToHeatmap(anyList()))
                .thenReturn(coordinatesMap);
        when(mapper.toDTO(heatmap1)).thenReturn(heatmapDTO1);
        when(h3CoordinatesMapper.toDTO(anyList()))
                .thenReturn(List.of(h3CoordinatesDTO1));

        Page<HeatmapWithCoordinatesDTO> result = service.searchWithCoordinates(
                null, null, month, null,
                null, null, null, 0, 10
        );

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(month, result.getContent().getFirst().heatmap().month());

        verify(repository).findAll(ArgumentMatchers.<Specification<Heatmap>>any(), any(Pageable.class));
    }

    @Test
    void searchWithCoordinates_shouldFilterByNumCasualties() {
        BigDecimal numCasualties = BigDecimal.valueOf(5);
        Page<Heatmap> heatmapPage = new PageImpl<>(List.of(heatmap1));
        Map<String, List<H3Coordinates>> coordinatesMap = Map.of(
                "898180208d3ffff", List.of(h3Coordinates1)
        );

        when(repository.findAll(ArgumentMatchers.<Specification<Heatmap>>any(), any(Pageable.class)))
                .thenReturn(heatmapPage);
        when(h3CoordinatesService.findByH3CellsToHeatmap(anyList()))
                .thenReturn(coordinatesMap);
        when(mapper.toDTO(heatmap1)).thenReturn(heatmapDTO1);
        when(h3CoordinatesMapper.toDTO(anyList()))
                .thenReturn(List.of(h3CoordinatesDTO1));

        Page<HeatmapWithCoordinatesDTO> result = service.searchWithCoordinates(
                null, null, null, null,
                null, numCasualties, null, 0, 10
        );

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(numCasualties, result.getContent().getFirst().heatmap().numCasualties());

        verify(repository).findAll(ArgumentMatchers.<Specification<Heatmap>>any(), any(Pageable.class));
    }

    @Test
    void searchWithCoordinates_shouldApplyAllFilters() {
        String h3Cell = "898180208d3ffff";
        Integer year = 2024;
        Integer month = 11;
        BigDecimal numCasualties = BigDecimal.valueOf(5);
        Page<Heatmap> heatmapPage = new PageImpl<>(List.of(heatmap1));
        Map<String, List<H3Coordinates>> coordinatesMap = Map.of(
                "898180208d3ffff", List.of(h3Coordinates1)
        );

        when(repository.findAll(ArgumentMatchers.<Specification<Heatmap>>any(), any(Pageable.class)))
                .thenReturn(heatmapPage);
        when(h3CoordinatesService.findByH3CellsToHeatmap(anyList()))
                .thenReturn(coordinatesMap);
        when(mapper.toDTO(heatmap1)).thenReturn(heatmapDTO1);
        when(h3CoordinatesMapper.toDTO(anyList()))
                .thenReturn(List.of(h3CoordinatesDTO1));

        Page<HeatmapWithCoordinatesDTO> result = service.searchWithCoordinates(
                h3Cell, year, month, null,
                null, numCasualties, null, 0, 10
        );

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        HeatmapDTO resultHeatmap = result.getContent().getFirst().heatmap();
        assertEquals(h3Cell, resultHeatmap.h3Cell());
        assertEquals(year, resultHeatmap.year());
        assertEquals(month, resultHeatmap.month());
        assertEquals(numCasualties, resultHeatmap.numCasualties());

        verify(repository).findAll(ArgumentMatchers.<Specification<Heatmap>>any(), any(Pageable.class));
    }

    @Test
    void searchWithCoordinates_shouldRespectPagination() {
        Page<Heatmap> heatmapPage = new PageImpl<>(
                List.of(heatmap1),
                PageRequest.of(1, 5),
                10
        );
        Map<String, List<H3Coordinates>> coordinatesMap = Map.of(
                "898180208d3ffff", List.of(h3Coordinates1)
        );

        when(repository.findAll(ArgumentMatchers.<Specification<Heatmap>>any(), any(Pageable.class)))
                .thenReturn(heatmapPage);
        when(h3CoordinatesService.findByH3CellsToHeatmap(anyList()))
                .thenReturn(coordinatesMap);
        when(mapper.toDTO(heatmap1)).thenReturn(heatmapDTO1);
        when(h3CoordinatesMapper.toDTO(anyList()))
                .thenReturn(List.of(h3CoordinatesDTO1));

        Page<HeatmapWithCoordinatesDTO> result = service.searchWithCoordinates(
                null, null, null, null,
                null, null, null, 1, 5
        );

        assertNotNull(result);
        assertEquals(1, result.getNumber());
        assertEquals(5, result.getSize());
        assertEquals(10, result.getTotalElements());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findAll(ArgumentMatchers.<Specification<Heatmap>>any(), pageableCaptor.capture());
        assertEquals(1, pageableCaptor.getValue().getPageNumber());
        assertEquals(5, pageableCaptor.getValue().getPageSize());
    }

    @Test
    void searchWithCoordinates_shouldHandleMultipleCoordinatesPerH3Cell() {
        H3Coordinates h3Coordinates3 = new H3Coordinates();
        h3Coordinates3.setId(UUID.randomUUID());
        h3Coordinates3.setH3Cell("898180208d3ffff");
        h3Coordinates3.setLatitude(BigDecimal.valueOf(-35.2358000));
        h3Coordinates3.setLongitude(BigDecimal.valueOf(-8.3586600));
        h3Coordinates3.setNeighborhood("nova descoberta");

        Page<Heatmap> heatmapPage = new PageImpl<>(List.of(heatmap1));
        Map<String, List<H3Coordinates>> coordinatesMap = Map.of(
                "898180208d3ffff", List.of(h3Coordinates1, h3Coordinates3)
        );

        H3CoordinatesDTO h3CoordinatesDTO3 = new H3CoordinatesDTO(h3Coordinates3.getH3Cell(),
                h3Coordinates3.getLatitude(), h3Coordinates3.getLongitude(),
                h3Coordinates3.getNeighborhood(), h3Coordinates3.getCreatedAt());

        when(repository.findAll(ArgumentMatchers.<Specification<Heatmap>>any(), any(Pageable.class)))
                .thenReturn(heatmapPage);
        when(h3CoordinatesService.findByH3CellsToHeatmap(anyList()))
                .thenReturn(coordinatesMap);
        when(mapper.toDTO(heatmap1)).thenReturn(heatmapDTO1);
        when(h3CoordinatesMapper.toDTO(List.of(h3Coordinates1, h3Coordinates3)))
                .thenReturn(List.of(h3CoordinatesDTO1, h3CoordinatesDTO3));

        Page<HeatmapWithCoordinatesDTO> result = service.searchWithCoordinates(
                null, null, null, null,
                null, null, null, 0, 10
        );

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(2, result.getContent().getFirst().coordinates().size());

        verify(h3CoordinatesService).findByH3CellsToHeatmap(anyList());
    }

    @Test
    void searchWithCoordinates_shouldHandleDistinctH3Cells() {
        Heatmap heatmap3 = new Heatmap();
        heatmap3.setId(UUID.randomUUID());
        heatmap3.setH3Cell("898180208d3ffff");

        Page<Heatmap> heatmapPage = new PageImpl<>(List.of(heatmap1, heatmap3));
        Map<String, List<H3Coordinates>> coordinatesMap = Map.of(
                "898180208d3ffff", List.of(h3Coordinates1)
        );

        when(repository.findAll(ArgumentMatchers.<Specification<Heatmap>>any(), any(Pageable.class)))
                .thenReturn(heatmapPage);
        when(h3CoordinatesService.findByH3CellsToHeatmap(anyList()))
                .thenReturn(coordinatesMap);
        when(mapper.toDTO(any(Heatmap.class))).thenReturn(heatmapDTO1);
        when(h3CoordinatesMapper.toDTO(anyList()))
                .thenReturn(List.of(h3CoordinatesDTO1));

        Page<HeatmapWithCoordinatesDTO> result = service.searchWithCoordinates(
                null, null, null, null,
                null, null, null, 0, 10
        );

        assertNotNull(result);
        assertEquals(2, result.getContent().size());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> h3CellsCaptor =
                (ArgumentCaptor<List<String>>) (ArgumentCaptor<?>) ArgumentCaptor.forClass(List.class);
        verify(h3CoordinatesService).findByH3CellsToHeatmap(h3CellsCaptor.capture());

        List<String> capturedH3Cells = h3CellsCaptor.getValue();
        assertEquals(1, capturedH3Cells.size());
        assertEquals("898180208d3ffff", capturedH3Cells.getFirst());
    }

    @Test
    void searchWithCoordinates_shouldIgnoreBlankH3Cell() {
        Page<Heatmap> heatmapPage = new PageImpl<>(List.of(heatmap1));
        Map<String, List<H3Coordinates>> coordinatesMap = Map.of(
                "898180208d3ffff", List.of(h3Coordinates1)
        );

        when(repository.findAll(ArgumentMatchers.<Specification<Heatmap>>any(), any(Pageable.class)))
                .thenReturn(heatmapPage);
        when(h3CoordinatesService.findByH3CellsToHeatmap(anyList()))
                .thenReturn(coordinatesMap);
        when(mapper.toDTO(heatmap1)).thenReturn(heatmapDTO1);
        when(h3CoordinatesMapper.toDTO(anyList()))
                .thenReturn(List.of(h3CoordinatesDTO1));

        Page<HeatmapWithCoordinatesDTO> result = service.searchWithCoordinates(
                "   ", null, null, null,
                null, null, null, 0, 10
        );

        assertNotNull(result);
        assertEquals(1, result.getContent().size());

        verify(repository).findAll(ArgumentMatchers.<Specification<Heatmap>>any(), any(Pageable.class));
    }
}