package com.github.sugayamidori.viaseguraapi.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "h3_coordinates", indexes = {
        @Index(name = "idx_h3_cell", columnList = "h3_cell"),
        @Index(name = "idx_bairro", columnList = "bairro_clean")
})
@Data
public class H3Coordinates implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "h3_cell", length = 15)
    private String h3Cell;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "bairro_clean", length = 100)
    private String neighborhood;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

}
