/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import jakarta.json.bind.annotation.JsonbTransient;
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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author HP
 */
@Entity
@Table(name = "theater")
@NamedQueries({
    @NamedQuery(name = "Theater.findAll", query = "SELECT t FROM Theater t"),
    @NamedQuery(name = "Theater.findByTheaterId", query = "SELECT t FROM Theater t WHERE t.theaterId = :theaterId"),
    @NamedQuery(name = "Theater.findByTheaterName", query = "SELECT t FROM Theater t WHERE t.theaterName = :theaterName"),
    @NamedQuery(name = "Theater.findByCity", query = "SELECT t FROM Theater t WHERE t.city = :city"),
    @NamedQuery(name = "Theater.findByAddress", query = "SELECT t FROM Theater t WHERE t.address = :address"),
    @NamedQuery(name = "Theater.findByCreatedAt", query = "SELECT t FROM Theater t WHERE t.createdAt = :createdAt"),
    @NamedQuery(name = "Theater.findByUpdatedAt", query = "SELECT t FROM Theater t WHERE t.updatedAt = :updatedAt"),
    @NamedQuery(name = "Theater.findByStatus", query = "SELECT t FROM Theater t WHERE t.status = :status")})
public class Theater implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "theater_id")
    private Long theaterId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "theater_name")
    private String theaterName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "city")
    private String city;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "address")
    private String address;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "theaterId")
    @JsonbTransient
    private Collection<Screen> screenCollection;

    public Theater() {
    }

    public Theater(Long theaterId) {
        this.theaterId = theaterId;
    }

    public Theater(Long theaterId, String theaterName, String city, String address, Date createdAt, Date updatedAt) {
        this.theaterId = theaterId;
        this.theaterName = theaterName;
        this.city = city;
        this.address = address;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getTheaterId() {
        return theaterId;
    }

    public void setTheaterId(Long theaterId) {
        this.theaterId = theaterId;
    }

    public String getTheaterName() {
        return theaterName;
    }

    public void setTheaterName(String theaterName) {
        this.theaterName = theaterName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Collection<Screen> getScreenCollection() {
        return screenCollection;
    }

    public void setScreenCollection(Collection<Screen> screenCollection) {
        this.screenCollection = screenCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (theaterId != null ? theaterId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Theater)) {
            return false;
        }
        Theater other = (Theater) object;
        if ((this.theaterId == null && other.theaterId != null) || (this.theaterId != null && !this.theaterId.equals(other.theaterId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Theater[ theaterId=" + theaterId + " ]";
    }

}
