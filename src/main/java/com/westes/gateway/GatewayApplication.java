package com.westes.gateway;

import java.util.Date;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(GatewayApplication.class, args);
  }

  // if a consumer application wanted to indicate our system of "westes"
  @Bean
  public RouteLocator myRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route(p -> p
            .path("/westes/accounts-service/**")
            .filters(f -> f.rewritePath("/westes/accounts-service/(?<segment>.*)", "/${segment}")
                .addResponseHeader("X-Response-Time", new Date().toString()))
            .uri("lb://ACCOUNTS-SERVICE")).
        route(p -> p
            .path("/westes/loans-service/**")
            .filters(f -> f.rewritePath("/westes/loans-service/(?<segment>.*)", "/${segment}")
                .addResponseHeader("X-Response-Time", new Date().toString()))
            .uri("lb://LOANS-SERVICE")).
        route(p -> p
            .path("/westes/cards-service/**")
            .filters(f -> f.rewritePath("/westes/cards-service/(?<segment>.*)", "/${segment}")
                .addResponseHeader("X-Response-Time", new Date().toString()))
            .uri("lb://CARDS-SERVICE")).build();
  }
}
