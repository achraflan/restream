package com.restream.api.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.restream.api.domain.Report;
import com.restream.api.repository.rowmapper.CheminRowMapper;
import com.restream.api.repository.rowmapper.ReportRowMapper;
import com.restream.api.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.LocalDate;
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
 * Spring Data SQL reactive custom repository implementation for the Report entity.
 */
@SuppressWarnings("unused")
class ReportRepositoryInternalImpl implements ReportRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CheminRowMapper cheminMapper;
    private final ReportRowMapper reportMapper;

    private static final Table entityTable = Table.aliased("report", EntityManager.ENTITY_ALIAS);
    private static final Table cheminTable = Table.aliased("chemin", "chemin");

    public ReportRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CheminRowMapper cheminMapper,
        ReportRowMapper reportMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.cheminMapper = cheminMapper;
        this.reportMapper = reportMapper;
    }

    @Override
    public Flux<Report> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Report> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Report> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = ReportSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CheminSqlHelper.getColumns(cheminTable, "chemin"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(cheminTable)
            .on(Column.create("chemin_id", entityTable))
            .equals(Column.create("id", cheminTable));

        String select = entityManager.createSelect(selectFrom, Report.class, pageable, criteria);
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
    public Flux<Report> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Report> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Report process(Row row, RowMetadata metadata) {
        Report entity = reportMapper.apply(row, "e");
        entity.setChemin(cheminMapper.apply(row, "chemin"));
        return entity;
    }

    @Override
    public <S extends Report> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Report> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update Report with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(Report entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class ReportSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("message", table, columnPrefix + "_message"));
        columns.add(Column.aliased("date_creation", table, columnPrefix + "_date_creation"));

        columns.add(Column.aliased("chemin_id", table, columnPrefix + "_chemin_id"));
        return columns;
    }
}
