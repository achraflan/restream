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
 * A Tag.
 */
@Table("tag")
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("tag_nom")
    private String tagNom;

    @JsonIgnoreProperties(value = { "chemins", "categorie", "tags" }, allowSetters = true)
    @Transient
    private Set<Chaine> chaines = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tag id(Long id) {
        this.id = id;
        return this;
    }

    public String getTagNom() {
        return this.tagNom;
    }

    public Tag tagNom(String tagNom) {
        this.tagNom = tagNom;
        return this;
    }

    public void setTagNom(String tagNom) {
        this.tagNom = tagNom;
    }

    public Set<Chaine> getChaines() {
        return this.chaines;
    }

    public Tag chaines(Set<Chaine> chaines) {
        this.setChaines(chaines);
        return this;
    }

    public Tag addChaines(Chaine chaine) {
        this.chaines.add(chaine);
        chaine.getTags().add(this);
        return this;
    }

    public Tag removeChaines(Chaine chaine) {
        this.chaines.remove(chaine);
        chaine.getTags().remove(this);
        return this;
    }

    public void setChaines(Set<Chaine> chaines) {
        if (this.chaines != null) {
            this.chaines.forEach(i -> i.removeTags(this));
        }
        if (chaines != null) {
            chaines.forEach(i -> i.addTags(this));
        }
        this.chaines = chaines;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tag)) {
            return false;
        }
        return id != null && id.equals(((Tag) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tag{" +
            "id=" + getId() +
            ", tagNom='" + getTagNom() + "'" +
            "}";
    }
}
