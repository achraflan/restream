package com.restream.api.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.restream.api.IntegrationTest;
import com.restream.api.domain.Chemin;
import com.restream.api.domain.enumeration.TypeChemin;
import com.restream.api.repository.CheminRepository;
import com.restream.api.service.EntityManager;
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
 * Integration tests for the {@link CheminResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class CheminResourceIT {

    private static final String DEFAULT_CHEMIN_NON = "AAAAAAAAAA";
    private static final String UPDATED_CHEMIN_NON = "BBBBBBBBBB";

    private static final TypeChemin DEFAULT_TYPE = TypeChemin.Direct;
    private static final TypeChemin UPDATED_TYPE = TypeChemin.Embed;

    private static final Boolean DEFAULT_CHEMIN_VALIDE = false;
    private static final Boolean UPDATED_CHEMIN_VALIDE = true;

    private static final Boolean DEFAULT_CHEMIN_MARCHE = false;
    private static final Boolean UPDATED_CHEMIN_MARCHE = true;

    private static final String ENTITY_API_URL = "/api/chemins";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CheminRepository cheminRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Chemin chemin;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Chemin createEntity(EntityManager em) {
        Chemin chemin = new Chemin()
            .cheminNon(DEFAULT_CHEMIN_NON)
            .type(DEFAULT_TYPE)
            .cheminValide(DEFAULT_CHEMIN_VALIDE)
            .cheminMarche(DEFAULT_CHEMIN_MARCHE);
        return chemin;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Chemin createUpdatedEntity(EntityManager em) {
        Chemin chemin = new Chemin()
            .cheminNon(UPDATED_CHEMIN_NON)
            .type(UPDATED_TYPE)
            .cheminValide(UPDATED_CHEMIN_VALIDE)
            .cheminMarche(UPDATED_CHEMIN_MARCHE);
        return chemin;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Chemin.class).block();
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
        chemin = createEntity(em);
    }

    @Test
    void createChemin() throws Exception {
        int databaseSizeBeforeCreate = cheminRepository.findAll().collectList().block().size();
        // Create the Chemin
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(chemin))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Chemin in the database
        List<Chemin> cheminList = cheminRepository.findAll().collectList().block();
        assertThat(cheminList).hasSize(databaseSizeBeforeCreate + 1);
        Chemin testChemin = cheminList.get(cheminList.size() - 1);
        assertThat(testChemin.getCheminNon()).isEqualTo(DEFAULT_CHEMIN_NON);
        assertThat(testChemin.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testChemin.getCheminValide()).isEqualTo(DEFAULT_CHEMIN_VALIDE);
        assertThat(testChemin.getCheminMarche()).isEqualTo(DEFAULT_CHEMIN_MARCHE);
    }

    @Test
    void createCheminWithExistingId() throws Exception {
        // Create the Chemin with an existing ID
        chemin.setId(1L);

        int databaseSizeBeforeCreate = cheminRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(chemin))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Chemin in the database
        List<Chemin> cheminList = cheminRepository.findAll().collectList().block();
        assertThat(cheminList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllChemins() {
        // Initialize the database
        cheminRepository.save(chemin).block();

        // Get all the cheminList
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
            .value(hasItem(chemin.getId().intValue()))
            .jsonPath("$.[*].cheminNon")
            .value(hasItem(DEFAULT_CHEMIN_NON))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE.toString()))
            .jsonPath("$.[*].cheminValide")
            .value(hasItem(DEFAULT_CHEMIN_VALIDE.booleanValue()))
            .jsonPath("$.[*].cheminMarche")
            .value(hasItem(DEFAULT_CHEMIN_MARCHE.booleanValue()));
    }

    @Test
    void getChemin() {
        // Initialize the database
        cheminRepository.save(chemin).block();

        // Get the chemin
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, chemin.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(chemin.getId().intValue()))
            .jsonPath("$.cheminNon")
            .value(is(DEFAULT_CHEMIN_NON))
            .jsonPath("$.type")
            .value(is(DEFAULT_TYPE.toString()))
            .jsonPath("$.cheminValide")
            .value(is(DEFAULT_CHEMIN_VALIDE.booleanValue()))
            .jsonPath("$.cheminMarche")
            .value(is(DEFAULT_CHEMIN_MARCHE.booleanValue()));
    }

    @Test
    void getNonExistingChemin() {
        // Get the chemin
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewChemin() throws Exception {
        // Initialize the database
        cheminRepository.save(chemin).block();

        int databaseSizeBeforeUpdate = cheminRepository.findAll().collectList().block().size();

        // Update the chemin
        Chemin updatedChemin = cheminRepository.findById(chemin.getId()).block();
        updatedChemin
            .cheminNon(UPDATED_CHEMIN_NON)
            .type(UPDATED_TYPE)
            .cheminValide(UPDATED_CHEMIN_VALIDE)
            .cheminMarche(UPDATED_CHEMIN_MARCHE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedChemin.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedChemin))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Chemin in the database
        List<Chemin> cheminList = cheminRepository.findAll().collectList().block();
        assertThat(cheminList).hasSize(databaseSizeBeforeUpdate);
        Chemin testChemin = cheminList.get(cheminList.size() - 1);
        assertThat(testChemin.getCheminNon()).isEqualTo(UPDATED_CHEMIN_NON);
        assertThat(testChemin.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testChemin.getCheminValide()).isEqualTo(UPDATED_CHEMIN_VALIDE);
        assertThat(testChemin.getCheminMarche()).isEqualTo(UPDATED_CHEMIN_MARCHE);
    }

    @Test
    void putNonExistingChemin() throws Exception {
        int databaseSizeBeforeUpdate = cheminRepository.findAll().collectList().block().size();
        chemin.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, chemin.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(chemin))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Chemin in the database
        List<Chemin> cheminList = cheminRepository.findAll().collectList().block();
        assertThat(cheminList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchChemin() throws Exception {
        int databaseSizeBeforeUpdate = cheminRepository.findAll().collectList().block().size();
        chemin.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(chemin))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Chemin in the database
        List<Chemin> cheminList = cheminRepository.findAll().collectList().block();
        assertThat(cheminList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamChemin() throws Exception {
        int databaseSizeBeforeUpdate = cheminRepository.findAll().collectList().block().size();
        chemin.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(chemin))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Chemin in the database
        List<Chemin> cheminList = cheminRepository.findAll().collectList().block();
        assertThat(cheminList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCheminWithPatch() throws Exception {
        // Initialize the database
        cheminRepository.save(chemin).block();

        int databaseSizeBeforeUpdate = cheminRepository.findAll().collectList().block().size();

        // Update the chemin using partial update
        Chemin partialUpdatedChemin = new Chemin();
        partialUpdatedChemin.setId(chemin.getId());

        partialUpdatedChemin.cheminValide(UPDATED_CHEMIN_VALIDE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedChemin.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedChemin))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Chemin in the database
        List<Chemin> cheminList = cheminRepository.findAll().collectList().block();
        assertThat(cheminList).hasSize(databaseSizeBeforeUpdate);
        Chemin testChemin = cheminList.get(cheminList.size() - 1);
        assertThat(testChemin.getCheminNon()).isEqualTo(DEFAULT_CHEMIN_NON);
        assertThat(testChemin.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testChemin.getCheminValide()).isEqualTo(UPDATED_CHEMIN_VALIDE);
        assertThat(testChemin.getCheminMarche()).isEqualTo(DEFAULT_CHEMIN_MARCHE);
    }

    @Test
    void fullUpdateCheminWithPatch() throws Exception {
        // Initialize the database
        cheminRepository.save(chemin).block();

        int databaseSizeBeforeUpdate = cheminRepository.findAll().collectList().block().size();

        // Update the chemin using partial update
        Chemin partialUpdatedChemin = new Chemin();
        partialUpdatedChemin.setId(chemin.getId());

        partialUpdatedChemin
            .cheminNon(UPDATED_CHEMIN_NON)
            .type(UPDATED_TYPE)
            .cheminValide(UPDATED_CHEMIN_VALIDE)
            .cheminMarche(UPDATED_CHEMIN_MARCHE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedChemin.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedChemin))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Chemin in the database
        List<Chemin> cheminList = cheminRepository.findAll().collectList().block();
        assertThat(cheminList).hasSize(databaseSizeBeforeUpdate);
        Chemin testChemin = cheminList.get(cheminList.size() - 1);
        assertThat(testChemin.getCheminNon()).isEqualTo(UPDATED_CHEMIN_NON);
        assertThat(testChemin.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testChemin.getCheminValide()).isEqualTo(UPDATED_CHEMIN_VALIDE);
        assertThat(testChemin.getCheminMarche()).isEqualTo(UPDATED_CHEMIN_MARCHE);
    }

    @Test
    void patchNonExistingChemin() throws Exception {
        int databaseSizeBeforeUpdate = cheminRepository.findAll().collectList().block().size();
        chemin.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, chemin.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(chemin))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Chemin in the database
        List<Chemin> cheminList = cheminRepository.findAll().collectList().block();
        assertThat(cheminList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchChemin() throws Exception {
        int databaseSizeBeforeUpdate = cheminRepository.findAll().collectList().block().size();
        chemin.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(chemin))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Chemin in the database
        List<Chemin> cheminList = cheminRepository.findAll().collectList().block();
        assertThat(cheminList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamChemin() throws Exception {
        int databaseSizeBeforeUpdate = cheminRepository.findAll().collectList().block().size();
        chemin.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(chemin))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Chemin in the database
        List<Chemin> cheminList = cheminRepository.findAll().collectList().block();
        assertThat(cheminList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteChemin() {
        // Initialize the database
        cheminRepository.save(chemin).block();

        int databaseSizeBeforeDelete = cheminRepository.findAll().collectList().block().size();

        // Delete the chemin
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, chemin.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Chemin> cheminList = cheminRepository.findAll().collectList().block();
        assertThat(cheminList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
