package com.github.sugayamidori.viaseguraapi.repository.specs;

import com.github.sugayamidori.viaseguraapi.model.H3Coordinates;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.text.Normalizer;

public class H3CoordinatesSpecs {

    public static Specification<H3Coordinates> h3CellEquals(String h3Cell) {
        return (root, query, cb) -> cb.equal(root.get("h3Cell"), h3Cell);
    }

    public static Specification<H3Coordinates> latitudeEquals(BigDecimal latitude) {
        return (root, query, cb) -> cb.equal(root.get("latitude"), latitude);
    }

    public static Specification<H3Coordinates> longitudeEquals(BigDecimal longitude) {
        return (root, query, cb) -> cb.equal(root.get("longitude"), longitude);
    }

    public static Specification<H3Coordinates> neighborhoodLike(String neighborhoodLike) {
        return (root, query, cb) -> cb.like(
                cb.function("unaccent", String.class, cb.upper(root.get("neighborhood"))),
                "%" + normalize(neighborhoodLike).toUpperCase() + "%"
        );
    }

    private static String normalize(String input) {
        if (input == null) return null;
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }
}
