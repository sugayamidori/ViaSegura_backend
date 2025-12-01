package com.github.sugayamidori.viaseguraapi.controller.mappers;

import com.github.sugayamidori.viaseguraapi.controller.dto.HeatmapDTO;
import com.github.sugayamidori.viaseguraapi.model.Heatmap;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HeatmapMapper {

    Heatmap toEntity(HeatmapDTO dto);

    HeatmapDTO toDTO(Heatmap heatmap);
}
