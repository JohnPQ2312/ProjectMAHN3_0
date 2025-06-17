package PersistenceClasses;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

@Entity
@Table(name = "USERS")
@NamedQueries({
    @NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u")})
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private BigDecimal id;

    @Basic(optional = false)
    @jakarta.validation.constraints.NotNull
    @jakarta.validation.constraints.Size(min = 1, max = 100)
    @Column(name = "NAME")
    private String name;

    @Basic(optional = false)
    @jakarta.validation.constraints.NotNull
    @jakarta.validation.constraints.Size(min = 1, max = 100)
    @Column(name = "EMAIL")
    private String email;

    @Basic(optional = false)
    @jakarta.validation.constraints.NotNull
    @jakarta.validation.constraints.Size(min = 1, max = 100)
    @Column(name = "PASSWORD")
    private String password;

    @jakarta.validation.constraints.Size(max = 20)
    @Column(name = "ROLE")
    private String role;

    @jakarta.validation.constraints.Size(max = 20)
    @Column(name = "PHONE")
    private String phone;

    @Column(name = "REGISTRATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date registrationDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "users")
    private Collection<Reviews> reviewsCollection;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "users")
    private Collection<Purchases> purchasesCollection;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fromUser")
    private Collection<Transfers> transfersFromCollection;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "toUser")
    private Collection<Transfers> transfersToCollection;

    public Users() {
    }

    public Users(BigDecimal id) {
        this.id = id;
    }

    public Users(BigDecimal id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Collection<Reviews> getReviewsCollection() {
        return reviewsCollection;
    }

    public void setReviewsCollection(Collection<Reviews> reviewsCollection) {
        this.reviewsCollection = reviewsCollection;
    }

    public Collection<Purchases> getPurchasesCollection() {
        return purchasesCollection;
    }

    public void setPurchasesCollection(Collection<Purchases> purchasesCollection) {
        this.purchasesCollection = purchasesCollection;
    }

    public Collection<Transfers> getTransfersFromCollection() {
        return transfersFromCollection;
    }

    public void setTransfersFromCollection(Collection<Transfers> transfersFromCollection) {
        this.transfersFromCollection = transfersFromCollection;
    }

    public Collection<Transfers> getTransfersToCollection() {
        return transfersToCollection;
    }

    public void setTransfersToCollection(Collection<Transfers> transfersToCollection) {
        this.transfersToCollection = transfersToCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Users)) {
            return false;
        }
        Users other = (Users) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PersistenceClasses.Users[ id=" + id + " ]";
    }
}