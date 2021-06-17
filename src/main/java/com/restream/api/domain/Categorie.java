package com.restream.api.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Categorie.
 */
@Table("categorie")
public class Categorie implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("categorie_nom")
    private String categorieNom;

    @Column("categorie_image")
    private String categorieImage;

    @Column("categorie_active")
    private Boolean categorieActive;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Categorie id(Long id) {
        this.id = id;
        return this;
    }

    public String getCategorieNom() {
        return this.categorieNom;
    }

    public Categorie categorieNom(String categorieNom) {
        this.categorieNom = categorieNom;
        return this;
    }

    public void setCategorieNom(String categorieNom) {
        this.categorieNom = categorieNom;
    }

    public String getCategorieImage() {
        return this.categorieImage;
    }

    public Categorie categorieImage(String categorieImage) {
        this.categorieImage = categorieImage;
        return this;
    }

    public void setCategorieImage(String categorieImage) {
        this.categorieImage = categorieImage;
    }

    public Boolean getCategorieActive() {
        return this.categorieActive;
    }

    public Categorie categorieActive(Boolean categorieActive) {
        this.categorieActive = categorieActive;
        return this;
    }

    public void setCategorieActive(Boolean categorieActive) {
        this.categorieActive = categorieActive;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Categorie)) {
            return false;
        }
        return id != null && id.equals(((Categorie) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Categorie{" +
            "id=" + getId() +
            ", categorieNom='" + getCategorieNom() + "'" +
            ", categorieImage='" + getCategorieImage() + "'" +
            ", categorieActive='" + getCategorieActive() + "'" +
            "}";
    }
}
