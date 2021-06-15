package com.restream.api.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.restream.api.domain.Chaine;
import com.restream.api.domain.Tag;
import com.restream.api.repository.CheminRepository;
import com.restream.api.repository.rowmapper.CategorieRowMapper;
import com.restream.api.repository.rowmapper.ChaineRowMapper;
import com.restream.api.service.EntityManager;
import com.restream.api.service.EntityManager.LinkTable;
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
 * Spring Data SQL reactive custom repository implementation for the Chaine entity.
 */
@SuppressWarnings("unused")
class ChaineRepositoryInternalImpl implements ChaineRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CategorieRowMapper categorieMapper;
    private final ChaineRowMapper chaineMapper;

    private static final Table entityTable = Table.aliased("chaine", EntityManager.ENTITY_ALIAS);
    private static final Table categorieTable = Table.aliased("categorie", "categorie");

    private static final EntityManager.LinkTable tagsLink = new LinkTable("rel_chaine__tags", "chaine_id", "tags_id");
    private final CheminRepository cheminRepository;

    public ChaineRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CategorieRowMapper categorieMapper,
        ChaineRowMapper chaineMapper,
        CheminRepository cheminRepository
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.categorieMapper = categorieMapper;
        this.chaineMapper = chaineMapper;
        this.cheminRepository = cheminRepository;
    }

    @Override
    public Flux<Chaine> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Chaine> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Chaine> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = ChaineSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CategorieSqlHelper.getColumns(categorieTable, "categorie"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(categorieTable)
            .on(Column.create("categorie_id", entityTable))
            .equals(Column.create("id", categorieTable));

        String select = entityManager.createSelect(selectFrom, Chaine.class, pageable, criteria);
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
    public Flux<Chaine> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Chaine> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    @Override
    public Mono<Chaine> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Chaine> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Chaine> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Chaine process(Row row, RowMetadata metadata) {
        Chaine entity = chaineMapper.apply(row, "e");
        entity.setCategorie(categorieMapper.apply(row, "categorie"));
        // entity.setChemins();
        // cheminRepository.findByChaine(entity.getId()).then(System.out::print);
        cheminRepository
            .findByChaine(entity.getId())
            .subscribe(
                value -> entity.addChemin(value),
                error -> error.printStackTrace(),
                () -> System.out.print("completed without a value")
            );
        return entity;
    }

    @Override
    public <S extends Chaine> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Chaine> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity).flatMap(savedEntity -> updateRelations(savedEntity));
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update Chaine with id = " + entity.getId());
                        }
                        return entity;
                    }
                )
                .then(updateRelations(entity));
        }
    }

    @Override
    public Mono<Integer> update(Chaine entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId)
            .then(r2dbcEntityTemplate.delete(Chaine.class).matching(query(where("id").is(entityId))).all().then());
    }

    protected <S extends Chaine> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager.updateLinkTable(tagsLink, entity.getId(), entity.getTags().stream().map(Tag::getId)).then();
        return result.thenReturn(entity);
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(tagsLink, entityId);
    }
}

class ChaineSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("chaine_nom", table, columnPrefix + "_chaine_nom"));
        columns.add(Column.aliased("chaine_image", table, columnPrefix + "_chaine_image"));
        columns.add(Column.aliased("chaine_active", table, columnPrefix + "_chaine_active"));

        columns.add(Column.aliased("categorie_id", table, columnPrefix + "_categorie_id"));
        return columns;
    }
}
