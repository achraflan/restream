package com.restream.api.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.restream.api.IntegrationTest;
import com.restream.api.domain.Chaine;
import com.restream.api.repository.ChaineRepository;
import com.restream.api.service.ChaineService;
import com.restream.api.service.EntityManager;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link ChaineResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class ChaineResourceIT {

    private static final String DEFAULT_CHAINE_NOM = "AAAAAAAAAA";
    private static final String UPDATED_CHAINE_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_CHAINE_IMAGE = "AAAAAAAAAA";
    private static final String UPDATED_CHAINE_IMAGE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_CHAINE_ACTIVE = false;
    private static final Boolean UPDATED_CHAINE_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/chaines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ChaineRepository chaineRepository;

    @Mock
    private ChaineRepository chaineRepositoryMock;

    @Mock
    private ChaineService chaineServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Chaine chaine;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Chaine createEntity(EntityManager em) {
        Chaine chaine = new Chaine().chaineNom(DEFAULT_CHAINE_NOM).chaineImage(DEFAULT_CHAINE_IMAGE).chaineActive(DEFAULT_CHAINE_ACTIVE);
        return chaine;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Chaine createUpdatedEntity(EntityManager em) {
        Chaine chaine = new Chaine().chaineNom(UPDATED_CHAINE_NOM).chaineImage(UPDATED_CHAINE_IMAGE).chaineActive(UPDATED_CHAINE_ACTIVE);
        return chaine;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_chaine__tags").block();
            em.deleteAll(Chaine.class).block();
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
        chaine = createEntity(em);
    }

    @Test
    void createChaine() throws Exception {
        int databaseSizeBeforeCreate = chaineRepository.findAll().collectList().block().size();
        // Create the Chaine
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(chaine))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Chaine in the database
        List<Chaine> chaineList = chaineRepository.findAll().collectList().block();
        assertThat(chaineList).hasSize(databaseSizeBeforeCreate + 1);
        Chaine testChaine = chaineList.get(chaineList.size() - 1);
        assertThat(testChaine.getChaineNom()).isEqualTo(DEFAULT_CHAINE_NOM);
        assertThat(testChaine.getChaineImage()).isEqualTo(DEFAULT_CHAINE_IMAGE);
        assertThat(testChaine.getChaineActive()).isEqualTo(DEFAULT_CHAINE_ACTIVE);
    }

    @Test
    void createChaineWithExistingId() throws Exception {
        // Create the Chaine with an existing ID
        chaine.setId(1L);

        int databaseSizeBeforeCreate = chaineRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(chaine))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Chaine in the database
        List<Chaine> chaineList = chaineRepository.findAll().collectList().block();
        assertThat(chaineList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllChaines() {
        // Initialize the database
        chaineRepository.save(chaine).block();

        // Get all the chaineList
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
            .value(hasItem(chaine.getId().intValue()))
            .jsonPath("$.[*].chaineNom")
            .value(hasItem(DEFAULT_CHAINE_NOM))
            .jsonPath("$.[*].chaineImage")
            .value(hasItem(DEFAULT_CHAINE_IMAGE))
            .jsonPath("$.[*].chaineActive")
            .value(hasItem(DEFAULT_CHAINE_ACTIVE.booleanValue()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllChainesWithEagerRelationshipsIsEnabled() {
        when(chaineServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(chaineServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllChainesWithEagerRelationshipsIsNotEnabled() {
        when(chaineServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(chaineServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getChaine() {
        // Initialize the database
        chaineRepository.save(chaine).block();

        // Get the chaine
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, chaine.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(chaine.getId().intValue()))
            .jsonPath("$.chaineNom")
            .value(is(DEFAULT_CHAINE_NOM))
            .jsonPath("$.chaineImage")
            .value(is(DEFAULT_CHAINE_IMAGE))
            .jsonPath("$.chaineActive")
            .value(is(DEFAULT_CHAINE_ACTIVE.booleanValue()));
    }

    @Test
    void getNonExistingChaine() {
        // Get the chaine
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewChaine() throws Exception {
        // Initialize the database
        chaineRepository.save(chaine).block();

        int databaseSizeBeforeUpdate = chaineRepository.findAll().collectList().block().size();

        // Update the chaine
        Chaine updatedChaine = chaineRepository.findById(chaine.getId()).block();
        updatedChaine.chaineNom(UPDATED_CHAINE_NOM).chaineImage(UPDATED_CHAINE_IMAGE).chaineActive(UPDATED_CHAINE_ACTIVE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedChaine.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedChaine))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Chaine in the database
        List<Chaine> chaineList = chaineRepository.findAll().collectList().block();
        assertThat(chaineList).hasSize(databaseSizeBeforeUpdate);
        Chaine testChaine = chaineList.get(chaineList.size() - 1);
        assertThat(testChaine.getChaineNom()).isEqualTo(UPDATED_CHAINE_NOM);
        assertThat(testChaine.getChaineImage()).isEqualTo(UPDATED_CHAINE_IMAGE);
        assertThat(testChaine.getChaineActive()).isEqualTo(UPDATED_CHAINE_ACTIVE);
    }

    @Test
    void putNonExistingChaine() throws Exception {
        int databaseSizeBeforeUpdate = chaineRepository.findAll().collectList().block().size();
        chaine.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, chaine.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(chaine))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Chaine in the database
        List<Chaine> chaineList = chaineRepository.findAll().collectList().block();
        assertThat(chaineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchChaine() throws Exception {
        int databaseSizeBeforeUpdate = chaineRepository.findAll().collectList().block().size();
        chaine.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(chaine))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Chaine in the database
        List<Chaine> chaineList = chaineRepository.findAll().collectList().block();
        assertThat(chaineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamChaine() throws Exception {
        int databaseSizeBeforeUpdate = chaineRepository.findAll().collectList().block().size();
        chaine.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(chaine))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Chaine in the database
        List<Chaine> chaineList = chaineRepository.findAll().collectList().block();
        assertThat(chaineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateChaineWithPatch() throws Exception {
        // Initialize the database
        chaineRepository.save(chaine).block();

        int databaseSizeBeforeUpdate = chaineRepository.findAll().collectList().block().size();

        // Update the chaine using partial update
        Chaine partialUpdatedChaine = new Chaine();
        partialUpdatedChaine.setId(chaine.getId());

        partialUpdatedChaine.chaineNom(UPDATED_CHAINE_NOM).chaineImage(UPDATED_CHAINE_IMAGE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedChaine.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedChaine))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Chaine in the database
        List<Chaine> chaineList = chaineRepository.findAll().collectList().block();
        assertThat(chaineList).hasSize(databaseSizeBeforeUpdate);
        Chaine testChaine = chaineList.get(chaineList.size() - 1);
        assertThat(testChaine.getChaineNom()).isEqualTo(UPDATED_CHAINE_NOM);
        assertThat(testChaine.getChaineImage()).isEqualTo(UPDATED_CHAINE_IMAGE);
        assertThat(testChaine.getChaineActive()).isEqualTo(DEFAULT_CHAINE_ACTIVE);
    }

    @Test
    void fullUpdateChaineWithPatch() throws Exception {
        // Initialize the database
        chaineRepository.save(chaine).block();

        int databaseSizeBeforeUpdate = chaineRepository.findAll().collectList().block().size();

        // Update the chaine using partial update
        Chaine partialUpdatedChaine = new Chaine();
        partialUpdatedChaine.setId(chaine.getId());

        partialUpdatedChaine.chaineNom(UPDATED_CHAINE_NOM).chaineImage(UPDATED_CHAINE_IMAGE).chaineActive(UPDATED_CHAINE_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedChaine.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedChaine))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Chaine in the database
        List<Chaine> chaineList = chaineRepository.findAll().collectList().block();
        assertThat(chaineList).hasSize(databaseSizeBeforeUpdate);
        Chaine testChaine = chaineList.get(chaineList.size() - 1);
        assertThat(testChaine.getChaineNom()).isEqualTo(UPDATED_CHAINE_NOM);
        assertThat(testChaine.getChaineImage()).isEqualTo(UPDATED_CHAINE_IMAGE);
        assertThat(testChaine.getChaineActive()).isEqualTo(UPDATED_CHAINE_ACTIVE);
    }

    @Test
    void patchNonExistingChaine() throws Exception {
        int databaseSizeBeforeUpdate = chaineRepository.findAll().collectList().block().size();
        chaine.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, chaine.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(chaine))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Chaine in the database
        List<Chaine> chaineList = chaineRepository.findAll().collectList().block();
        assertThat(chaineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchChaine() throws Exception {
        int databaseSizeBeforeUpdate = chaineRepository.findAll().collectList().block().size();
        chaine.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(chaine))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Chaine in the database
        List<Chaine> chaineList = chaineRepository.findAll().collectList().block();
        assertThat(chaineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamChaine() throws Exception {
        int databaseSizeBeforeUpdate = chaineRepository.findAll().collectList().block().size();
        chaine.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(chaine))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Chaine in the database
        List<Chaine> chaineList = chaineRepository.findAll().collectList().block();
        assertThat(chaineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteChaine() {
        // Initialize the database
        chaineRepository.save(chaine).block();

        int databaseSizeBeforeDelete = chaineRepository.findAll().collectList().block().size();

        // Delete the chaine
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, chaine.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Chaine> chaineList = chaineRepository.findAll().collectList().block();
        assertThat(chaineList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
