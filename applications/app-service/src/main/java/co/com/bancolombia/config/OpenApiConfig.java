package co.com.bancolombia.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.parameters.PathParameter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Contact contact = new Contact();
        contact.setName("Pragma Software");
        contact.setUrl("https://pragma.com.co");
        contact.setEmail("info@pragma.com.co");

        License license = new License();
        license.setName("Apache 2.0");
        license.setUrl("https://www.apache.org/licenses/LICENSE-2.0.html");

        Info info = new Info()
                .title("Franchise API")
                .description("API for managing franchises, branches and products")
                .version("1.0.0")
                .contact(contact)
                .license(license)
                .summary("Reactive RESTful API for managing franchises");

        Server devServer = new Server()
                .url("http://localhost:8080")
                .description("Development Server\n");

        List<Server> servers = new ArrayList<>();
        servers.add(devServer);

        OpenAPI openAPI = new OpenAPI()
                .info(info)
                .servers(servers);

        // Agregar endpoints manualmente
        addFranchiseEndpoints(openAPI);
        addBranchEndpoints(openAPI);
        addProductEndpoints(openAPI);

        return openAPI;
    }

    private void addFranchiseEndpoints(OpenAPI openAPI) {
        // POST /api/v1/franchises/create
        Operation createOp = new Operation()
                .summary("Create a new franchise")
                .description("Creates a new franchise with the provided name")
                .tags(List.of("Franchises"))
                .responses(new ApiResponses().addApiResponse("201", new ApiResponse().description("Franchise created successfully")));

        PathItem createPath = new PathItem().post(createOp);
        openAPI.path("/api/v1/franchises/create", createPath);

        // GET /api/v1/franchises/getAll
        Operation getAllOp = new Operation()
                .summary("Get all franchises")
                .description("Retrieves all registered franchises")
                .tags(List.of("Franchises"))
                .responses(new ApiResponses().addApiResponse("200", new ApiResponse().description("Franchises retrieved successfully")));

        PathItem getAllPath = new PathItem().get(getAllOp);
        openAPI.path("/api/v1/franchises/getAll", getAllPath);

        // GET /api/v1/franchises/getById/{id}
        Operation getByIdOp = new Operation()
                .summary("Get franchise by ID")
                .description("Retrieves a franchise by its ID")
                .tags(List.of("Franchises"))
                .addParametersItem(new PathParameter().name("id").description("Franchise ID").required(true))
                .responses(new ApiResponses()
                        .addApiResponse("200", new ApiResponse().description("Franchise retrieved successfully"))
                        .addApiResponse("404", new ApiResponse().description("Franchise not found")));

        PathItem getByIdPath = new PathItem().get(getByIdOp);
        openAPI.path("/api/v1/franchises/getById/{id}", getByIdPath);

        // PUT /api/v1/franchises/update/{id}
        Operation updateOp = new Operation()
                .summary("Update franchise name")
                .description("Updates the name of an existing franchise")
                .tags(List.of("Franchises"))
                .addParametersItem(new PathParameter().name("id").description("Franchise ID").required(true))
                .responses(new ApiResponses().addApiResponse("200", new ApiResponse().description("Franchise updated successfully")));

        PathItem updatePath = new PathItem().put(updateOp);
        openAPI.path("/api/v1/franchises/update/{id}", updatePath);

        // DELETE /api/v1/franchises/delete/{id}
        Operation deleteOp = new Operation()
                .summary("Delete franchise")
                .description("Deletes a franchise by its ID")
                .tags(List.of("Franchises"))
                .addParametersItem(new PathParameter().name("id").description("Franchise ID").required(true))
                .responses(new ApiResponses().addApiResponse("204", new ApiResponse().description("Franchise deleted successfully")));

        PathItem deletePath = new PathItem().delete(deleteOp);
        openAPI.path("/api/v1/franchises/delete/{id}", deletePath);
    }

    private void addBranchEndpoints(OpenAPI openAPI) {
        // GET /api/v1/branches/get/franchise/{franchiseId}/branch/{branchId}
        Operation getOp = new Operation()
                .summary("Get branch")
                .description("Retrieves a branch by franchise and branch IDs")
                .tags(List.of("Branches"))
                .addParametersItem(new PathParameter().name("franchiseId").description("Franchise ID").required(true))
                .addParametersItem(new PathParameter().name("branchId").description("Branch ID").required(true))
                .responses(new ApiResponses()
                        .addApiResponse("200", new ApiResponse().description("Branch retrieved successfully"))
                        .addApiResponse("404", new ApiResponse().description("Branch not found")));

        PathItem getPath = new PathItem().get(getOp);
        openAPI.path("/api/v1/branches/get/franchise/{franchiseId}/branch/{branchId}", getPath);

        // POST /api/v1/branches/create/franchise/{franchiseId}
        Operation createOp = new Operation()
                .summary("Create a new branch")
                .description("Creates a new branch in a franchise")
                .tags(List.of("Branches"))
                .addParametersItem(new PathParameter().name("franchiseId").description("Franchise ID").required(true))
                .responses(new ApiResponses().addApiResponse("201", new ApiResponse().description("Branch created successfully")));

        PathItem createPath = new PathItem().post(createOp);
        openAPI.path("/api/v1/branches/create/franchise/{franchiseId}", createPath);

        // PUT /api/v1/branches/update/franchise/{franchiseId}/branch/{branchId}
        Operation updateOp = new Operation()
                .summary("Update branch name")
                .description("Updates the name of an existing branch")
                .tags(List.of("Branches"))
                .addParametersItem(new PathParameter().name("franchiseId").description("Franchise ID").required(true))
                .addParametersItem(new PathParameter().name("branchId").description("Branch ID").required(true))
                .responses(new ApiResponses().addApiResponse("200", new ApiResponse().description("Branch updated successfully")));

        PathItem updatePath = new PathItem().put(updateOp);
        openAPI.path("/api/v1/branches/update/franchise/{franchiseId}/branch/{branchId}", updatePath);
    }

    private void addProductEndpoints(OpenAPI openAPI) {
        // POST /api/v1/products/create/franchise/{franchiseId}/branch/{branchId}
        Operation createOp = new Operation()
                .summary("Create a new product")
                .description("Creates a new product in a branch")
                .tags(List.of("Products"))
                .addParametersItem(new PathParameter().name("franchiseId").description("Franchise ID").required(true))
                .addParametersItem(new PathParameter().name("branchId").description("Branch ID").required(true))
                .responses(new ApiResponses().addApiResponse("201", new ApiResponse().description("Product created successfully")));

        PathItem createPath = new PathItem().post(createOp);
        openAPI.path("/api/v1/products/create/franchise/{franchiseId}/branch/{branchId}", createPath);

        // GET /api/v1/products/get/franchise/{franchiseId}/branch/{branchId}/product/{productId}
        Operation getOp = new Operation()
                .summary("Get product")
                .description("Retrieves a product by franchise, branch, and product IDs")
                .tags(List.of("Products"))
                .addParametersItem(new PathParameter().name("franchiseId").description("Franchise ID").required(true))
                .addParametersItem(new PathParameter().name("branchId").description("Branch ID").required(true))
                .addParametersItem(new PathParameter().name("productId").description("Product ID").required(true))
                .responses(new ApiResponses()
                        .addApiResponse("200", new ApiResponse().description("Product retrieved successfully"))
                        .addApiResponse("404", new ApiResponse().description("Product not found")));

        PathItem getPath = new PathItem().get(getOp);
        openAPI.path("/api/v1/products/get/franchise/{franchiseId}/branch/{branchId}/product/{productId}", getPath);

        // DELETE /api/v1/products/delete/franchise/{franchiseId}/branch/{branchId}/product/{productId}
        Operation deleteOp = new Operation()
                .summary("Delete product")
                .description("Deletes a product by franchise, branch, and product IDs")
                .tags(List.of("Products"))
                .addParametersItem(new PathParameter().name("franchiseId").description("Franchise ID").required(true))
                .addParametersItem(new PathParameter().name("branchId").description("Branch ID").required(true))
                .addParametersItem(new PathParameter().name("productId").description("Product ID").required(true))
                .responses(new ApiResponses().addApiResponse("204", new ApiResponse().description("Product deleted successfully")));

        PathItem deletePath = new PathItem().delete(deleteOp);
        openAPI.path("/api/v1/products/delete/franchise/{franchiseId}/branch/{branchId}/product/{productId}", deletePath);

        // PUT /api/v1/products/productStock/franchise/{franchiseId}/branch/{branchId}/product/{productId}/stock
        Operation updateStockOp = new Operation()
                .summary("Update product stock")
                .description("Updates the stock of an existing product")
                .tags(List.of("Products"))
                .addParametersItem(new PathParameter().name("franchiseId").description("Franchise ID").required(true))
                .addParametersItem(new PathParameter().name("branchId").description("Branch ID").required(true))
                .addParametersItem(new PathParameter().name("productId").description("Product ID").required(true))
                .responses(new ApiResponses().addApiResponse("200", new ApiResponse().description("Product stock updated successfully")));

        PathItem updateStockPath = new PathItem().put(updateStockOp);
        openAPI.path("/api/v1/products/productStock/franchise/{franchiseId}/branch/{branchId}/product/{productId}/stock", updateStockPath);

        // PUT /api/v1/products/productName/franchise/{franchiseId}/branch/{branchId}/product/{productId}
        Operation updateNameOp = new Operation()
                .summary("Update product name")
                .description("Updates the name of an existing product")
                .tags(List.of("Products"))
                .addParametersItem(new PathParameter().name("franchiseId").description("Franchise ID").required(true))
                .addParametersItem(new PathParameter().name("branchId").description("Branch ID").required(true))
                .addParametersItem(new PathParameter().name("productId").description("Product ID").required(true))
                .responses(new ApiResponses().addApiResponse("200", new ApiResponse().description("Product name updated successfully")));

        PathItem updateNamePath = new PathItem().put(updateNameOp);
        openAPI.path("/api/v1/products/productName/franchise/{franchiseId}/branch/{branchId}/product/{productId}", updateNamePath);

        // GET /api/v1/products/getHighStock/franchise/{franchiseId}/branch/{branchId}/product/
        Operation getHighStockOp = new Operation()
                .summary("Get product with highest stock")
                .description("Retrieves the product with the highest stock in a branch")
                .tags(List.of("Products"))
                .addParametersItem(new PathParameter().name("franchiseId").description("Franchise ID").required(true))
                .addParametersItem(new PathParameter().name("branchId").description("Branch ID").required(true))
                .responses(new ApiResponses()
                        .addApiResponse("200", new ApiResponse().description("Product retrieved successfully")));

        PathItem getHighStockPath = new PathItem().get(getHighStockOp);
        openAPI.path("/api/v1/products/getHighStock/franchise/{franchiseId}/branch/{branchId}/product/", getHighStockPath);
    }
}

