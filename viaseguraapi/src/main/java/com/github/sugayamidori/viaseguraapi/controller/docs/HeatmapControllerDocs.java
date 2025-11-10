package com.github.sugayamidori.viaseguraapi.controller.docs;

import com.github.sugayamidori.viaseguraapi.controller.dto.H3CoordinatesDTO;
import com.github.sugayamidori.viaseguraapi.controller.dto.HeatmapDTO;
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

public interface HeatmapControllerDocs {

    @Operation(summary = "Search for the Heatmap",
            description = "Search for the Heatmap",
            tags = "Heatmap",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = HeatmapDTO.class))
                            )),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content),
            })
    ResponseEntity<Page<HeatmapDTO>> search(
            @RequestParam(value = "h3Cell", required = false)
            String h3Cell,
            @RequestParam(value = "year", required = false)
            Integer year,
            @RequestParam(value = "month", required = false)
            Integer month,
            @RequestParam(value = "numSinistros", required = false)
            BigDecimal numSinistros,
            @RequestParam(value = "pagina", defaultValue = "0")
            Integer pagina,
            @RequestParam(value = "tamanhoPagina", defaultValue = "20")
            Integer tamanhoPagina

    );
}
