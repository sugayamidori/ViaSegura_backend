package com.github.sugayamidori.viaseguraapi.controller.docs;

import com.github.sugayamidori.viaseguraapi.controller.dto.ErrorResponse;
import com.github.sugayamidori.viaseguraapi.controller.dto.H3CoordinatesDTO;
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
import java.util.List;

public interface H3CoordinatesControllerDocs {

    @Operation(summary = "Search for H3 Coordinates",
            description = "Search for H3 Coordinates",
            tags = "H3 Coordinates",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = H3CoordinatesDTO.class))
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
                    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )),
            })
    ResponseEntity<Page<H3CoordinatesDTO>> search(@RequestParam(value = "h3Cell", required = false)
                                                  String h3Cell,
                                                  @RequestParam(value = "latitude", required = false)
                                                  BigDecimal latitude,
                                                  @RequestParam(value = "longitude", required = false)
                                                  BigDecimal longitude,
                                                  @RequestParam(value = "neighborhood", required = false)
                                                  String neighborhood,
                                                  @RequestParam(value = "page", defaultValue = "0")
                                                  Integer page,
                                                  @RequestParam(value = "pageSize", defaultValue = "300")
                                                  Integer pageSize);

    @Operation(summary = "Search for all neighborhoods",
            description = "Get all different neighborhoods",
            tags = "H3 Coordinates",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = List.class))
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
                    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )),
            })
    ResponseEntity<List<String>> getAllNeighborhoods();
}
