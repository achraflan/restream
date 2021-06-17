package com.restream.api.repository.rowmapper;

import com.restream.api.domain.Report;
import com.restream.api.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Report}, with proper type conversions.
 */
@Service
public class ReportRowMapper implements BiFunction<Row, String, Report> {

    private final ColumnConverter converter;

    public ReportRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Report} stored in the database.
     */
    @Override
    public Report apply(Row row, String prefix) {
        Report entity = new Report();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setMessage(converter.fromRow(row, prefix + "_message", String.class));
        entity.setDateCreation(converter.fromRow(row, prefix + "_date_creation", LocalDate.class));
        entity.setCheminId(converter.fromRow(row, prefix + "_chemin_id", Long.class));
        return entity;
    }
}
