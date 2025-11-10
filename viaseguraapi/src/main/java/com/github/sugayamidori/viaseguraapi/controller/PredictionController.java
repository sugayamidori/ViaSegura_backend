package com.github.sugayamidori.viaseguraapi.controller;

import com.github.sugayamidori.viaseguraapi.controller.docs.PredictionControllerDocs;
import com.github.sugayamidori.viaseguraapi.controller.dto.PredictionDTO;
import com.github.sugayamidori.viaseguraapi.controller.mappers.PredictionMapper;
import com.github.sugayamidori.viaseguraapi.model.Prediction;
import com.github.sugayamidori.viaseguraapi.service.PredictionService;
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
@RequestMapping("predictions")
@RequiredArgsConstructor
@Tag(name = "Prediction", description = "Endpoints for managing predictions")
public class PredictionController implements PredictionControllerDocs {

    private final PredictionService service;
    private final PredictionMapper mapper;

    @GetMapping
    @Override
    public ResponseEntity<Page<PredictionDTO>> search(
            @RequestParam(value = "h3Cell", required = false)
            String h3Cell,
            @RequestParam(value = "weekStart", required = false)
            String weekStart,
            @RequestParam(value = "predictedAccidents", required = false)
            BigDecimal predictedAccidents,
            @RequestParam(value = "pagina", defaultValue = "0")
            Integer pagina,
            @RequestParam(value = "tamanhoPagina", defaultValue = "20")
            Integer tamanhoPagina

    ) {
        Page<Prediction> paginaResultado = service.search(h3Cell, weekStart, predictedAccidents, pagina, tamanhoPagina);

        Page<PredictionDTO> resultado = paginaResultado.map(mapper::toDTO);

        return ResponseEntity.ok(resultado);
    }

}
