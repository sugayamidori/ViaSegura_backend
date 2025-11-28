package com.github.sugayamidori.viaseguraapi.repository;

import com.github.sugayamidori.viaseguraapi.model.Heatmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface HeatmapRepository extends JpaRepository<Heatmap, UUID>, JpaSpecificationExecutor<Heatmap> {
}
