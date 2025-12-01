package com.github.sugayamidori.viaseguraapi.controller.dto;

import java.util.List;

public record HeatmapWithCoordinatesDTO(
        HeatmapDTO heatmap,
        List<H3CoordinatesDTO> coordinates) {
}
