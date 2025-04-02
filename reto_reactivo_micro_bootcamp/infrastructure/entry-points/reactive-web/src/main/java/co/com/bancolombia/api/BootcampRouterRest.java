package co.com.bancolombia.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class BootcampRouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(BootcampHandler bootcampHandler) {
        return route(POST("/api/bootcamp"), bootcampHandler::createBootcampWithCapacities)
                .and(route(GET("/api/bootcamp"), bootcampHandler::listBootcampWithCapacity));
    }
}
