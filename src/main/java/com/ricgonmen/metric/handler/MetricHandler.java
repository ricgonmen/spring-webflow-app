package com.ricgonmen.metric.handler;

import com.ricgonmen.metric.model.Metric;
import com.ricgonmen.metric.repository.MetricRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class MetricHandler {
    private MetricRepository repository;

    public MetricHandler(MetricRepository repository) {
        this.repository = repository;
    }

    public Mono<ServerResponse> getAllMetrics(ServerRequest request) {
        Flux<Metric> products = repository.findAll();

        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(products, Metric.class);
    }

    public Mono<ServerResponse> getMetric(ServerRequest request) {
        String id = request.pathVariable("id");

        Mono<Metric> productMono = this.repository.findById(id);
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();

        return productMono
                .flatMap(product ->
                        ServerResponse.ok()
                                .contentType(APPLICATION_JSON)
                                .body(fromValue(product)))
                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> saveMetric(ServerRequest request) {
        Mono<Metric> productMono = request.bodyToMono(Metric.class);

        return productMono.flatMap(product ->
                ServerResponse.status(HttpStatus.CREATED)
                        .contentType(APPLICATION_JSON)
                        .body(repository.save(product), Metric.class));
    }

    public Mono<ServerResponse> updateMetric(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Metric> existingMetricMono = this.repository.findById(id);
        Mono<Metric> productMono = request.bodyToMono(Metric.class);

        Mono<ServerResponse> notFound = ServerResponse.notFound().build();

        return productMono.zipWith(existingMetricMono,
                (product, existingMetric) ->
                        new Metric(existingMetric.getId(), product.getName(), product.getTimeStamp(), product.getValue())
        ).flatMap(product ->
                ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .body(repository.save(product), Metric.class)
        ).switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> deleteMetric(ServerRequest request) {
        String id = request.pathVariable("id");

        Mono<Metric> productMono = this.repository.findById(id);
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();

        return productMono
                .flatMap(existingMetric ->
                        ServerResponse.ok()
                                .build(repository.delete(existingMetric))
                )
                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> deleteAllMetrics(ServerRequest request) {
        return ServerResponse.ok()
                .build(repository.deleteAll());
    }
}
