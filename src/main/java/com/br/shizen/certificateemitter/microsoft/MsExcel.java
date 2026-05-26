package com.br.shizen.certificateemitter.microsoft;

import jakarta.annotation.PreDestroy;
import lombok.Setter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Scope("prototype")
public class MsExcel {

    @Setter
    private String fileName;
    private FileInputStream excelReadFile;
    private FileOutputStream excelWriteFile;
    private Workbook wb;
    private Sheet sheet;

    @PreDestroy
    public void preDestroy() throws IOException {
        this.wb.close();
    }

    public void loadFile() throws IOException {
        this.excelReadFile = new FileInputStream(new File(this.fileName));
        this.initialize();
    }

    private void initialize() throws IOException {
        if (this.excelReadFile != null) {
            this.wb = WorkbookFactory.create(this.excelReadFile);
            this.sheet = this.wb.getSheetAt(0);
        }
        if (this.excelWriteFile != null) {
            this.wb = new XSSFWorkbook();
            this.sheet = this.wb.createSheet("sheet 1");
        }
    }

    public Row getFirstRowFromTop(Map<String, String> queryMap) {
        int rowStart = 2;
        int rowEnd = this.sheet.getLastRowNum();
        if (rowEnd <= 0) {
            return null;
        }
        Row firstRow = this.sheet.getRow(1);
        int lastColumn = firstRow.getLastCellNum();

        this.sheet.forEach(row -> {
            row.forEach(cell -> {
                System.out.println(cell);
            });
        });
        for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
            Row r = this.sheet.getRow(rowNum);
            if (r == null) {
                // This whole row is empty
                // Handle it as needed
                return this.sheet.getRow(rowNum - 1);
            }
            lastColumn = Math.max(r.getLastCellNum(), 3);
            for (int cn = 0; cn < lastColumn; cn++) {
                Cell c = r.getCell(cn, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (c == null) {
                    // The spreadsheet is empty in this cell
                } else {
                    // Do something useful with the cell's contents
                }
            }
        }
        return null;
    }

    public void createFile() throws IOException {
        this.excelWriteFile = new FileOutputStream(this.fileName);
        this.initialize();
    }

    public Row createRow(@NonNull Class cells) throws IOException {
        Row row = this.sheet.createRow(0);
        Field[] fields = cells.getFields();
        AtomicInteger counter = new AtomicInteger(0);
        Arrays.stream(fields).forEach(field -> {
            row.createCell(counter.get());
            counter.getAndIncrement();
        });
        this.wb.write(this.excelWriteFile);
        return row;
    }

    public void setCellValue(@NonNull Row row, int columnNumber, String value) throws IOException {
        AtomicInteger counter = new AtomicInteger(0);
        row.forEach(cell -> {
            if (counter.get() == columnNumber) {
                cell.setCellValue(value);
            }
            counter.getAndIncrement();
        });
        this.wb.write(this.excelWriteFile);
    }

    public void closeDoc() throws IOException {
        this.wb.close();
    }
}
