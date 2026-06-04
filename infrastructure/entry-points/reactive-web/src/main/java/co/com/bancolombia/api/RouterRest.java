package co.com.bancolombia.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(GET("/api/usecase/path"), handler::listenGETUseCase)
                .andRoute(POST("/api/v1/franchises",
                        contentType(MediaType.APPLICATION_JSON),
                        franchiseHandler::createFranchise))
                .and(route(GET("/api/v1/franchises",
                        franchiseHandler::getAllFranchises)));
    }
}
