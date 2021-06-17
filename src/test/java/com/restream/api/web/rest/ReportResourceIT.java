package com.restream.api.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.restream.api.IntegrationTest;
import com.restream.api.domain.Report;
import com.restream.api.repository.ReportRepository;
import com.restream.api.service.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link ReportResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class ReportResourceIT {

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_CREATION = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_CREATION = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/reports";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Report report;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Report createEntity(EntityManager em) {
        Report report = new Report().message(DEFAULT_MESSAGE).dateCreation(DEFAULT_DATE_CREATION);
        return report;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Report createUpdatedEntity(EntityManager em) {
        Report report = new Report().message(UPDATED_MESSAGE).dateCreation(UPDATED_DATE_CREATION);
        return report;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Report.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        report = createEntity(em);
    }

    @Test
    void createReport() throws Exception {
        int databaseSizeBeforeCreate = reportRepository.findAll().collectList().block().size();
        // Create the Report
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(report))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeCreate + 1);
        Report testReport = reportList.get(reportList.size() - 1);
        assertThat(testReport.getMessage()).isEqualTo(DEFAULT_MESSAGE);
        assertThat(testReport.getDateCreation()).isEqualTo(DEFAULT_DATE_CREATION);
    }

    @Test
    void createReportWithExistingId() throws Exception {
        // Create the Report with an existing ID
        report.setId(1L);

        int databaseSizeBeforeCreate = reportRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(report))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkMessageIsRequired() throws Exception {
        int databaseSizeBeforeTest = reportRepository.findAll().collectList().block().size();
        // set the field null
        report.setMessage(null);

        // Create the Report, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(report))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllReports() {
        // Initialize the database
        reportRepository.save(report).block();

        // Get all the reportList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(report.getId().intValue()))
            .jsonPath("$.[*].message")
            .value(hasItem(DEFAULT_MESSAGE))
            .jsonPath("$.[*].dateCreation")
            .value(hasItem(DEFAULT_DATE_CREATION.toString()));
    }

    @Test
    void getReport() {
        // Initialize the database
        reportRepository.save(report).block();

        // Get the report
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, report.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(report.getId().intValue()))
            .jsonPath("$.message")
            .value(is(DEFAULT_MESSAGE))
            .jsonPath("$.dateCreation")
            .value(is(DEFAULT_DATE_CREATION.toString()));
    }

    @Test
    void getNonExistingReport() {
        // Get the report
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewReport() throws Exception {
        // Initialize the database
        reportRepository.save(report).block();

        int databaseSizeBeforeUpdate = reportRepository.findAll().collectList().block().size();

        // Update the report
        Report updatedReport = reportRepository.findById(report.getId()).block();
        updatedReport.message(UPDATED_MESSAGE).dateCreation(UPDATED_DATE_CREATION);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedReport.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedReport))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeUpdate);
        Report testReport = reportList.get(reportList.size() - 1);
        assertThat(testReport.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testReport.getDateCreation()).isEqualTo(UPDATED_DATE_CREATION);
    }

    @Test
    void putNonExistingReport() throws Exception {
        int databaseSizeBeforeUpdate = reportRepository.findAll().collectList().block().size();
        report.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, report.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(report))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchReport() throws Exception {
        int databaseSizeBeforeUpdate = reportRepository.findAll().collectList().block().size();
        report.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(report))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamReport() throws Exception {
        int databaseSizeBeforeUpdate = reportRepository.findAll().collectList().block().size();
        report.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(report))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateReportWithPatch() throws Exception {
        // Initialize the database
        reportRepository.save(report).block();

        int databaseSizeBeforeUpdate = reportRepository.findAll().collectList().block().size();

        // Update the report using partial update
        Report partialUpdatedReport = new Report();
        partialUpdatedReport.setId(report.getId());

        partialUpdatedReport.message(UPDATED_MESSAGE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedReport.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedReport))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeUpdate);
        Report testReport = reportList.get(reportList.size() - 1);
        assertThat(testReport.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testReport.getDateCreation()).isEqualTo(DEFAULT_DATE_CREATION);
    }

    @Test
    void fullUpdateReportWithPatch() throws Exception {
        // Initialize the database
        reportRepository.save(report).block();

        int databaseSizeBeforeUpdate = reportRepository.findAll().collectList().block().size();

        // Update the report using partial update
        Report partialUpdatedReport = new Report();
        partialUpdatedReport.setId(report.getId());

        partialUpdatedReport.message(UPDATED_MESSAGE).dateCreation(UPDATED_DATE_CREATION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedReport.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedReport))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeUpdate);
        Report testReport = reportList.get(reportList.size() - 1);
        assertThat(testReport.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testReport.getDateCreation()).isEqualTo(UPDATED_DATE_CREATION);
    }

    @Test
    void patchNonExistingReport() throws Exception {
        int databaseSizeBeforeUpdate = reportRepository.findAll().collectList().block().size();
        report.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, report.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(report))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchReport() throws Exception {
        int databaseSizeBeforeUpdate = reportRepository.findAll().collectList().block().size();
        report.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(report))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamReport() throws Exception {
        int databaseSizeBeforeUpdate = reportRepository.findAll().collectList().block().size();
        report.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(report))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Report in the database
        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteReport() {
        // Initialize the database
        reportRepository.save(report).block();

        int databaseSizeBeforeDelete = reportRepository.findAll().collectList().block().size();

        // Delete the report
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, report.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Report> reportList = reportRepository.findAll().collectList().block();
        assertThat(reportList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
