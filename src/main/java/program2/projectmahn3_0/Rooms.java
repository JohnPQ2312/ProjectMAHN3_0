/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package program2.projectmahn3_0;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;

/**
 *
 * @author jp570
 */
@Entity
@Table(name = "ROOMS")

public class Rooms implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigDecimal id;
    private String name;
    private String description;
    private Museums museumId;
    private Collection<Prices> pricesCollection;
    private Collection<Collections> collectionsCollection;
    private Collection<Ratings> ratingsCollection;
    private Collection<Thematics> thematicsCollection;
    private Collection<Entries> entriesCollection;

    public Rooms() {
    }

    public Rooms(BigDecimal id) {
        this.id = id;
    }

    public Rooms(BigDecimal id, String name) {
        this.id = id;
        this.name = name;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Museums getMuseumId() {
        return museumId;
    }

    public void setMuseumId(Museums museumId) {
        this.museumId = museumId;
    }

    @XmlTransient
    public Collection<Prices> getPricesCollection() {
        return pricesCollection;
    }

    public void setPricesCollection(Collection<Prices> pricesCollection) {
        this.pricesCollection = pricesCollection;
    }

    @XmlTransient
    public Collection<Collections> getCollectionsCollection() {
        return collectionsCollection;
    }

    public void setCollectionsCollection(Collection<Collections> collectionsCollection) {
        this.collectionsCollection = collectionsCollection;
    }

    @XmlTransient
    public Collection<Ratings> getRatingsCollection() {
        return ratingsCollection;
    }

    public void setRatingsCollection(Collection<Ratings> ratingsCollection) {
        this.ratingsCollection = ratingsCollection;
    }

    @XmlTransient
    public Collection<Thematics> getThematicsCollection() {
        return thematicsCollection;
    }

    public void setThematicsCollection(Collection<Thematics> thematicsCollection) {
        this.thematicsCollection = thematicsCollection;
    }

    @XmlTransient
    public Collection<Entries> getEntriesCollection() {
        return entriesCollection;
    }

    public void setEntriesCollection(Collection<Entries> entriesCollection) {
        this.entriesCollection = entriesCollection;
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
        if (!(object instanceof Rooms)) {
            return false;
        }
        Rooms other = (Rooms) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "program2.projectmahn3_0.Rooms[ id=" + id + " ]";
    }
    
}
