package com.ricgonmen.metric.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.ricgonmen.metric.model.Metric;

public interface MetricRepository
        extends ReactiveMongoRepository<Metric, String> {
}
