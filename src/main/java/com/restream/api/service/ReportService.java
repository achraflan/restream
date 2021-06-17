package com.restream.api.service;

import com.restream.api.domain.Report;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Report}.
 */
public interface ReportService {
    /**
     * Save a report.
     *
     * @param report the entity to save.
     * @return the persisted entity.
     */
    Mono<Report> save(Report report);

    /**
     * Partially updates a report.
     *
     * @param report the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Report> partialUpdate(Report report);

    /**
     * Get all the reports.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Report> findAll(Pageable pageable);

    /**
     * Returns the number of reports available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" report.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Report> findOne(Long id);

    /**
     * Delete the "id" report.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
