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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author jp570
 */
@Entity
@Table(name = "ENTRIES")
@NamedQueries({
    @NamedQuery(name = "Entries.findAll", query = "SELECT e FROM Entries e")})
public class Entries implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private BigDecimal id;
    @Basic(optional = false)
    @jakarta.validation.constraints.NotNull
    @Column(name = "VISIT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date visitDate;
    @Basic(optional = false)
    @jakarta.validation.constraints.NotNull
    @Column(name = "PRICE")
    private BigDecimal price;
    @Basic(optional = false)
    @jakarta.validation.constraints.NotNull
    @jakarta.validation.constraints.Size(min = 1, max = 255)
    @Column(name = "QR_CODE")
    private String qrCode;
    @jakarta.validation.constraints.Size(max = 20)
    @Column(name = "STATUS")
    private String status;
    @JoinColumn(name = "PURCHASE_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Purchases purchases;
    @JoinColumn(name = "ROOM_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Rooms rooms;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entries")
    private Collection<Transfers> transfersCollection;

    public Entries() {
    }

    public Entries(BigDecimal id) {
        this.id = id;
    }

    public Entries(BigDecimal id, Date visitDate, BigDecimal price, String qrCode) {
        this.id = id;
        this.visitDate = visitDate;
        this.price = price;
        this.qrCode = qrCode;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Purchases getPurchases() {
        return purchases;
    }

    public void setPurchases(Purchases purchases) {
        this.purchases = purchases;
    }

    public Rooms getRooms() {
        return rooms;
    }

    public void setRooms(Rooms rooms) {
        this.rooms = rooms;
    }

    public Collection<Transfers> getTransfersCollection() {
        return transfersCollection;
    }

    public void setTransfersCollection(Collection<Transfers> transfersCollection) {
        this.transfersCollection = transfersCollection;
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
        if (!(object instanceof Entries)) {
            return false;
        }
        Entries other = (Entries) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PersistenceClasses.Entries[ id=" + id + " ]";
    }
    
}
