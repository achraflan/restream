package com.restream.api.service;

import com.restream.api.domain.Chemin;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Chemin}.
 */
public interface CheminService {
    /**
     * Save a chemin.
     *
     * @param chemin the entity to save.
     * @return the persisted entity.
     */
    Mono<Chemin> save(Chemin chemin);

    /**
     * Partially updates a chemin.
     *
     * @param chemin the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Chemin> partialUpdate(Chemin chemin);

    /**
     * Get all the chemins.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Chemin> findAll(Pageable pageable);

    /**
     * Returns the number of chemins available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" chemin.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Chemin> findOne(Long id);

    /**
     * Delete the "id" chemin.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
