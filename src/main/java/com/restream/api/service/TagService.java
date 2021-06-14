package com.restream.api.service;

import com.restream.api.domain.Tag;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Tag}.
 */
public interface TagService {
    /**
     * Save a tag.
     *
     * @param tag the entity to save.
     * @return the persisted entity.
     */
    Mono<Tag> save(Tag tag);

    /**
     * Partially updates a tag.
     *
     * @param tag the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Tag> partialUpdate(Tag tag);

    /**
     * Get all the tags.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Tag> findAll(Pageable pageable);

    /**
     * Returns the number of tags available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" tag.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Tag> findOne(Long id);

    /**
     * Delete the "id" tag.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
