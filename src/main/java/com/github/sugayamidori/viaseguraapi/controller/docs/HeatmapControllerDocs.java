package com.github.sugayamidori.viaseguraapi.controller.docs;

import com.github.sugayamidori.viaseguraapi.controller.dto.ErrorResponse;
import com.github.sugayamidori.viaseguraapi.controller.dto.FileBase64DTO;
import com.github.sugayamidori.viaseguraapi.controller.dto.HeatmapDTO;
import com.github.sugayamidori.viaseguraapi.controller.dto.HeatmapWithCoordinatesDTO;
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

    @Operation(summary = "Search for the Heatmaps",
            description = "Search for the Heatmaps",
            tags = "Heatmap",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = HeatmapDTO.class))
                            )),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            })
    ResponseEntity<Page<HeatmapWithCoordinatesDTO>> search(
            @RequestParam(value = "h3Cell", required = false) String h3Cell,
            @RequestParam(value = "start_year", required = false) Integer startYear,
            @RequestParam(value = "start_month", required = false) Integer startMonth,
            @RequestParam(value = "end_year", required = false) Integer endYear,
            @RequestParam(value = "end_month", required = false) Integer endMonth,
            @RequestParam(value = "num_casualties", required = false) BigDecimal numCasualties,
            @RequestParam(value = "neighborhood", required = false) String neighborhood,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "150") Integer pageSize

    );

    @Operation(summary = "Export data by filter",
            description = "Export data in base64 with xls",
            tags = "Heatmap",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = FileBase64DTO.class)
                            )),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            })
    public ResponseEntity<FileBase64DTO> exportData(
            @RequestParam(value = "start_year", required = false) Integer startYear,
            @RequestParam(value = "start_month", required = false) Integer startMonth,
            @RequestParam(value = "end_year", required = false) Integer endYear,
            @RequestParam(value = "end_month", required = false) Integer endMonth,
            @RequestParam(value = "num_casualties", required = false) BigDecimal numCasualties,
            @RequestParam(value = "neighborhood", required = false) String neighborhood

    );
}
