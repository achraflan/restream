package com.restream.api.repository;

import com.restream.api.domain.Chemin;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Chemin entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CheminRepository extends R2dbcRepository<Chemin, Long>, CheminRepositoryInternal {
    Flux<Chemin> findAllBy(Pageable pageable);

    @Query("SELECT * FROM chemin entity WHERE entity.chaine_id = :id")
    Flux<Chemin> findByChaine(Long id);

    @Query("SELECT * FROM chemin entity WHERE entity.chaine_id IS NULL")
    Flux<Chemin> findAllWhereChaineIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Chemin> findAll();

    @Override
    Mono<Chemin> findById(Long id);

    @Override
    <S extends Chemin> Mono<S> save(S entity);
}

interface CheminRepositoryInternal {
    <S extends Chemin> Mono<S> insert(S entity);
    <S extends Chemin> Mono<S> save(S entity);
    Mono<Integer> update(Chemin entity);

    Flux<Chemin> findAll();
    Mono<Chemin> findById(Long id);
    Flux<Chemin> findAllBy(Pageable pageable);
    Flux<Chemin> findAllBy(Pageable pageable, Criteria criteria);
}
