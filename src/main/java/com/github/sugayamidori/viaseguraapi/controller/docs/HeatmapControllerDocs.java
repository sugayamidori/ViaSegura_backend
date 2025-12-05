package com.github.sugayamidori.viaseguraapi.controller.docs;

import com.github.sugayamidori.viaseguraapi.controller.dto.ErrorResponse;
import com.github.sugayamidori.viaseguraapi.controller.dto.FileBase64DTO;
import com.github.sugayamidori.viaseguraapi.controller.dto.HeatmapDTO;
import com.github.sugayamidori.viaseguraapi.controller.dto.HeatmapWithCoordinatesDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Tag(name = "Heatmap", description = "Endpoints for querying and exporting accident heatmap data")
public interface HeatmapControllerDocs {

    @Operation(
            summary = "Search heatmaps with filters",
            description = """
                    Performs paginated search of heatmap data with support for multiple filters.
                    
                    **Available filters:**
                    - Location (H3 cell or neighborhood)
                    - Period (start and end year/month)
                    - Minimum number of casualties
                    
                    **Notes:**
                    - All filters are optional and can be combined
                    - Default pagination returns 300 records per page
                    - Results include geographic coordinates
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Search completed successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = HeatmapDTO.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid parameters (e.g., incorrect date format, negative values)",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "User not authenticated - missing or invalid token",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access denied - user without permission to view data",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<Page<HeatmapWithCoordinatesDTO>> search(
            @Parameter(
                    description = "H3 cell identifier for geographic location filtering",
                    example = "89818065923ffff"
            )
            @RequestParam(value = "h3Cell", required = false) String h3Cell,

            @Parameter(
                    description = "Start year of the search period (format: YYYY)",
                    example = "2023"
            )
            @RequestParam(value = "start_year", required = false) Integer startYear,

            @Parameter(
                    description = "Start month of the search period (1-12)",
                    example = "1",
                    schema = @Schema(minimum = "1", maximum = "12")
            )
            @RequestParam(value = "start_month", required = false) Integer startMonth,

            @Parameter(
                    description = "End year of the search period (format: YYYY)",
                    example = "2024"
            )
            @RequestParam(value = "end_year", required = false) Integer endYear,

            @Parameter(
                    description = "End month of the search period (1-12)",
                    example = "12",
                    schema = @Schema(minimum = "1", maximum = "12")
            )
            @RequestParam(value = "end_month", required = false) Integer endMonth,

            @Parameter(
                    description = "Minimum number of casualties to filter accidents"
            )
            @RequestParam(value = "num_casualties", required = false) BigDecimal numCasualties,

            @Parameter(
                    description = "Neighborhood name for location filtering",
                    example = "boa viagem"
            )
            @RequestParam(value = "neighborhood", required = false) String neighborhood,

            @Parameter(
                    description = "Page number (starts at 0)",
                    example = "0",
                    schema = @Schema(minimum = "0")
            )
            @RequestParam(value = "page", defaultValue = "0") Integer page,

            @Parameter(
                    description = "Number of records per page",
                    example = "300",
                    schema = @Schema(minimum = "1")
            )
            @RequestParam(value = "pageSize", defaultValue = "300") Integer pageSize
    );

    @Operation(
            summary = "Export filtered data in Excel format",
            description = """
                    Exports heatmap data applying the specified filters.
                    
                    **Export format:**
                    - Excel file (.xlsx)
                    - Base64 encoded
                    - Includes all available columns
                    
                    **Available filters:**
                    - Period (start and end year/month)
                    - Minimum number of casualties
                    - Neighborhood
                    
                    **Export limit:** It is recommended not to export more than 50,000 records at once.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "File exported successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = FileBase64DTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid parameters or dataset too large for export",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "User not authenticated - missing or invalid token",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access denied - user without permission to export data",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal error during file generation",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<FileBase64DTO> exportData(
            @Parameter(
                    description = "Start year of the export period (format: YYYY)",
                    example = "2020"
            )
            @RequestParam(value = "start_year", required = false) Integer startYear,

            @Parameter(
                    description = "Start month of the export period (1-12)",
                    example = "1",
                    schema = @Schema(minimum = "1", maximum = "12")
            )
            @RequestParam(value = "start_month", required = false) Integer startMonth,

            @Parameter(
                    description = "End year of the export period (format: YYYY)",
                    example = "2024"
            )
            @RequestParam(value = "end_year", required = false) Integer endYear,

            @Parameter(
                    description = "End month of the export period (1-12)",
                    example = "12",
                    schema = @Schema(minimum = "1", maximum = "12")
            )
            @RequestParam(value = "end_month", required = false) Integer endMonth,

            @Parameter(
                    description = "Minimum number of casualties to filter accidents"
            )
            @RequestParam(value = "num_casualties", required = false) BigDecimal numCasualties,

            @Parameter(
                    description = "Neighborhood name for location filtering",
                    example = "Boa Viagem"
            )
            @RequestParam(value = "neighborhood", required = false) String neighborhood
    );
}
