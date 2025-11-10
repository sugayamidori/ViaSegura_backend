package com.github.sugayamidori.viaseguraapi.controller.docs;

import com.github.sugayamidori.viaseguraapi.controller.dto.HeatmapDTO;
import com.github.sugayamidori.viaseguraapi.controller.dto.PredictionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

public interface PredictionControllerDocs {

    @Operation(summary = "Search for the Predictions",
            description = "Search for the Predictions",
            tags = "Prediction",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = PredictionDTO.class))
                            )),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content),
            })
    ResponseEntity<Page<PredictionDTO>> search(
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

    );
}
