package com.restream.api.repository;

import com.restream.api.domain.Report;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Report entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReportRepository extends R2dbcRepository<Report, Long>, ReportRepositoryInternal {
    Flux<Report> findAllBy(Pageable pageable);

    @Query("SELECT * FROM report entity WHERE entity.chemin_id = :id")
    Flux<Report> findByChemin(Long id);

    @Query("SELECT * FROM report entity WHERE entity.chemin_id IS NULL")
    Flux<Report> findAllWhereCheminIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Report> findAll();

    @Override
    Mono<Report> findById(Long id);

    @Override
    <S extends Report> Mono<S> save(S entity);
}

interface ReportRepositoryInternal {
    <S extends Report> Mono<S> insert(S entity);
    <S extends Report> Mono<S> save(S entity);
    Mono<Integer> update(Report entity);

    Flux<Report> findAll();
    Mono<Report> findById(Long id);
    Flux<Report> findAllBy(Pageable pageable);
    Flux<Report> findAllBy(Pageable pageable, Criteria criteria);
}
