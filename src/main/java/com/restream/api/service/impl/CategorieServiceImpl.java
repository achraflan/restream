package com.restream.api.service.impl;

import com.restream.api.domain.Categorie;
import com.restream.api.repository.CategorieRepository;
import com.restream.api.service.CategorieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Categorie}.
 */
@Service
@Transactional
public class CategorieServiceImpl implements CategorieService {

    private final Logger log = LoggerFactory.getLogger(CategorieServiceImpl.class);

    private final CategorieRepository categorieRepository;

    public CategorieServiceImpl(CategorieRepository categorieRepository) {
        this.categorieRepository = categorieRepository;
    }

    @Override
    public Mono<Categorie> save(Categorie categorie) {
        log.debug("Request to save Categorie : {}", categorie);
        return categorieRepository.save(categorie);
    }

    @Override
    public Mono<Categorie> partialUpdate(Categorie categorie) {
        log.debug("Request to partially update Categorie : {}", categorie);

        return categorieRepository
            .findById(categorie.getId())
            .map(
                existingCategorie -> {
                    if (categorie.getCategorieNom() != null) {
                        existingCategorie.setCategorieNom(categorie.getCategorieNom());
                    }
                    if (categorie.getCategorieImage() != null) {
                        existingCategorie.setCategorieImage(categorie.getCategorieImage());
                    }
                    if (categorie.getCategorieActive() != null) {
                        existingCategorie.setCategorieActive(categorie.getCategorieActive());
                    }

                    return existingCategorie;
                }
            )
            .flatMap(categorieRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Categorie> findAll(Pageable pageable) {
        log.debug("Request to get all Categories");
        return categorieRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return categorieRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Categorie> findOne(Long id) {
        log.debug("Request to get Categorie : {}", id);
        return categorieRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Categorie : {}", id);
        return categorieRepository.deleteById(id);
    }
}
