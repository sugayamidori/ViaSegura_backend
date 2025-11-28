package com.github.sugayamidori.viaseguraapi.service;

import com.github.sugayamidori.viaseguraapi.controller.dto.HeatmapWithCoordinatesDTO;
import com.github.sugayamidori.viaseguraapi.controller.mappers.H3CoordinatesMapper;
import com.github.sugayamidori.viaseguraapi.controller.mappers.HeatmapMapper;
import com.github.sugayamidori.viaseguraapi.model.H3Coordinates;
import com.github.sugayamidori.viaseguraapi.model.Heatmap;
import com.github.sugayamidori.viaseguraapi.repository.HeatmapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.github.sugayamidori.viaseguraapi.repository.specs.HeatmapSpecs.*;

@Service
@RequiredArgsConstructor
public class HeatmapService {

    private final HeatmapRepository repository;
    private final H3CoordinatesService h3CoordinatesService;
    private final HeatmapMapper mapper;
    private final H3CoordinatesMapper h3CoordinatesMapper;

    public Page<HeatmapWithCoordinatesDTO> searchWithCoordinates(
            String h3Cell,
            Integer startYear,
            Integer startMonth,
            Integer endYear,
            Integer endMonth,
            BigDecimal numCasualties,
            Integer page,
            Integer pageSize
    ) {
        Page<Heatmap> heatmaps = search(h3Cell, startYear, startMonth, endYear, endMonth, numCasualties, page, pageSize);

        if (heatmaps.isEmpty()) {
            return Page.empty(heatmaps.getPageable());
        }

        List<String> h3Cells = heatmaps.getContent()
                .stream()
                .map(Heatmap::getH3Cell)
                .distinct()
                .toList();

        Map<String, List<H3Coordinates>> coordinatesByCell =
                h3CoordinatesService.findByH3CellsToHeatmap(h3Cells);

        List<HeatmapWithCoordinatesDTO> content = heatmaps.getContent()
                .stream()
                .map(heatmap -> new HeatmapWithCoordinatesDTO(
                        mapper.toDTO(heatmap),
                        h3CoordinatesMapper.toDTO(
                                coordinatesByCell.getOrDefault(heatmap.getH3Cell(), Collections.emptyList())
                        )
                ))
                .toList();

        return new PageImpl<>(content, heatmaps.getPageable(), heatmaps.getTotalElements());
    }

    private Page<Heatmap> search(
            String h3Cell,
            Integer startYear,
            Integer startMonth,
            Integer endYear,
            Integer endMonth,
            BigDecimal numCasualties,
            Integer page,
            Integer pageSize
    ) {

        Specification<Heatmap> specs = (root, query, cb) -> cb.conjunction();

        if(h3Cell != null && !h3Cell.isBlank()) {
            specs = specs.and(h3CellEquals(h3Cell));
        }

        if(numCasualties != null) {
            specs = specs.and(numCasualtiesEquals(numCasualties));
        }

        Specification<Heatmap> groupStart = Specification.allOf();
        Specification<Heatmap> groupEnd = Specification.allOf();

        if(startYear != null) {
            groupStart = groupStart.and(startYearEquals(startYear));
        }

        if(startMonth != null) {
            groupStart = groupStart.and(startMonthEquals(startMonth));
        }

        if(endYear != null) {
            groupEnd = groupEnd.and(endYearEquals(endYear));
        }

        if(endMonth != null) {
            groupEnd = groupEnd.and(endMonthEquals(endMonth));
        }

        specs = specs.and(groupStart.and(groupEnd));

        Pageable pageRequest = PageRequest.of(page, pageSize);

        return repository.findAll(specs, pageRequest);
    }
}
