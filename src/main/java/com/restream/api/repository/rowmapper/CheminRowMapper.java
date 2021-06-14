package com.restream.api.repository.rowmapper;

import com.restream.api.domain.Chemin;
import com.restream.api.domain.enumeration.TypeChemin;
import com.restream.api.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Chemin}, with proper type conversions.
 */
@Service
public class CheminRowMapper implements BiFunction<Row, String, Chemin> {

    private final ColumnConverter converter;

    public CheminRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Chemin} stored in the database.
     */
    @Override
    public Chemin apply(Row row, String prefix) {
        Chemin entity = new Chemin();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCheminNon(converter.fromRow(row, prefix + "_chemin_non", String.class));
        entity.setType(converter.fromRow(row, prefix + "_type", TypeChemin.class));
        entity.setCheminValide(converter.fromRow(row, prefix + "_chemin_valide", Boolean.class));
        entity.setCheminMarche(converter.fromRow(row, prefix + "_chemin_marche", Boolean.class));
        entity.setChaineId(converter.fromRow(row, prefix + "_chaine_id", Long.class));
        return entity;
    }
}
