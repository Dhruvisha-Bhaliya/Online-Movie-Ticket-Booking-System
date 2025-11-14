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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
 * @author DELL
 */
@Entity
@Table(name = "screen")
@NamedQueries({
    @NamedQuery(name = "Screen.findAll", query = "SELECT s FROM Screen s"),
    @NamedQuery(name = "Screen.findByScreenId", query = "SELECT s FROM Screen s WHERE s.screenId = :screenId"),
    @NamedQuery(name = "Screen.findByScreenNumber", query = "SELECT s FROM Screen s WHERE s.screenNumber = :screenNumber"),
    @NamedQuery(name = "Screen.findByCapacity", query = "SELECT s FROM Screen s WHERE s.capacity = :capacity"),
    @NamedQuery(name = "Screen.findByScreenName", query = "SELECT s FROM Screen s WHERE s.screenName = :screenName"),
    @NamedQuery(name = "Screen.findByCreatedAt", query = "SELECT s FROM Screen s WHERE s.createdAt = :createdAt"),
    @NamedQuery(name = "Screen.findByUpdatedAt", query = "SELECT s FROM Screen s WHERE s.updatedAt = :updatedAt"),
    @NamedQuery(name = "Screen.findByStatus", query = "SELECT s FROM Screen s WHERE s.status = :status")})
public class Screen implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "screen_id")
    private Long screenId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "screen_number")
    private String screenNumber;
    @Basic(optional = false)
    @NotNull
    @Column(name = "capacity")
    private int capacity;
    @Size(max = 255)
    @Column(name = "screen_name")
    private String screenName;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "screenId")
    @JsonbTransient
    private Collection<Seat> seatCollection;
    @OneToMany(mappedBy = "screenId")
    @JsonbTransient
    private Collection<Showmovie> showmovieCollection;
    @JoinColumn(name = "theater_id", referencedColumnName = "theater_id")
    @ManyToOne(optional = false)
    private Theater theaterId;

    public Screen() {
    }

    public Screen(Long screenId) {
        this.screenId = screenId;
    }

    public Screen(Long screenId, String screenNumber, int capacity, Date createdAt, Date updatedAt) {
        this.screenId = screenId;
        this.screenNumber = screenNumber;
        this.capacity = capacity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getScreenId() {
        return screenId;
    }

    public void setScreenId(Long screenId) {
        this.screenId = screenId;
    }

    public String getScreenNumber() {
        return screenNumber;
    }

    public void setScreenNumber(String screenNumber) {
        this.screenNumber = screenNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
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

    public Collection<Seat> getSeatCollection() {
        return seatCollection;
    }

    public void setSeatCollection(Collection<Seat> seatCollection) {
        this.seatCollection = seatCollection;
    }

    public Collection<Showmovie> getShowmovieCollection() {
        return showmovieCollection;
    }

    public void setShowmovieCollection(Collection<Showmovie> showmovieCollection) {
        this.showmovieCollection = showmovieCollection;
    }

    public Theater getTheaterId() {
        return theaterId;
    }

    public void setTheaterId(Theater theaterId) {
        this.theaterId = theaterId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (screenId != null ? screenId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Screen)) {
            return false;
        }
        Screen other = (Screen) object;
        if ((this.screenId == null && other.screenId != null) || (this.screenId != null && !this.screenId.equals(other.screenId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Screen[ screenId=" + screenId + " ]";
    }

}
