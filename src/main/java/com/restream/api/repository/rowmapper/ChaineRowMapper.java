package com.restream.api.repository.rowmapper;

import com.restream.api.domain.Chaine;
import com.restream.api.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Chaine}, with proper type conversions.
 */
@Service
public class ChaineRowMapper implements BiFunction<Row, String, Chaine> {

    private final ColumnConverter converter;

    public ChaineRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Chaine} stored in the database.
     */
    @Override
    public Chaine apply(Row row, String prefix) {
        Chaine entity = new Chaine();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setChaineNom(converter.fromRow(row, prefix + "_chaine_nom", String.class));
        entity.setChaineImage(converter.fromRow(row, prefix + "_chaine_image", String.class));
        entity.setChaineActive(converter.fromRow(row, prefix + "_chaine_active", Boolean.class));
        entity.setCategorieId(converter.fromRow(row, prefix + "_categorie_id", Long.class));
        return entity;
    }
}
