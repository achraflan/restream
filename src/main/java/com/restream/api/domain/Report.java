package com.restream.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Report.
 */
@Table("report")
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("message")
    private String message;

    @Column("date_creation")
    private LocalDate dateCreation;

    @JsonIgnoreProperties(value = { "chaine" }, allowSetters = true)
    @Transient
    private Chemin chemin;

    @Column("chemin_id")
    private Long cheminId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Report id(Long id) {
        this.id = id;
        return this;
    }

    public String getMessage() {
        return this.message;
    }

    public Report message(String message) {
        this.message = message;
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getDateCreation() {
        return this.dateCreation;
    }

    public Report dateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
        return this;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Chemin getChemin() {
        return this.chemin;
    }

    public Report chemin(Chemin chemin) {
        this.setChemin(chemin);
        this.cheminId = chemin != null ? chemin.getId() : null;
        return this;
    }

    public void setChemin(Chemin chemin) {
        this.chemin = chemin;
        this.cheminId = chemin != null ? chemin.getId() : null;
    }

    public Long getCheminId() {
        return this.cheminId;
    }

    public void setCheminId(Long chemin) {
        this.cheminId = chemin;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Report)) {
            return false;
        }
        return id != null && id.equals(((Report) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Report{" +
            "id=" + getId() +
            ", message='" + getMessage() + "'" +
            ", dateCreation='" + getDateCreation() + "'" +
            "}";
    }
}
