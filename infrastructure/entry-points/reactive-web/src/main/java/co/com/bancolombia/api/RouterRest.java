package co.com.bancolombia.api;

import co.com.bancolombia.api.branch.BranchHandler;
import co.com.bancolombia.api.franchise.FranchiseHandler;
import co.com.bancolombia.api.product.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;

@Configuration
public class RouterRest {

    //   FRANCHISE
    @Bean
    public RouterFunction<ServerResponse> routerFranchiseFunction(FranchiseHandler handler) {
        return RouterFunctions.route()
                .nest(RequestPredicates.path("/api/v1/franchises"), builder -> builder
                        .GET("/getAll", handler::getAllFranchises)
                        .POST("/create", handler::createFranchise)
                        .GET("/getById/{id}", handler::getFranchiseById)
                        .PUT("/update/{id}", handler::updateFranchiseName)
                        .DELETE("/delete/{id}", handler::deleteFranchise))

                .build();
    }

    //   BRANCH
    @Bean
    public RouterFunction<ServerResponse> routerBranchFunction(BranchHandler handler) {
        return RouterFunctions.route()
                .nest(RequestPredicates.path("/api/v1/branches"), builder -> builder
                        .GET("/get/franchise/{franchiseId}/branch/{branchId}", handler::getBranch)
                        .POST("/create/franchise/{franchiseId}", handler::addBranch)
                        .PUT("/update/franchise/{franchiseId}/branch/{branchId}", handler::updateBranchName))

                .build();
    }

    //   PRODUCT
    @Bean
    public RouterFunction<ServerResponse> routerProductFunction(ProductHandler handler) {
        return RouterFunctions.route()
                .nest(RequestPredicates.path("/api/v1/products"), builder -> builder
                        .POST("/create/franchise/{franchiseId}/branch/{branchId}", contentType(MediaType.APPLICATION_JSON),handler::addProduct)
                        .GET("/get/franchise/{franchiseId}/branch/{branchId}/product/{productId}", handler::getProduct)
                        .DELETE("/delete/franchise/{franchiseId}/branch/{branchId}/product/{productId}",
                                handler::deleteProduct)
                        .PUT("/productStock/franchise/{franchiseId}/branch/{branchId}/product/{productId}/stock",
                                contentType(MediaType.APPLICATION_JSON),
                                handler::updateProductStock)
                        .PUT("/productName/franchise/{franchiseId}/branch/{branchId}/product/{productId}",
                                contentType(MediaType.APPLICATION_JSON),
                                handler::updateProductName)
                        .GET("/getHighStock/franchise/{franchiseId}/branch/{branchId}/product/", handler::getHighestStockProduct))
                .build();
    }


}
