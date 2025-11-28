package com.github.sugayamidori.viaseguraapi.controller;

import com.github.sugayamidori.viaseguraapi.controller.docs.HeatmapControllerDocs;
import com.github.sugayamidori.viaseguraapi.controller.dto.HeatmapWithCoordinatesDTO;
import com.github.sugayamidori.viaseguraapi.service.HeatmapService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("heatmap")
@RequiredArgsConstructor
@Tag(name = "Heatmap", description = "Endpoints for managing heatmap")
public class HeatmapController implements HeatmapControllerDocs {

    private final HeatmapService service;

    @GetMapping
    @Override
    public ResponseEntity<Page<HeatmapWithCoordinatesDTO>> search(
            @RequestParam(value = "h3Cell", required = false) String h3Cell,
            @RequestParam(value = "start_year", required = false) Integer startYear,
            @RequestParam(value = "start_month", required = false) Integer startMonth,
            @RequestParam(value = "end_year", required = false) Integer endYear,
            @RequestParam(value = "end_month", required = false) Integer endMonth,
            @RequestParam(value = "num_casualties", required = false) BigDecimal numCasualties,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        Page<HeatmapWithCoordinatesDTO> result = service.searchWithCoordinates(
                h3Cell, startYear, startMonth, endYear, endMonth, numCasualties, page, pageSize
        );

        return ResponseEntity.ok(result);
    }

}
