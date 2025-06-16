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
@Table(name = "COMMISSIONS")
@NamedQueries({
    @NamedQuery(name = "Commissions.findAll", query = "SELECT c FROM Commissions c")})
public class Commissions implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private BigDecimal id;
    @Basic(optional = false)
    @jakarta.validation.constraints.NotNull
    @Column(name = "AMOUNT")
    private BigDecimal amount;
    @Column(name = "COMMISSION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date commissionDate;
    @JoinColumn(name = "PAYMENT_METHOD_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private PaymentMethods paymentMethods;
    @JoinColumn(name = "PURCHASE_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Purchases purchases;

    public Commissions() {
    }

    public Commissions(BigDecimal id) {
        this.id = id;
    }

    public Commissions(BigDecimal id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getCommissionDate() {
        return commissionDate;
    }

    public void setCommissionDate(Date commissionDate) {
        this.commissionDate = commissionDate;
    }

    public PaymentMethods getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(PaymentMethods paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public Purchases getPurchases() {
        return purchases;
    }

    public void setPurchases(Purchases purchases) {
        this.purchases = purchases;
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
        if (!(object instanceof Commissions)) {
            return false;
        }
        Commissions other = (Commissions) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PersistenceClasses.Commissions[ id=" + id + " ]";
    }
    
}
