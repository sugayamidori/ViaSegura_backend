package com.github.sugayamidori.viaseguraapi.repository;

import com.github.sugayamidori.viaseguraapi.model.H3Coordinates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface H3CoordinatesRepository extends JpaRepository<H3Coordinates, UUID>, JpaSpecificationExecutor<H3Coordinates> {

    @Query("SELECT h FROM H3Coordinates h WHERE h.h3Cell IN :h3Cells")
    List<H3Coordinates> findByH3CellIn(@Param("h3Cells") List<String> h3Cells);
}
