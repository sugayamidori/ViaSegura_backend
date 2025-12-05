package com.github.sugayamidori.viaseguraapi.controller.docs;

import com.github.sugayamidori.viaseguraapi.controller.dto.ErrorResponse;
import com.github.sugayamidori.viaseguraapi.controller.dto.H3CoordinatesDTO;
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
import java.util.List;

@Tag(name = "H3 Coordinates", description = "Endpoints for managing H3 geospatial coordinates and neighborhood data")
public interface H3CoordinatesControllerDocs {

    @Operation(
            summary = "Search H3 coordinates with filters",
            description = """
                    Performs paginated search of H3 coordinate data with support for multiple filters.
                    
                    **Available filters:**
                    - H3 cell identifier
                    - Geographic coordinates (latitude/longitude)
                    - Neighborhood name
                    
                    **Notes:**
                    - All filters are optional and can be combined
                    - Default pagination returns 300 records per page
                    - Latitude ranges from -90 to 90
                    - Longitude ranges from -180 to 180
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Search completed successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = H3CoordinatesDTO.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid parameters (e.g., invalid coordinates, malformed H3 cell)",
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
                            responseCode = "404",
                            description = "No coordinates found matching the specified criteria",
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
    ResponseEntity<Page<H3CoordinatesDTO>> search(
            @Parameter(
                    description = "H3 cell identifier for geospatial location",
                    example = "89818065923ffff"
            )
            @RequestParam(value = "h3Cell", required = false) String h3Cell,

            @Parameter(
                    description = "Latitude coordinate in decimal degrees",
                    example = "-8.149268120243516",
                    schema = @Schema(minimum = "-90", maximum = "90")
            )
            @RequestParam(value = "latitude", required = false) BigDecimal latitude,

            @Parameter(
                    description = "Longitude coordinate in decimal degrees",
                    example = "-34.91272534906557",
                    schema = @Schema(minimum = "-180", maximum = "180")
            )
            @RequestParam(value = "longitude", required = false) BigDecimal longitude,

            @Parameter(
                    description = "Neighborhood name for location filtering",
                    example = "Boa Viagem"
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
            summary = "Get all unique neighborhoods",
            description = """
                    Returns a complete list of all distinct neighborhoods available in the system.
                    
                    **Use cases:**
                    - Populating dropdown filters
                    - Validating neighborhood inputs
                    - Displaying available locations
                    
                    **Notes:**
                    - Results are returned as a simple string list
                    - List is sorted alphabetically
                    - No pagination is applied (returns all neighborhoods)
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List retrieved successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = String.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request - malformed request",
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
                            responseCode = "404",
                            description = "No neighborhoods found in the system",
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
    ResponseEntity<List<String>> getAllNeighborhoods();
}
