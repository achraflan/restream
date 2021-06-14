package com.restream.api.repository;

import com.restream.api.domain.Chaine;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Chaine entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChaineRepository extends R2dbcRepository<Chaine, Long>, ChaineRepositoryInternal {
    Flux<Chaine> findAllBy(Pageable pageable);

    @Override
    Mono<Chaine> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Chaine> findAllWithEagerRelationships();

    @Override
    Flux<Chaine> findAllWithEagerRelationships(Pageable page);

    @Override
    Mono<Void> deleteById(Long id);

    @Query("SELECT * FROM chaine entity WHERE entity.categorie_id = :id")
    Flux<Chaine> findByCategorie(Long id);

    @Query("SELECT * FROM chaine entity WHERE entity.categorie_id IS NULL")
    Flux<Chaine> findAllWhereCategorieIsNull();

    @Query(
        "SELECT entity.* FROM chaine entity JOIN rel_chaine__tags joinTable ON entity.id = joinTable.chaine_id WHERE joinTable.tags_id = :id"
    )
    Flux<Chaine> findByTags(Long id);

    // just to avoid having unambigous methods
    @Override
    Flux<Chaine> findAll();

    @Override
    Mono<Chaine> findById(Long id);

    @Override
    <S extends Chaine> Mono<S> save(S entity);
}

interface ChaineRepositoryInternal {
    <S extends Chaine> Mono<S> insert(S entity);
    <S extends Chaine> Mono<S> save(S entity);
    Mono<Integer> update(Chaine entity);

    Flux<Chaine> findAll();
    Mono<Chaine> findById(Long id);
    Flux<Chaine> findAllBy(Pageable pageable);
    Flux<Chaine> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Chaine> findOneWithEagerRelationships(Long id);

    Flux<Chaine> findAllWithEagerRelationships();

    Flux<Chaine> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
