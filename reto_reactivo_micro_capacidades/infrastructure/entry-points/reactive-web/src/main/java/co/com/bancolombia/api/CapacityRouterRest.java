package co.com.bancolombia.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class CapacityRouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(CapacityHandler capacityHandler) {
        return route(POST("/api/capacity"), capacityHandler::createCapacity)
                .and(route(GET("/api/capacity"), capacityHandler::listCapacityTechnologies));
    }
}
