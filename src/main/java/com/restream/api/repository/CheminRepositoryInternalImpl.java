package com.restream.api.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.restream.api.domain.Chemin;
import com.restream.api.domain.enumeration.TypeChemin;
import com.restream.api.repository.rowmapper.ChaineRowMapper;
import com.restream.api.repository.rowmapper.CheminRowMapper;
import com.restream.api.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Chemin entity.
 */
@SuppressWarnings("unused")
class CheminRepositoryInternalImpl implements CheminRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ChaineRowMapper chaineMapper;
    private final CheminRowMapper cheminMapper;

    private static final Table entityTable = Table.aliased("chemin", EntityManager.ENTITY_ALIAS);
    private static final Table chaineTable = Table.aliased("chaine", "chaine");

    public CheminRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ChaineRowMapper chaineMapper,
        CheminRowMapper cheminMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.chaineMapper = chaineMapper;
        this.cheminMapper = cheminMapper;
    }

    @Override
    public Flux<Chemin> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Chemin> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Chemin> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = CheminSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ChaineSqlHelper.getColumns(chaineTable, "chaine"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(chaineTable)
            .on(Column.create("chaine_id", entityTable))
            .equals(Column.create("id", chaineTable));

        String select = entityManager.createSelect(selectFrom, Chemin.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(
                crit ->
                    new StringBuilder(select)
                        .append(" ")
                        .append("WHERE")
                        .append(" ")
                        .append(alias)
                        .append(".")
                        .append(crit.toString())
                        .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<Chemin> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Chemin> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Chemin process(Row row, RowMetadata metadata) {
        Chemin entity = cheminMapper.apply(row, "e");
        entity.setChaine(chaineMapper.apply(row, "chaine"));
        return entity;
    }

    @Override
    public <S extends Chemin> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Chemin> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update Chemin with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(Chemin entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class CheminSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("chemin_non", table, columnPrefix + "_chemin_non"));
        columns.add(Column.aliased("type", table, columnPrefix + "_type"));
        columns.add(Column.aliased("chemin_valide", table, columnPrefix + "_chemin_valide"));
        columns.add(Column.aliased("chemin_marche", table, columnPrefix + "_chemin_marche"));
        columns.add(Column.aliased("chemin_description", table, columnPrefix + "_chemin_description"));

        columns.add(Column.aliased("chaine_id", table, columnPrefix + "_chaine_id"));
        return columns;
    }
}
