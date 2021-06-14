package com.restream.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Chaine.
 */
@Table("chaine")
public class Chaine implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("chaine_nom")
    private String chaineNom;

    @Column("chaine_image")
    private String chaineImage;

    @Column("chaine_active")
    private Boolean chaineActive;

    @Transient
    @JsonIgnoreProperties(value = { "chaine" }, allowSetters = true)
    private Set<Chemin> chemins = new HashSet<>();

    @Transient
    private Categorie categorie;

    @Column("categorie_id")
    private Long categorieId;

    @JsonIgnoreProperties(value = { "chaines" }, allowSetters = true)
    @Transient
    private Set<Tag> tags = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Chaine id(Long id) {
        this.id = id;
        return this;
    }

    public String getChaineNom() {
        return this.chaineNom;
    }

    public Chaine chaineNom(String chaineNom) {
        this.chaineNom = chaineNom;
        return this;
    }

    public void setChaineNom(String chaineNom) {
        this.chaineNom = chaineNom;
    }

    public String getChaineImage() {
        return this.chaineImage;
    }

    public Chaine chaineImage(String chaineImage) {
        this.chaineImage = chaineImage;
        return this;
    }

    public void setChaineImage(String chaineImage) {
        this.chaineImage = chaineImage;
    }

    public Boolean getChaineActive() {
        return this.chaineActive;
    }

    public Chaine chaineActive(Boolean chaineActive) {
        this.chaineActive = chaineActive;
        return this;
    }

    public void setChaineActive(Boolean chaineActive) {
        this.chaineActive = chaineActive;
    }

    public Set<Chemin> getChemins() {
        return this.chemins;
    }

    public Chaine chemins(Set<Chemin> chemins) {
        this.setChemins(chemins);
        return this;
    }

    public Chaine addChemin(Chemin chemin) {
        this.chemins.add(chemin);
        chemin.setChaine(this);
        return this;
    }

    public Chaine removeChemin(Chemin chemin) {
        this.chemins.remove(chemin);
        chemin.setChaine(null);
        return this;
    }

    public void setChemins(Set<Chemin> chemins) {
        if (this.chemins != null) {
            this.chemins.forEach(i -> i.setChaine(null));
        }
        if (chemins != null) {
            chemins.forEach(i -> i.setChaine(this));
        }
        this.chemins = chemins;
    }

    public Categorie getCategorie() {
        return this.categorie;
    }

    public Chaine categorie(Categorie categorie) {
        this.setCategorie(categorie);
        this.categorieId = categorie != null ? categorie.getId() : null;
        return this;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
        this.categorieId = categorie != null ? categorie.getId() : null;
    }

    public Long getCategorieId() {
        return this.categorieId;
    }

    public void setCategorieId(Long categorie) {
        this.categorieId = categorie;
    }

    public Set<Tag> getTags() {
        return this.tags;
    }

    public Chaine tags(Set<Tag> tags) {
        this.setTags(tags);
        return this;
    }

    public Chaine addTags(Tag tag) {
        this.tags.add(tag);
        tag.getChaines().add(this);
        return this;
    }

    public Chaine removeTags(Tag tag) {
        this.tags.remove(tag);
        tag.getChaines().remove(this);
        return this;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Chaine)) {
            return false;
        }
        return id != null && id.equals(((Chaine) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Chaine{" +
            "id=" + getId() +
            ", chaineNom='" + getChaineNom() + "'" +
            ", chaineImage='" + getChaineImage() + "'" +
            ", chaineActive='" + getChaineActive() + "'" +
            "}";
    }
}
