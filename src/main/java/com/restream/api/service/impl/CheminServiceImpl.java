package com.restream.api.service.impl;

import com.restream.api.domain.Chemin;
import com.restream.api.repository.CheminRepository;
import com.restream.api.service.CheminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Chemin}.
 */
@Service
@Transactional
public class CheminServiceImpl implements CheminService {

    private final Logger log = LoggerFactory.getLogger(CheminServiceImpl.class);

    private final CheminRepository cheminRepository;

    public CheminServiceImpl(CheminRepository cheminRepository) {
        this.cheminRepository = cheminRepository;
    }

    @Override
    public Mono<Chemin> save(Chemin chemin) {
        log.debug("Request to save Chemin : {}", chemin);
        return cheminRepository.save(chemin);
    }

    @Override
    public Mono<Chemin> partialUpdate(Chemin chemin) {
        log.debug("Request to partially update Chemin : {}", chemin);

        return cheminRepository
            .findById(chemin.getId())
            .map(
                existingChemin -> {
                    if (chemin.getCheminNon() != null) {
                        existingChemin.setCheminNon(chemin.getCheminNon());
                    }
                    if (chemin.getType() != null) {
                        existingChemin.setType(chemin.getType());
                    }
                    if (chemin.getCheminValide() != null) {
                        existingChemin.setCheminValide(chemin.getCheminValide());
                    }
                    if (chemin.getCheminMarche() != null) {
                        existingChemin.setCheminMarche(chemin.getCheminMarche());
                    }
                    if (chemin.getCheminDescription() != null) {
                        existingChemin.setCheminDescription(chemin.getCheminDescription());
                    }

                    return existingChemin;
                }
            )
            .flatMap(cheminRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Chemin> findAll(Pageable pageable) {
        log.debug("Request to get all Chemins");
        return cheminRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return cheminRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Chemin> findOne(Long id) {
        log.debug("Request to get Chemin : {}", id);
        return cheminRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Chemin : {}", id);
        return cheminRepository.deleteById(id);
    }
}
