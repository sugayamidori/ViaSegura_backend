package com.github.sugayamidori.viaseguraapi.service;

import com.github.sugayamidori.viaseguraapi.model.Prediction;
import com.github.sugayamidori.viaseguraapi.repository.PredictionRepository;
import com.github.sugayamidori.viaseguraapi.service.PredictionService;
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
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PredictionServiceTest {

    @Mock
    private PredictionRepository repository;

    @InjectMocks
    private PredictionService service;

    private Prediction prediction1;
    private Prediction prediction2;

    @BeforeEach
    void setUp() {
        prediction1 = new Prediction();
        prediction1.setId(UUID.randomUUID());
        prediction1.setH3Cell("898180208d3ffff");
        prediction1.setWeekStart(new Date());
        prediction1.setPredictedAccidents(BigDecimal.valueOf(0.0009153104641940453));
        prediction1.setCreatedAt(LocalDateTime.now());

        prediction2 = new Prediction();
        prediction2.setId(UUID.randomUUID());
        prediction2.setH3Cell("89818065923ffff");
        prediction2.setWeekStart(new Date());
        prediction2.setPredictedAccidents(BigDecimal.valueOf(1.2305097036787915));
        prediction2.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void search_shouldReturnAllPredictions_whenNoFiltersProvided() {

        Page<Prediction> expectedPage = new PageImpl<>(List.of(prediction1, prediction2));

        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<Prediction> result =
                service.search("", null, null, 0, 10);

        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        verify(repository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void search_shouldFilterByH3Cell_whenH3CellProvided() {
        String h3Cell = "898180208d3ffff";
        Page<Prediction> expectedPage = new PageImpl<>(List.of(prediction1));

        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<Prediction> result =
                service.search(h3Cell, null, null, 0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        assertEquals(h3Cell, result.getContent().getFirst().getH3Cell());
        verify(repository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void search_shouldFilterByWeekStart_whenWeekStartProvided() {
        Date weekStart = new Date();
        Page<Prediction> expectedPage = new PageImpl<>(List.of(prediction1, prediction2));

        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<Prediction> result =
                service.search("", weekStart.toString(), null, 0, 10);

        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(weekStart.toString(),
                result.getContent().getFirst().getWeekStart().toString());
        verify(repository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void search_shouldFilterByPredictedAccidents_whenPredictedAccidentsProvided() {
        BigDecimal predicted = BigDecimal.valueOf(1.2305097036787915);
        Page<Prediction> expectedPage = new PageImpl<>(List.of(prediction2));

        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<Prediction> result =
                service.search("", null, predicted, 0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        assertEquals(predicted, result.getContent().getFirst().getPredictedAccidents());
        verify(repository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void search_shouldApplyMultipleFilters_whenAllParametersProvided() {
        String h3Cell = "898180208d3ffff";
        Date weekStart = new Date();
        BigDecimal predicted = BigDecimal.valueOf(0.0009153104641940453);
        Page<Prediction> expectedPage = new PageImpl<>(List.of(prediction1));
        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<Prediction> result =
                service.search(h3Cell, weekStart.toString(), predicted, 0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        assertEquals(h3Cell, result.getContent().getFirst().getH3Cell());
        assertEquals(weekStart.toString(), result.getContent().getFirst().getWeekStart().toString());
        assertEquals(predicted, result.getContent().getFirst().getPredictedAccidents());
        verify(repository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void search_shouldRespectPagination_whenPageAndPageSizeProvided() {

        Page<Prediction> expectedPage = new PageImpl<>(
                List.of(prediction1),
                PageRequest.of(1, 5),
                10
        );

        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<Prediction> result =
                service.search("", null, null, 1, 5);

        assertEquals(1, result.getNumber()); // página atual
        assertEquals(5, result.getSize());   // tamanho da página
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findAll(any(Specification.class), pageableCaptor.capture());
        assertEquals(1, pageableCaptor.getValue().getPageNumber());
        assertEquals(5, pageableCaptor.getValue().getPageSize());
    }

    @Test
    void search_shouldReturnEmptyPage_whenNoResultsFound() {
        Page<Prediction> emptyPage = new PageImpl<>(List.of());

        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        Page<Prediction> result =
                service.search("nonexistent", null, null, 0, 10);

        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(repository).findAll(any(Specification.class), any(Pageable.class));
    }
}