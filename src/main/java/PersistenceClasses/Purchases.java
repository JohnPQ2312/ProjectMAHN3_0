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
@Table(name = "PURCHASES")
@NamedQueries({
    @NamedQuery(name = "Purchases.findAll", query = "SELECT p FROM Purchases p")})
public class Purchases implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private BigDecimal id;
    @Basic(optional = false)
    @jakarta.validation.constraints.NotNull
    @Column(name = "TOTAL_AMOUNT")
    private BigDecimal totalAmount;
    @Basic(optional = false)
    @jakarta.validation.constraints.NotNull
    @Column(name = "TOTAL_COMMISSION")
    private BigDecimal totalCommission;
    @Column(name = "PURCHASE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date purchaseDate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "purchases")
    private Collection<Commissions> commissionsCollection;
    @JoinColumn(name = "PAYMENT_METHOD_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private PaymentMethods paymentMethods;
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Users users;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "purchases")
    private Collection<Entries> entriesCollection;

    public Purchases() {
    }

    public Purchases(BigDecimal id) {
        this.id = id;
    }

    public Purchases(BigDecimal id, BigDecimal totalAmount, BigDecimal totalCommission) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.totalCommission = totalCommission;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(BigDecimal totalCommission) {
        this.totalCommission = totalCommission;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Collection<Commissions> getCommissionsCollection() {
        return commissionsCollection;
    }

    public void setCommissionsCollection(Collection<Commissions> commissionsCollection) {
        this.commissionsCollection = commissionsCollection;
    }

    public PaymentMethods getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(PaymentMethods paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
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
        if (!(object instanceof Purchases)) {
            return false;
        }
        Purchases other = (Purchases) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PersistenceClasses.Purchases[ id=" + id + " ]";
    }
    
}
