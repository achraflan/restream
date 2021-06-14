package com.restream.api.service;

import com.restream.api.domain.Chaine;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Chaine}.
 */
public interface ChaineService {
    /**
     * Save a chaine.
     *
     * @param chaine the entity to save.
     * @return the persisted entity.
     */
    Mono<Chaine> save(Chaine chaine);

    /**
     * Partially updates a chaine.
     *
     * @param chaine the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Chaine> partialUpdate(Chaine chaine);

    /**
     * Get all the chaines.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Chaine> findAll(Pageable pageable);

    /**
     * Get all the chaines with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Chaine> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of chaines available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" chaine.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Chaine> findOne(Long id);

    /**
     * Delete the "id" chaine.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
