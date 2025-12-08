/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 *
 * @author HP
 */
@Entity
@Table(name = "admin")
@NamedQueries({
    @NamedQuery(name = "Admin.findAll", query = "SELECT a FROM Admin a"),
    @NamedQuery(name = "Admin.findByAdminId", query = "SELECT a FROM Admin a WHERE a.adminId = :adminId"),
    @NamedQuery(name = "Admin.findByUsername", query = "SELECT a FROM Admin a WHERE a.username = :username"),
    @NamedQuery(name = "Admin.findByPassword", query = "SELECT a FROM Admin a WHERE a.password = :password"),
    @NamedQuery(name = "Admin.findByEmail", query = "SELECT a FROM Admin a WHERE a.email = :email"),
    @NamedQuery(name = "Admin.findByCreatedAt", query = "SELECT a FROM Admin a WHERE a.createdAt = :createdAt"),
    @NamedQuery(name = "Admin.findByUpdatedAt", query = "SELECT a FROM Admin a WHERE a.updatedAt = :updatedAt"),
    @NamedQuery(name = "Admin.findByStatus", query = "SELECT a FROM Admin a WHERE a.status = :status"),
    @NamedQuery(name = "Admin.findByPhoneno", query = "SELECT a FROM Admin a WHERE a.phoneno = :phoneno")})
public class Admin implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "admin_id")
    private Long adminId;
    @Size(max = 30)
    @Column(name = "username")
    private String username;
    @Size(max = 30)
    @Column(name = "password")
    private String password;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 50)
    @Column(name = "email")
    private String email;
    @Basic(optional = false)
    @NotNull
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Basic(optional = false)
    @NotNull
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Size(max = 7)
    @Column(name = "status")
    private String status;
    @Column(name = "phoneno")
    private BigInteger phoneno;
    @JoinColumn(name = "role", referencedColumnName = "role_id")
    @ManyToOne
    private RoleMaster role;

    public Admin() {
    }

    public Admin(Long adminId) {
        this.adminId = adminId;
    }

    public Admin(Long adminId, Date createdAt, Date updatedAt) {
        this.adminId = adminId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigInteger getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(BigInteger phoneno) {
        this.phoneno = phoneno;
    }

    public RoleMaster getRole() {
        return role;
    }

    public void setRole(RoleMaster role) {
        this.role = role;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (adminId != null ? adminId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Admin)) {
            return false;
        }
        Admin other = (Admin) object;
        if ((this.adminId == null && other.adminId != null) || (this.adminId != null && !this.adminId.equals(other.adminId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Admin[ adminId=" + adminId + " ]";
    }
    
}
