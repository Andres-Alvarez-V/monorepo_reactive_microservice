package co.com.bancolombia.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class TechnologyRouterRest {
    @Bean("technologyRouterFunction")
    public RouterFunction<ServerResponse> routerFunction(TechnologyHandler handler) {
        return route(POST("/api/technology"), handler::createTechnology)
                .and(route(GET("/api"), request -> ServerResponse.ok().body(Mono.just("OK"), String.class)))
                .and(route(GET("/api/technology"), handler::listTechnologies));
    }
}
