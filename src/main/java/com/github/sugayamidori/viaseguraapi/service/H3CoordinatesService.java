package com.github.sugayamidori.viaseguraapi.service;

import com.github.sugayamidori.viaseguraapi.model.H3Coordinates;
import com.github.sugayamidori.viaseguraapi.repository.H3CoordinatesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.sugayamidori.viaseguraapi.repository.specs.H3CoordinatesSpecs.*;

@Service
@RequiredArgsConstructor
public class H3CoordinatesService {

    private static final Set<String> LOWERCASE_WORDS = Set.of(
            "de", "da", "do", "das", "dos"
    );

    private final H3CoordinatesRepository repository;

    public Page<H3Coordinates> search(
            String h3Cell,
            BigDecimal latitude,
            BigDecimal longitude,
            String neighborhood,
            Integer page,
            Integer pageSize
    ) {

        Specification<H3Coordinates> specs = (root, query, cb) -> cb.conjunction();

        if(h3Cell != null && !h3Cell.isBlank()) {
            specs = specs.and(h3CellEquals(h3Cell));
        }

        if(latitude != null) {
            specs = specs.and(latitudeEquals(latitude));
        }

        if(longitude != null) {
            specs = specs.and(longitudeEquals(longitude));
        }

        if(neighborhood != null) {
            specs = specs.and(neighborhoodLike(neighborhood));
        }

        Pageable pageRequest = PageRequest.of(page, pageSize);

        return repository.findAll(specs, pageRequest);
    }

    public Map<String, List<H3Coordinates>> findByH3CellsToHeatmap(List<String> h3Cells) {
        List<H3Coordinates> allCoordinates = repository.findByH3CellIn(h3Cells);

        return allCoordinates.stream()
                .collect(Collectors.groupingBy(H3Coordinates::getH3Cell));
    }

    public List<String> findDistinctNeighborhoods() {
        List<String> distinctNeighborhoods = repository.findDistinctNeighborhoods();

        List<String> sanitized = distinctNeighborhoods.stream()
                .filter(value -> value != null && !value.contains("\\N"))
                .toList();

        Map<String, List<String>> grouped = sanitized.stream()
                .collect(Collectors.groupingBy(this::normalize));

        List<String> finalList = new ArrayList<>();

        for (List<String> group : grouped.values()) {
            String chosen = group.stream()
                    .filter(this::hasAccents)
                    .findFirst()
                    .orElse(group.getFirst());

            finalList.add(capitalizeWords(chosen));
        }

        finalList.sort(Comparator.naturalOrder());

        return finalList;
    }

    private String capitalizeWords(String text) {
        if (text == null || text.isBlank()) return text;

        String[] words = text.toLowerCase().split("\\s+");

        for (int i = 0; i < words.length; i++) {
            String w = words[i];

            if (i == 0 || !LOWERCASE_WORDS.contains(w)) {
                words[i] = w.substring(0, 1).toUpperCase() + w.substring(1);
            }
        }

        return String.join(" ", words);
    }
    private String normalize(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
    }

    private boolean hasAccents(String input) {
        return !input.equals(normalize(input));
    }

}
