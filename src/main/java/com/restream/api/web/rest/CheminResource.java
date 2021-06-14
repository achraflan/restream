package com.restream.api.web.rest;

import com.restream.api.domain.Chemin;
import com.restream.api.repository.CheminRepository;
import com.restream.api.service.CheminService;
import com.restream.api.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.restream.api.domain.Chemin}.
 */
@RestController
@RequestMapping("/api")
public class CheminResource {

    private final Logger log = LoggerFactory.getLogger(CheminResource.class);

    private static final String ENTITY_NAME = "chemin";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CheminService cheminService;

    private final CheminRepository cheminRepository;

    public CheminResource(CheminService cheminService, CheminRepository cheminRepository) {
        this.cheminService = cheminService;
        this.cheminRepository = cheminRepository;
    }

    /**
     * {@code POST  /chemins} : Create a new chemin.
     *
     * @param chemin the chemin to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new chemin, or with status {@code 400 (Bad Request)} if the chemin has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/chemins")
    public Mono<ResponseEntity<Chemin>> createChemin(@RequestBody Chemin chemin) throws URISyntaxException {
        log.debug("REST request to save Chemin : {}", chemin);
        if (chemin.getId() != null) {
            throw new BadRequestAlertException("A new chemin cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return cheminService
            .save(chemin)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/chemins/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /chemins/:id} : Updates an existing chemin.
     *
     * @param id the id of the chemin to save.
     * @param chemin the chemin to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chemin,
     * or with status {@code 400 (Bad Request)} if the chemin is not valid,
     * or with status {@code 500 (Internal Server Error)} if the chemin couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/chemins/{id}")
    public Mono<ResponseEntity<Chemin>> updateChemin(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Chemin chemin
    ) throws URISyntaxException {
        log.debug("REST request to update Chemin : {}, {}", id, chemin);
        if (chemin.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chemin.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return cheminRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return cheminService
                        .save(chemin)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            result ->
                                ResponseEntity
                                    .ok()
                                    .headers(
                                        HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString())
                                    )
                                    .body(result)
                        );
                }
            );
    }

    /**
     * {@code PATCH  /chemins/:id} : Partial updates given fields of an existing chemin, field will ignore if it is null
     *
     * @param id the id of the chemin to save.
     * @param chemin the chemin to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chemin,
     * or with status {@code 400 (Bad Request)} if the chemin is not valid,
     * or with status {@code 404 (Not Found)} if the chemin is not found,
     * or with status {@code 500 (Internal Server Error)} if the chemin couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/chemins/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<Chemin>> partialUpdateChemin(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Chemin chemin
    ) throws URISyntaxException {
        log.debug("REST request to partial update Chemin partially : {}, {}", id, chemin);
        if (chemin.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chemin.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return cheminRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<Chemin> result = cheminService.partialUpdate(chemin);

                    return result
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            res ->
                                ResponseEntity
                                    .ok()
                                    .headers(
                                        HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString())
                                    )
                                    .body(res)
                        );
                }
            );
    }

    /**
     * {@code GET  /chemins} : get all the chemins.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of chemins in body.
     */
    @GetMapping("/chemins")
    public Mono<ResponseEntity<List<Chemin>>> getAllChemins(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Chemins");
        return cheminService
            .countAll()
            .zipWith(cheminService.findAll(pageable).collectList())
            .map(
                countWithEntities -> {
                    return ResponseEntity
                        .ok()
                        .headers(
                            PaginationUtil.generatePaginationHttpHeaders(
                                UriComponentsBuilder.fromHttpRequest(request),
                                new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                            )
                        )
                        .body(countWithEntities.getT2());
                }
            );
    }

    /**
     * {@code GET  /chemins/:id} : get the "id" chemin.
     *
     * @param id the id of the chemin to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the chemin, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/chemins/{id}")
    public Mono<ResponseEntity<Chemin>> getChemin(@PathVariable Long id) {
        log.debug("REST request to get Chemin : {}", id);
        Mono<Chemin> chemin = cheminService.findOne(id);
        return ResponseUtil.wrapOrNotFound(chemin);
    }

    /**
     * {@code DELETE  /chemins/:id} : delete the "id" chemin.
     *
     * @param id the id of the chemin to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/chemins/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteChemin(@PathVariable Long id) {
        log.debug("REST request to delete Chemin : {}", id);
        return cheminService
            .delete(id)
            .map(
                result ->
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
            );
    }
}
