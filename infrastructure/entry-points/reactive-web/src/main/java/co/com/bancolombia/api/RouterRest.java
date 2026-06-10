package co.com.bancolombia.api;

import co.com.bancolombia.api.branch.BranchHandler;
import co.com.bancolombia.api.branch.dto.BranchRequest;
import co.com.bancolombia.api.branch.dto.BranchResponse;
import co.com.bancolombia.api.franchise.FranchiseHandler;
import co.com.bancolombia.api.franchise.dto.FranchiseRequest;
import co.com.bancolombia.api.franchise.dto.FranchiseResponse;
import co.com.bancolombia.api.product.ProductHandler;
import co.com.bancolombia.api.product.dto.ProductRequest;
import co.com.bancolombia.api.product.dto.ProductResponse;
import co.com.bancolombia.api.product.dto.StockUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;

@Configuration
public class RouterRest {

    // =====================================================================
    //  FRANCHISE
    // =====================================================================
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/franchises/getAll",
                    method = RequestMethod.GET,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "getAllFranchises",
                    operation = @Operation(
                            operationId = "getAllFranchises",
                            tags = {"Franchises"},
                            summary = "Get all franchises",
                            description = "Retrieves all registered franchises with their branches and products.",
                            responses = @ApiResponse(
                                    responseCode = "200",
                                    description = "Franchises retrieved successfully",
                                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(
                                                    schema = @Schema(implementation = FranchiseResponse.class))))
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/franchises/create",
                    method = RequestMethod.POST,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "createFranchise",
                    operation = @Operation(
                            operationId = "createFranchise",
                            tags = {"Franchises"},
                            summary = "Create a new franchise",
                            description = "Creates a new franchise with the provided name. Returns the created franchise.",
                            requestBody = @RequestBody(required = true,
                                    content = @Content(
                                            schema = @Schema(implementation = FranchiseRequest.class),
                                            examples = @ExampleObject(name = "Pizza Company",
                                                    value = "{\n  \"name\": \"Pizza Company\"\n}"))),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Franchise created successfully",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Validation error: name is blank or null")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/franchises/getById/{id}",
                    method = RequestMethod.GET,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "getFranchiseById",
                    operation = @Operation(
                            operationId = "getFranchiseById",
                            tags = {"Franchises"},
                            summary = "Get franchise by ID",
                            description = "Retrieves a single franchise by its identifier.",
                            parameters = @Parameter(name = "id", description = "Franchise identifier", required = true,
                                    example = "507f1f77bcf86cd799439011"),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Franchise retrieved successfully",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Franchise not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/franchises/update/{id}",
                    method = RequestMethod.PUT,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "updateFranchiseName",
                    operation = @Operation(
                            operationId = "updateFranchiseName",
                            tags = {"Franchises"},
                            summary = "Update franchise name",
                            description = "Updates the name of an existing franchise.",
                            parameters = @Parameter(name = "id", description = "Franchise identifier", required = true),
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = FranchiseRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Franchise updated successfully",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Validation error: name is blank or null"),
                                    @ApiResponse(responseCode = "404", description = "Franchise not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/franchises/delete/{id}",
                    method = RequestMethod.DELETE,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "deleteFranchise",
                    operation = @Operation(
                            operationId = "deleteFranchise",
                            tags = {"Franchises"},
                            summary = "Delete franchise",
                            description = "Deletes a franchise by its identifier.",
                            parameters = @Parameter(name = "id", description = "Franchise identifier", required = true),
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Franchise deleted successfully"),
                                    @ApiResponse(responseCode = "404", description = "Franchise not found")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFranchiseFunction(FranchiseHandler handler) {
        return RouterFunctions.route()
                .nest(RequestPredicates.path("/api/v1/franchises"), builder -> builder
                        .GET("/getAll", handler::getAllFranchises)
                        .POST("/create", contentType(MediaType.APPLICATION_JSON), handler::createFranchise)
                        .GET("/getById/{id}", handler::getFranchiseById)
                        .PUT("/update/{id}", contentType(MediaType.APPLICATION_JSON), handler::updateFranchiseName)
                        .DELETE("/delete/{id}", handler::deleteFranchise))
                .build();
    }

    // =====================================================================
    //  BRANCH
    // =====================================================================
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/branches/get/franchise/{franchiseId}/branch/{branchId}",
                    method = RequestMethod.GET,
                    beanClass = BranchHandler.class,
                    beanMethod = "getBranch",
                    operation = @Operation(
                            operationId = "getBranch",
                            tags = {"Branches"},
                            summary = "Get a branch by id within a franchise",
                            parameters = {
                                    @Parameter(name = "franchiseId", description = "Franchise identifier", required = true),
                                    @Parameter(name = "branchId", description = "Branch identifier", required = true)
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Branch retrieved successfully",
                                            content = @Content(schema = @Schema(implementation = BranchResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Franchise or Branch not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/branches/create/franchise/{franchiseId}",
                    method = RequestMethod.POST,
                    beanClass = BranchHandler.class,
                    beanMethod = "addBranch",
                    operation = @Operation(
                            operationId = "addBranch",
                            tags = {"Branches"},
                            summary = "Add a branch to a franchise",
                            parameters = @Parameter(name = "franchiseId", description = "Franchise identifier", required = true),
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = BranchRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Branch created successfully",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Validation error"),
                                    @ApiResponse(responseCode = "404", description = "Franchise not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/branches/update/franchise/{franchiseId}/branch/{branchId}",
                    method = RequestMethod.PUT,
                    beanClass = BranchHandler.class,
                    beanMethod = "updateBranchName",
                    operation = @Operation(
                            operationId = "updateBranchName",
                            tags = {"Branches"},
                            summary = "Update branch name",
                            parameters = {
                                    @Parameter(name = "franchiseId", description = "Franchise identifier", required = true),
                                    @Parameter(name = "branchId", description = "Branch identifier", required = true)
                            },
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = BranchRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Branch updated successfully",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Validation error"),
                                    @ApiResponse(responseCode = "404", description = "Franchise or Branch not found")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerBranchFunction(BranchHandler handler) {
        return RouterFunctions.route()
                .nest(RequestPredicates.path("/api/v1/branches"), builder -> builder
                        .GET("/get/franchise/{franchiseId}/branch/{branchId}", handler::getBranch)
                        .POST("/create/franchise/{franchiseId}", contentType(MediaType.APPLICATION_JSON), handler::addBranch)
                        .PUT("/update/franchise/{franchiseId}/branch/{branchId}", contentType(MediaType.APPLICATION_JSON), handler::updateBranchName))
                .build();
    }

    // =====================================================================
    //  PRODUCT
    // =====================================================================
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/products/create/franchise/{franchiseId}/branch/{branchId}",
                    method = RequestMethod.POST,
                    beanClass = ProductHandler.class,
                    beanMethod = "addProduct",
                    operation = @Operation(
                            operationId = "addProduct",
                            tags = {"Products"},
                            summary = "Add a product to a branch",
                            parameters = {
                                    @Parameter(name = "franchiseId", description = "Franchise identifier", required = true),
                                    @Parameter(name = "branchId", description = "Branch identifier", required = true)
                            },
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = ProductRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Product created successfully",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Validation error"),
                                    @ApiResponse(responseCode = "404", description = "Franchise or Branch not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/products/get/franchise/{franchiseId}/branch/{branchId}/product/{productId}",
                    method = RequestMethod.GET,
                    beanClass = ProductHandler.class,
                    beanMethod = "getProduct",
                    operation = @Operation(
                            operationId = "getProduct",
                            tags = {"Products"},
                            summary = "Get a product by id",
                            parameters = {
                                    @Parameter(name = "franchiseId", required = true),
                                    @Parameter(name = "branchId", required = true),
                                    @Parameter(name = "productId", required = true)
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Product retrieved successfully",
                                            content = @Content(schema = @Schema(implementation = ProductResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Franchise, Branch or Product not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/products/delete/franchise/{franchiseId}/branch/{branchId}/product/{productId}",
                    method = RequestMethod.DELETE,
                    beanClass = ProductHandler.class,
                    beanMethod = "deleteProduct",
                    operation = @Operation(
                            operationId = "deleteProduct",
                            tags = {"Products"},
                            summary = "Delete a product from a branch",
                            parameters = {
                                    @Parameter(name = "franchiseId", required = true),
                                    @Parameter(name = "branchId", required = true),
                                    @Parameter(name = "productId", required = true)
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Product deleted successfully",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Franchise, Branch or Product not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/products/productStock/franchise/{franchiseId}/branch/{branchId}/product/{productId}/stock",
                    method = RequestMethod.PUT,
                    beanClass = ProductHandler.class,
                    beanMethod = "updateProductStock",
                    operation = @Operation(
                            operationId = "updateProductStock",
                            tags = {"Products"},
                            summary = "Update product stock",
                            parameters = {
                                    @Parameter(name = "franchiseId", required = true),
                                    @Parameter(name = "branchId", required = true),
                                    @Parameter(name = "productId", required = true)
                            },
                            requestBody = @RequestBody(required = true,
                                    content = @Content(
                                            schema = @Schema(implementation = StockUpdateRequest.class),
                                            examples = @ExampleObject(name = "New stock",
                                                    value = "{\n  \"stock\": 150\n}"))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Stock updated successfully",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Franchise, Branch or Product not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/products/productName/franchise/{franchiseId}/branch/{branchId}/product/{productId}",
                    method = RequestMethod.PUT,
                    beanClass = ProductHandler.class,
                    beanMethod = "updateProductName",
                    operation = @Operation(
                            operationId = "updateProductName",
                            tags = {"Products"},
                            summary = "Update product name",
                            parameters = {
                                    @Parameter(name = "franchiseId", required = true),
                                    @Parameter(name = "branchId", required = true),
                                    @Parameter(name = "productId", required = true)
                            },
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = ProductRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Name updated successfully",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Franchise, Branch or Product not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/products/getHighStock/franchise/{franchiseId}/branch/{branchId}/product/",
                    method = RequestMethod.GET,
                    beanClass = ProductHandler.class,
                    beanMethod = "getHighestStockProduct",
                    operation = @Operation(
                            operationId = "getHighestStockProduct",
                            tags = {"Products"},
                            summary = "Get the product with the highest stock in a branch",
                            parameters = {
                                    @Parameter(name = "franchiseId", required = true),
                                    @Parameter(name = "branchId", required = true)
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Product retrieved successfully",
                                            content = @Content(schema = @Schema(implementation = ProductResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Branch has no products or not found")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerProductFunction(ProductHandler handler) {
        return RouterFunctions.route()
                .nest(RequestPredicates.path("/api/v1/products"), builder -> builder
                        .POST("/create/franchise/{franchiseId}/branch/{branchId}", contentType(MediaType.APPLICATION_JSON), handler::addProduct)
                        .GET("/get/franchise/{franchiseId}/branch/{branchId}/product/{productId}", handler::getProduct)
                        .DELETE("/delete/franchise/{franchiseId}/branch/{branchId}/product/{productId}", handler::deleteProduct)
                        .PUT("/productStock/franchise/{franchiseId}/branch/{branchId}/product/{productId}/stock",
                                contentType(MediaType.APPLICATION_JSON), handler::updateProductStock)
                        .PUT("/productName/franchise/{franchiseId}/branch/{branchId}/product/{productId}",
                                contentType(MediaType.APPLICATION_JSON), handler::updateProductName)
                        .GET("/getHighStock/franchise/{franchiseId}/branch/{branchId}/product/", handler::getHighestStockProduct))
                .build();
    }
}
