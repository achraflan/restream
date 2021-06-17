package com.restream.api.web.rest;

import com.restream.api.domain.Chaine;
import com.restream.api.repository.ChaineRepository;
import com.restream.api.service.ChaineService;
import com.restream.api.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.restream.api.domain.Chaine}.
 */
@RestController
@RequestMapping("/api")
public class ChaineResource {

    private final Logger log = LoggerFactory.getLogger(ChaineResource.class);

    private static final String ENTITY_NAME = "chaine";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChaineService chaineService;

    private final ChaineRepository chaineRepository;

    public ChaineResource(ChaineService chaineService, ChaineRepository chaineRepository) {
        this.chaineService = chaineService;
        this.chaineRepository = chaineRepository;
    }

    /**
     * {@code POST  /chaines} : Create a new chaine.
     *
     * @param chaine the chaine to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new chaine, or with status {@code 400 (Bad Request)} if the chaine has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/chaines")
    public Mono<ResponseEntity<Chaine>> createChaine(@RequestBody Chaine chaine) throws URISyntaxException {
        log.debug("REST request to save Chaine : {}", chaine);
        if (chaine.getId() != null) {
            throw new BadRequestAlertException("A new chaine cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return chaineService
            .save(chaine)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/chaines/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /chaines/:id} : Updates an existing chaine.
     *
     * @param id the id of the chaine to save.
     * @param chaine the chaine to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chaine,
     * or with status {@code 400 (Bad Request)} if the chaine is not valid,
     * or with status {@code 500 (Internal Server Error)} if the chaine couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/chaines/{id}")
    public Mono<ResponseEntity<Chaine>> updateChaine(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Chaine chaine
    ) throws URISyntaxException {
        log.debug("REST request to update Chaine : {}, {}", id, chaine);
        if (chaine.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chaine.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return chaineRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return chaineService
                        .save(chaine)
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
     * {@code PATCH  /chaines/:id} : Partial updates given fields of an existing chaine, field will ignore if it is null
     *
     * @param id the id of the chaine to save.
     * @param chaine the chaine to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chaine,
     * or with status {@code 400 (Bad Request)} if the chaine is not valid,
     * or with status {@code 404 (Not Found)} if the chaine is not found,
     * or with status {@code 500 (Internal Server Error)} if the chaine couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/chaines/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<Chaine>> partialUpdateChaine(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Chaine chaine
    ) throws URISyntaxException {
        log.debug("REST request to partial update Chaine partially : {}, {}", id, chaine);
        if (chaine.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chaine.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return chaineRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<Chaine> result = chaineService.partialUpdate(chaine);

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
     * {@code GET  /chaines} : get all the chaines.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of chaines in body.
     */
    @GetMapping("/chaines")
    public Mono<ResponseEntity<List<Chaine>>> getAllChaines(
        Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Chaines");
        return chaineService
            .countAll()
            .zipWith(chaineService.findAll(pageable).collectList())
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
     * {@code GET  /chaines/:id} : get the "id" chaine.
     *
     * @param id the id of the chaine to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the chaine, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/chaines/{id}")
    public Mono<ResponseEntity<Chaine>> getChaine(@PathVariable Long id) {
        log.debug("REST request to get Chaine : {}", id);
        Mono<Chaine> chaine = chaineService.findOne(id);
        return ResponseUtil.wrapOrNotFound(chaine);
    }

    /**
     * {@code DELETE  /chaines/:id} : delete the "id" chaine.
     *
     * @param id the id of the chaine to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/chaines/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteChaine(@PathVariable Long id) {
        log.debug("REST request to delete Chaine : {}", id);
        return chaineService
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
