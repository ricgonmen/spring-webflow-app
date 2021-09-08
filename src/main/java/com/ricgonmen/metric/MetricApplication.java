package com.ricgonmen.metric;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import reactor.core.publisher.Flux;

import com.ricgonmen.metric.handler.MetricHandler;
import com.ricgonmen.metric.model.Metric;
import com.ricgonmen.metric.repository.MetricRepository;

import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class MetricApplication {

	public static void main(String[] args) {
		SpringApplication.run(MetricApplication.class, args);
	}


	@Bean
	CommandLineRunner init(ReactiveMongoOperations operations, MetricRepository repository) {
		return args -> {
			Flux<Metric> productFlux = Flux.just(
							new Metric("TEMPERATURE", 24.9),
							new Metric("TEMPERATURE", 21.4),
							new Metric("TEMPERATURE", 11.9))
					.flatMap(repository::save);

			productFlux
					.thenMany(repository.findAll())
					.subscribe(System.out::println);
		};
	}

	@Bean
	RouterFunction<ServerResponse> routes(MetricHandler handler) {
		return route()
				.path("/products",
						builder -> builder
								.nest(accept(APPLICATION_JSON).or(contentType(APPLICATION_JSON)).or(accept(TEXT_EVENT_STREAM)),
										nestedBuilder -> nestedBuilder
												.GET("/{id}", handler::getMetric)
												.GET(handler::getAllMetrics)
												.PUT("/{id}", handler::updateMetric)
												.POST(handler::saveMetric)
								)
								.DELETE("/{id}", handler::deleteMetric)
								.DELETE(handler::deleteAllMetrics)
				).build();
	}
}
