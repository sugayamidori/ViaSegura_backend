package com.github.sugayamidori.viaseguraapi.service;

import com.github.sugayamidori.viaseguraapi.controller.dto.H3CoordinatesDTO;
import com.github.sugayamidori.viaseguraapi.controller.dto.HeatmapDTO;
import com.github.sugayamidori.viaseguraapi.controller.dto.HeatmapWithCoordinatesDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;

@Service
public class ExcelExportService {

    public String generateBase64Excel(List<HeatmapWithCoordinatesDTO> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Heatmap Data");

            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = createHeaderStyle(workbook);

            String[] headers = {"H3 Cell", "Year", "Month",
                    "Neighborhood", "Latitude", "Longitude"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (HeatmapWithCoordinatesDTO item : data) {
                HeatmapDTO heatmap = item.heatmap();

                if (item.coordinates() != null && !item.coordinates().isEmpty()) {
                    for (H3CoordinatesDTO coordinate : item.coordinates()) {
                        Row row = sheet.createRow(rowNum++);
                        fillRow(row, heatmap, coordinate);
                    }
                } else {
                    Row row = sheet.createRow(rowNum++);
                    fillRow(row, heatmap, null);
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);

            byte[] excelBytes = outputStream.toByteArray();

            return Base64.getEncoder().encodeToString(excelBytes);
        }
    }

    private void fillRow(Row row, HeatmapDTO heatmap, H3CoordinatesDTO coordinate) {
        int colNum = 0;

        createCell(row, colNum++, heatmap.h3Cell());
        createCell(row, colNum++, heatmap.year());
        createCell(row, colNum++, heatmap.month());

        if (coordinate != null) {
            createCell(row, colNum++, coordinate.neighborhood());
            createCell(row, colNum++, coordinate.latitude());
            createCell(row, colNum, coordinate.longitude());
        } else {
            createCell(row, colNum++, "");
            createCell(row, colNum++, "");
            createCell(row, colNum, "");
        }
    }

    private void createCell(Row row, int column, Object value) {
        Cell cell = row.createCell(column);

        switch (value) {
            case null -> cell.setCellValue("");
            case String s -> cell.setCellValue(s);
            case Integer i -> cell.setCellValue(i);
            case BigDecimal bigDecimal -> cell.setCellValue(bigDecimal.doubleValue());
            case Double v -> cell.setCellValue(v);
            default -> cell.setCellValue(value.toString());
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}
