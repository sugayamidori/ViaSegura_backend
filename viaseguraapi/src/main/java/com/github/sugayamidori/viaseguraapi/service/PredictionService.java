package com.github.sugayamidori.viaseguraapi.service;

import com.github.sugayamidori.viaseguraapi.model.Prediction;
import com.github.sugayamidori.viaseguraapi.repository.PredictionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.github.sugayamidori.viaseguraapi.repository.specs.PredictionSpecs.*;


@Service
@RequiredArgsConstructor
public class PredictionService {

    private final PredictionRepository repository;

    public Page<Prediction> search(
            String h3Cell,
            String weekStart,
            BigDecimal predictedAccidents,
            Integer page,
            Integer pageSize
    ) {

        Specification<Prediction> specs = (root, query, cb) -> cb.conjunction();

        if(!h3Cell.isBlank()) {
            specs = specs.and(h3CellEquals(h3Cell));
        }

        if(weekStart != null) {
            specs = specs.and(weekStartEquals(weekStart));
        }

        if(predictedAccidents != null) {
            specs = specs.and(predictedAccidentsEquals(predictedAccidents));
        }

        Pageable pageRequest = PageRequest.of(page, pageSize);

        return repository.findAll(specs, pageRequest);
    }
}
