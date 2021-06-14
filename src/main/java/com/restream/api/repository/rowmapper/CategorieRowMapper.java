package com.restream.api.repository.rowmapper;

import com.restream.api.domain.Categorie;
import com.restream.api.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Categorie}, with proper type conversions.
 */
@Service
public class CategorieRowMapper implements BiFunction<Row, String, Categorie> {

    private final ColumnConverter converter;

    public CategorieRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Categorie} stored in the database.
     */
    @Override
    public Categorie apply(Row row, String prefix) {
        Categorie entity = new Categorie();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCategorieNom(converter.fromRow(row, prefix + "_categorie_nom", String.class));
        entity.setCategorieImage(converter.fromRow(row, prefix + "_categorie_image", String.class));
        entity.setCategorieActive(converter.fromRow(row, prefix + "_categorie_active", Boolean.class));
        return entity;
    }
}
