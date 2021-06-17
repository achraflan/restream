package com.restream.api.service.impl;

import com.restream.api.domain.Report;
import com.restream.api.repository.ReportRepository;
import com.restream.api.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Report}.
 */
@Service
@Transactional
public class ReportServiceImpl implements ReportService {

    private final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final ReportRepository reportRepository;

    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public Mono<Report> save(Report report) {
        log.debug("Request to save Report : {}", report);
        return reportRepository.save(report);
    }

    @Override
    public Mono<Report> partialUpdate(Report report) {
        log.debug("Request to partially update Report : {}", report);

        return reportRepository
            .findById(report.getId())
            .map(
                existingReport -> {
                    if (report.getMessage() != null) {
                        existingReport.setMessage(report.getMessage());
                    }
                    if (report.getDateCreation() != null) {
                        existingReport.setDateCreation(report.getDateCreation());
                    }

                    return existingReport;
                }
            )
            .flatMap(reportRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Report> findAll(Pageable pageable) {
        log.debug("Request to get all Reports");
        return reportRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return reportRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Report> findOne(Long id) {
        log.debug("Request to get Report : {}", id);
        return reportRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Report : {}", id);
        return reportRepository.deleteById(id);
    }
}
