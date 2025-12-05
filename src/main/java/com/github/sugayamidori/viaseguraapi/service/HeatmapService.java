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

import java.io.IOException;
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
    private final ExcelExportService excelExportService;

    public Page<HeatmapWithCoordinatesDTO> searchWithCoordinates(
            String h3Cell,
            Integer startYear,
            Integer startMonth,
            Integer endYear,
            Integer endMonth,
            BigDecimal numCasualties,
            String neighborhood,
            Integer page,
            Integer pageSize
    ) {
        Page<Heatmap> heatmaps = search(h3Cell, startYear, startMonth, endYear, endMonth, numCasualties, neighborhood, page, pageSize);

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
            String neighborhood,
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

        if(neighborhood != null && !neighborhood.isBlank()) {
            specs = specs.and(hasH3CoordinatesInNeighborhood(neighborhood));
        }

        specs = configSpecsToPeriod(startYear, startMonth, endYear, endMonth, neighborhood, specs);

        Pageable pageRequest = PageRequest.of(page, pageSize);

        return repository.findAll(specs, pageRequest);
    }

    public String exportDataAsBase64(
            Integer startYear,
            Integer startMonth,
            Integer endYear,
            Integer endMonth,
            BigDecimal numCasualties,
            String neighborhood
    ) throws IOException {
        List<HeatmapWithCoordinatesDTO> content = configureBase64(
                startYear, startMonth, endYear, endMonth, numCasualties, neighborhood
        );

        return excelExportService.generateBase64Excel(content);
    }

    private List<HeatmapWithCoordinatesDTO> configureBase64(
            Integer startYear,
            Integer startMonth,
            Integer endYear,
            Integer endMonth,
            BigDecimal numCasualties,
            String neighborhood
            ) {
        List<Heatmap> heatmaps = search(startYear, startMonth, endYear, endMonth, numCasualties, neighborhood);

        List<String> h3Cells = heatmaps
                .stream()
                .map(Heatmap::getH3Cell)
                .distinct()
                .toList();

        Map<String, List<H3Coordinates>> coordinatesByCell =
                h3CoordinatesService.findByH3CellsToHeatmap(h3Cells);

        return heatmaps
                .stream()
                .map(heatmap -> new HeatmapWithCoordinatesDTO(
                        mapper.toDTO(heatmap),
                        h3CoordinatesMapper.toDTO(
                                coordinatesByCell.getOrDefault(heatmap.getH3Cell(), Collections.emptyList())
                        )
                ))
                .toList();
    }

    private List<Heatmap> search(
            Integer startYear,
            Integer startMonth,
            Integer endYear,
            Integer endMonth,
            BigDecimal numCasualties,
            String neighborhood
    ) {

        Specification<Heatmap> specs = (root, query, cb) -> cb.conjunction();

        if(numCasualties != null) {
            specs = specs.and(numCasualtiesEquals(numCasualties));
        }

        specs = configSpecsToPeriod(startYear, startMonth, endYear, endMonth, neighborhood, specs);

        return repository.findAll(specs);
    }

    private static Specification<Heatmap> configSpecsToPeriod(Integer startYear, Integer startMonth, Integer endYear, Integer endMonth, String neighborhood, Specification<Heatmap> specs) {
        Specification<Heatmap> groupStart = Specification.allOf();
        Specification<Heatmap> groupEnd = Specification.allOf();

        if(startYear != null) {
            groupStart = groupStart.and(startYearGreatEquals(startYear));
        }

        if(startMonth != null) {
            groupStart = groupStart.and(startMonthGreatEquals(startMonth));
        }

        if(endYear != null) {
            groupEnd = groupEnd.and(endYearLessEquals(endYear));
        }

        if(endMonth != null) {
            groupEnd = groupEnd.and(endMonthLessEquals(endMonth));
        }

        if(neighborhood != null && !neighborhood.isBlank()) {
            specs = specs.and(hasH3CoordinatesInNeighborhood(neighborhood));
        }

        specs = specs.and(groupStart.and(groupEnd));
        return specs;
    }
}