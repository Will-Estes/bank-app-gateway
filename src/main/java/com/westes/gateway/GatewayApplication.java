package com.westes.gateway;

import com.westes.gateway.trace.logging.ObservationContextSnapshotLifter;
import io.micrometer.context.ContextSnapshot;
import java.util.Date;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

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
            .path("/willestes82892/accounts-service/**")
            .filters(f -> f.rewritePath("/willestes82892/accounts-service/(?<segment>.*)", "/${segment}")
                .addResponseHeader("X-Response-Time", new Date().toString()))
            .uri("lb://ACCOUNTS-SERVICE")).
        route(p -> p
            .path("/willestes82892/loans-service/**")
            .filters(f -> f.rewritePath("/willestes82892/loans-service/(?<segment>.*)", "/${segment}")
                .addResponseHeader("X-Response-Time", new Date().toString()))
            .uri("lb://LOANS-SERVICE")).
        route(p -> p
            .path("/willestes82892/cards-service/**")
            .filters(f -> f.rewritePath("/willestes82892/cards-service/(?<segment>.*)", "/${segment}")
                .addResponseHeader("X-Response-Time", new Date().toString()))
            .uri("lb://CARDS-SERVICE")).build();
  }

  @ConditionalOnClass({ContextSnapshot.class, Hooks.class})
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
  @Bean
  public ApplicationListener<ContextRefreshedEvent> reactiveObservableHook() {
    return event -> Hooks.onEachOperator(
        ObservationContextSnapshotLifter.class.getSimpleName(),
        Operators.lift(ObservationContextSnapshotLifter.lifter()));
  }
}
