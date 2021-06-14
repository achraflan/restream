package com.restream.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restream.api.domain.enumeration.TypeChemin;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Chemin.
 */
@Table("chemin")
public class Chemin implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("chemin_non")
    private String cheminNon;

    @Column("type")
    private TypeChemin type;

    @Column("chemin_valide")
    private Boolean cheminValide;

    @Column("chemin_marche")
    private Boolean cheminMarche;

    /**
     * Another side of the same relationship
     */
    @ApiModelProperty(value = "Another side of the same relationship")
    @JsonIgnoreProperties(value = { "chemins", "categorie", "tags" }, allowSetters = true)
    @Transient
    private Chaine chaine;

    @Column("chaine_id")
    private Long chaineId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Chemin id(Long id) {
        this.id = id;
        return this;
    }

    public String getCheminNon() {
        return this.cheminNon;
    }

    public Chemin cheminNon(String cheminNon) {
        this.cheminNon = cheminNon;
        return this;
    }

    public void setCheminNon(String cheminNon) {
        this.cheminNon = cheminNon;
    }

    public TypeChemin getType() {
        return this.type;
    }

    public Chemin type(TypeChemin type) {
        this.type = type;
        return this;
    }

    public void setType(TypeChemin type) {
        this.type = type;
    }

    public Boolean getCheminValide() {
        return this.cheminValide;
    }

    public Chemin cheminValide(Boolean cheminValide) {
        this.cheminValide = cheminValide;
        return this;
    }

    public void setCheminValide(Boolean cheminValide) {
        this.cheminValide = cheminValide;
    }

    public Boolean getCheminMarche() {
        return this.cheminMarche;
    }

    public Chemin cheminMarche(Boolean cheminMarche) {
        this.cheminMarche = cheminMarche;
        return this;
    }

    public void setCheminMarche(Boolean cheminMarche) {
        this.cheminMarche = cheminMarche;
    }

    public Chaine getChaine() {
        return this.chaine;
    }

    public Chemin chaine(Chaine chaine) {
        this.setChaine(chaine);
        this.chaineId = chaine != null ? chaine.getId() : null;
        return this;
    }

    public void setChaine(Chaine chaine) {
        this.chaine = chaine;
        this.chaineId = chaine != null ? chaine.getId() : null;
    }

    public Long getChaineId() {
        return this.chaineId;
    }

    public void setChaineId(Long chaine) {
        this.chaineId = chaine;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Chemin)) {
            return false;
        }
        return id != null && id.equals(((Chemin) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Chemin{" +
            "id=" + getId() +
            ", cheminNon='" + getCheminNon() + "'" +
            ", type='" + getType() + "'" +
            ", cheminValide='" + getCheminValide() + "'" +
            ", cheminMarche='" + getCheminMarche() + "'" +
            "}";
    }
}
