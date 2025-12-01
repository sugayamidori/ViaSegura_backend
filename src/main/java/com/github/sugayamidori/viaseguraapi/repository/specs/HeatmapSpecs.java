package com.github.sugayamidori.viaseguraapi.repository.specs;

import com.github.sugayamidori.viaseguraapi.model.H3Coordinates;
import com.github.sugayamidori.viaseguraapi.model.Heatmap;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class HeatmapSpecs {

    public static Specification<Heatmap> h3CellEquals(String h3Cell) {
        return (root, query, cb) -> cb.equal(root.get("h3Cell"), h3Cell);
    }

    public static Specification<Heatmap> startYearEquals(Integer startYear) {
        return (root, query, cb) -> cb.ge(root.get("year"), startYear);
    }

    public static Specification<Heatmap> startMonthEquals(Integer startMonth) {
        return (root, query, cb) -> cb.ge(root.get("month"), startMonth);
    }

    public static Specification<Heatmap> endYearEquals(Integer endYear) {
        return (root, query, cb) -> cb.le(root.get("year"), endYear);
    }

    public static Specification<Heatmap> endMonthEquals(Integer endMonth) {
        return (root, query, cb) -> cb.le(root.get("month"), endMonth);
    }

    public static Specification<Heatmap> numCasualtiesEquals(BigDecimal numCasualties) {
        return (root, query, cb) -> cb.equal(root.get("numCasualties"), numCasualties);
    }

    public static Specification<Heatmap> hasH3CoordinatesInNeighborhood(String neighborhood) {
        return (root, query, cb) -> {
            Subquery<String> subquery = query.subquery(String.class);
            Root<H3Coordinates> h3Root = subquery.from(H3Coordinates.class);

            subquery.select(h3Root.get("h3Cell"))
                    .where(
                            cb.and(
                                    cb.equal(h3Root.get("h3Cell"), root.get("h3Cell")),
                                    cb.like(
                                            cb.function("unaccent", String.class, cb.upper(h3Root.get("neighborhood"))),
                                            "%" + neighborhood.toUpperCase() + "%"
                                    )
                            )
                    );

            return cb.exists(subquery);
        };
    }
}
