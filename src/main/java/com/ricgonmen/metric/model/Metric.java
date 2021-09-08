package com.ricgonmen.metric.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Document
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Metric {
	
    @Id
    private String id;

    private String name;
    
    private LocalDateTime timeStamp;
    
    private Double value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Metric metric = (Metric) o;
        return Objects.equals(id, metric.id) &&
                Objects.equals(timeStamp, metric.timeStamp) &&
                Objects.equals(name, metric.name) &&
                Objects.equals(value, metric.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timeStamp, name, value);
    }

    @Override
    public String toString() {
        return "Metric{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", time-stamp='" + timeStamp.toString() + '\'' +
                ", value=" + value +
                '}';
    }

	public Metric(String name, Double value) {
		super();
		id = null;
		this.name = name;
		this.value = value;
		timeStamp = LocalDateTime.now();
	}
}
