package co.com.bancolombia.api;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;


@Configuration
public class CapacityTechnologyRouterRest {

    @Bean("capacityTechnologyRouterFunction")
    public RouterFunction<ServerResponse> routerFunction(CapacityTechnologyHandler handler) {
        return route(POST("/api/capacity-technology"), handler::createCapacityTechnology)
                .and(route(GET("/api/capacity-technologies-by-capicity-id"), handler::findCapacityWithTechnologieByCapacityIdIn))
                .and(route(GET("/api/capacity-technologies-by-technology-count"), handler::findAllCapacitiesWithTechnologiesOrderedByTechnologyCount));
    }
}
