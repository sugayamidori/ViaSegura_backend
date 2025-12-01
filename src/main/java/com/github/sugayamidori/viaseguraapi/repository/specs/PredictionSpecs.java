package com.github.sugayamidori.viaseguraapi.repository.specs;

import com.github.sugayamidori.viaseguraapi.model.Prediction;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class PredictionSpecs {

    public static Specification<Prediction> h3CellEquals(String h3Cell) {
        return (root, query, cb) -> cb.equal(root.get("h3Cell"), h3Cell);
    }

    public static Specification<Prediction> weekStartEquals(String weekStart) {
        return (root, query, cb) -> cb.equal(root.get("weekStart"), weekStart);
    }

    public static Specification<Prediction> predictedAccidentsEquals(BigDecimal predictedAccidents) {
        return (root, query, cb) -> cb.equal(root.get("predictedAccidents"), predictedAccidents);
    }
}
