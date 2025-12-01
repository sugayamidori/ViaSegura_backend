package com.github.sugayamidori.viaseguraapi.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "heatmap", indexes = {
        @Index(name = "idx_heatmap_cell", columnList = "h3_cell"),
        @Index(name = "idx_heatmap_date", columnList = "year, month")
})
@Data
public class Heatmap implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "h3_cell", length = 15)
    private String h3Cell;

    @Column
    private Integer year;

    @Column
    private Integer month;

    @Column(name = "num_sinistros", precision = 10, scale = 2)
    private BigDecimal numCasualties;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

}
