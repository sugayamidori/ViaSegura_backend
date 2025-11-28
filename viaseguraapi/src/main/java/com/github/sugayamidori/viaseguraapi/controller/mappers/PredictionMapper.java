package com.github.sugayamidori.viaseguraapi.controller.mappers;

import com.github.sugayamidori.viaseguraapi.controller.dto.PredictionDTO;
import com.github.sugayamidori.viaseguraapi.model.Prediction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PredictionMapper {

    Prediction toEntity(PredictionDTO dto);

    PredictionDTO toDTO(Prediction heatmap);
}
