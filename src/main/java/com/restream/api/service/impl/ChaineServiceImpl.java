package com.restream.api.service.impl;

import com.restream.api.domain.Chaine;
import com.restream.api.repository.ChaineRepository;
import com.restream.api.service.ChaineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Chaine}.
 */
@Service
@Transactional
public class ChaineServiceImpl implements ChaineService {

    private final Logger log = LoggerFactory.getLogger(ChaineServiceImpl.class);

    private final ChaineRepository chaineRepository;

    public ChaineServiceImpl(ChaineRepository chaineRepository) {
        this.chaineRepository = chaineRepository;
    }

    @Override
    public Mono<Chaine> save(Chaine chaine) {
        log.debug("Request to save Chaine : {}", chaine);
        return chaineRepository.save(chaine);
    }

    @Override
    public Mono<Chaine> partialUpdate(Chaine chaine) {
        log.debug("Request to partially update Chaine : {}", chaine);

        return chaineRepository
            .findById(chaine.getId())
            .map(
                existingChaine -> {
                    if (chaine.getChaineNom() != null) {
                        existingChaine.setChaineNom(chaine.getChaineNom());
                    }
                    if (chaine.getChaineImage() != null) {
                        existingChaine.setChaineImage(chaine.getChaineImage());
                    }
                    if (chaine.getChaineActive() != null) {
                        existingChaine.setChaineActive(chaine.getChaineActive());
                    }

                    return existingChaine;
                }
            )
            .flatMap(chaineRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Chaine> findAll(Pageable pageable) {
        log.debug("Request to get all Chaines");
        return chaineRepository.findAllBy(pageable);
    }

    public Flux<Chaine> findAllWithEagerRelationships(Pageable pageable) {
        return chaineRepository.findAllWithEagerRelationships(pageable);
    }

    public Mono<Long> countAll() {
        return chaineRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Chaine> findOne(Long id) {
        log.debug("Request to get Chaine : {}", id);
        return chaineRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Chaine : {}", id);
        return chaineRepository.deleteById(id);
    }
}
