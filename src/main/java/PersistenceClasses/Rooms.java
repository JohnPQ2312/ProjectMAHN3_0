/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PersistenceClasses;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;

/**
 *
 * @author jp570
 */
@Entity
@Table(name = "ROOMS")
@NamedQueries({
    @NamedQuery(name = "Rooms.findAll", query = "SELECT r FROM Rooms r")})
public class Rooms implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private BigDecimal id;
    @Basic(optional = false)
    @jakarta.validation.constraints.NotNull
    @jakarta.validation.constraints.Size(min = 1, max = 100)
    @Column(name = "NAME")
    private String name;
    @jakarta.validation.constraints.Size(max = 200)
    @Column(name = "DESCRIPTION")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rooms")
    private Collection<Reviews> reviewsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rooms")
    private Collection<Prices> pricesCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rooms")
    private Collection<Themes> themesCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rooms")
    private Collection<Collections> collectionsCollection;
    @JoinColumn(name = "MUSEUM_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Museums museums;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rooms")
    private Collection<RoomImages> roomImagesCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rooms")
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

    public Collection<Reviews> getReviewsCollection() {
        return reviewsCollection;
    }

    public void setReviewsCollection(Collection<Reviews> reviewsCollection) {
        this.reviewsCollection = reviewsCollection;
    }

    public Collection<Prices> getPricesCollection() {
        return pricesCollection;
    }

    public void setPricesCollection(Collection<Prices> pricesCollection) {
        this.pricesCollection = pricesCollection;
    }

    public Collection<Themes> getThemesCollection() {
        return themesCollection;
    }

    public void setThemesCollection(Collection<Themes> themesCollection) {
        this.themesCollection = themesCollection;
    }

    public Collection<Collections> getCollectionsCollection() {
        return collectionsCollection;
    }

    public void setCollectionsCollection(Collection<Collections> collectionsCollection) {
        this.collectionsCollection = collectionsCollection;
    }

    public Museums getMuseums() {
        return museums;
    }

    public void setMuseums(Museums museums) {
        this.museums = museums;
    }

    public Collection<RoomImages> getRoomImagesCollection() {
        return roomImagesCollection;
    }

    public void setRoomImagesCollection(Collection<RoomImages> roomImagesCollection) {
        this.roomImagesCollection = roomImagesCollection;
    }

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
        return "PersistenceClasses.Rooms[ id=" + id + " ]";
    }
    
}
