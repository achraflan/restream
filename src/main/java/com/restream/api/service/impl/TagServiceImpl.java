package com.restream.api.service.impl;

import com.restream.api.domain.Tag;
import com.restream.api.repository.TagRepository;
import com.restream.api.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Tag}.
 */
@Service
@Transactional
public class TagServiceImpl implements TagService {

    private final Logger log = LoggerFactory.getLogger(TagServiceImpl.class);

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Mono<Tag> save(Tag tag) {
        log.debug("Request to save Tag : {}", tag);
        return tagRepository.save(tag);
    }

    @Override
    public Mono<Tag> partialUpdate(Tag tag) {
        log.debug("Request to partially update Tag : {}", tag);

        return tagRepository
            .findById(tag.getId())
            .map(
                existingTag -> {
                    if (tag.getTagNom() != null) {
                        existingTag.setTagNom(tag.getTagNom());
                    }

                    return existingTag;
                }
            )
            .flatMap(tagRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Tag> findAll(Pageable pageable) {
        log.debug("Request to get all Tags");
        return tagRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return tagRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Tag> findOne(Long id) {
        log.debug("Request to get Tag : {}", id);
        return tagRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Tag : {}", id);
        return tagRepository.deleteById(id);
    }
}
