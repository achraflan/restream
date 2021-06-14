package com.restream.api.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.restream.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChaineTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Chaine.class);
        Chaine chaine1 = new Chaine();
        chaine1.setId(1L);
        Chaine chaine2 = new Chaine();
        chaine2.setId(chaine1.getId());
        assertThat(chaine1).isEqualTo(chaine2);
        chaine2.setId(2L);
        assertThat(chaine1).isNotEqualTo(chaine2);
        chaine1.setId(null);
        assertThat(chaine1).isNotEqualTo(chaine2);
    }
}
