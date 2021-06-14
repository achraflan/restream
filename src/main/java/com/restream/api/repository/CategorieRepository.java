package com.restream.api.repository;

import com.restream.api.domain.Categorie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Categorie entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CategorieRepository extends R2dbcRepository<Categorie, Long>, CategorieRepositoryInternal {
    Flux<Categorie> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<Categorie> findAll();

    @Override
    Mono<Categorie> findById(Long id);

    @Override
    <S extends Categorie> Mono<S> save(S entity);
}

interface CategorieRepositoryInternal {
    <S extends Categorie> Mono<S> insert(S entity);
    <S extends Categorie> Mono<S> save(S entity);
    Mono<Integer> update(Categorie entity);

    Flux<Categorie> findAll();
    Mono<Categorie> findById(Long id);
    Flux<Categorie> findAllBy(Pageable pageable);
    Flux<Categorie> findAllBy(Pageable pageable, Criteria criteria);
}
