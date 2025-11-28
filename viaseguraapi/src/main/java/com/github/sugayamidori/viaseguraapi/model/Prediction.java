package com.github.sugayamidori.viaseguraapi.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "predictions", indexes = {
        @Index(name = "idx_predictions_cell", columnList = "h3_cell"),
        @Index(name = "idx_predictions_date", columnList = "week_start")
})
@Data
public class Prediction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "h3_cell", length = 15)
    private String h3Cell;

    @Column(name = "week_start")
    private LocalDate weekStart;

    @Column(name = "predicted_accidents", precision = 15, scale = 10)
    private BigDecimal predictedAccidents;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

}
