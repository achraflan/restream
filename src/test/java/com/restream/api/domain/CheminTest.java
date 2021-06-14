package com.restream.api.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.restream.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CheminTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Chemin.class);
        Chemin chemin1 = new Chemin();
        chemin1.setId(1L);
        Chemin chemin2 = new Chemin();
        chemin2.setId(chemin1.getId());
        assertThat(chemin1).isEqualTo(chemin2);
        chemin2.setId(2L);
        assertThat(chemin1).isNotEqualTo(chemin2);
        chemin1.setId(null);
        assertThat(chemin1).isNotEqualTo(chemin2);
    }
}
