/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PersistenceClasses;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author jp570
 */
@Entity
@Table(name = "SPECIES")
@NamedQueries({
    @NamedQuery(name = "Species.findAll", query = "SELECT s FROM Species s")})
public class Species implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private BigDecimal id;
    @jakarta.validation.constraints.Size(max = 100)
    @Column(name = "SCIENTIFIC_NAME")
    private String scientificName;
    @jakarta.validation.constraints.Size(max = 50)
    @Column(name = "COMMON_NAME")
    private String commonName;
    @Column(name = "EXTINCTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date extinctionDate;
    @jakarta.validation.constraints.Size(max = 20)
    @Column(name = "PERIOD")
    private String period;
    @Column(name = "WEIGHT")
    private BigDecimal weight;
    @jakarta.validation.constraints.Size(max = 500)
    @Column(name = "FEATURES")
    private String features;
    @JoinColumn(name = "COLLECTION_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Collections collections;

    public Species() {
    }

    public Species(BigDecimal id) {
        this.id = id;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public Date getExtinctionDate() {
        return extinctionDate;
    }

    public void setExtinctionDate(Date extinctionDate) {
        this.extinctionDate = extinctionDate;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public Collections getCollections() {
        return collections;
    }

    public void setCollections(Collections collections) {
        this.collections = collections;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Species)) {
            return false;
        }
        Species other = (Species) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PersistenceClasses.Species[ id=" + id + " ]";
    }
    
}
