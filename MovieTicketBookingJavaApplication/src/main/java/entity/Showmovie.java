/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

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
 * @author HP
 */
@Entity
@Table(name = "showmovie")
@NamedQueries({
    @NamedQuery(name = "Showmovie.findAll", query = "SELECT s FROM Showmovie s"),
    @NamedQuery(name = "Showmovie.findByShowId", query = "SELECT s FROM Showmovie s WHERE s.showId = :showId"),
    @NamedQuery(name = "Showmovie.findByShowTime", query = "SELECT s FROM Showmovie s WHERE s.showTime = :showTime"),
    @NamedQuery(name = "Showmovie.findByCreatedAt", query = "SELECT s FROM Showmovie s WHERE s.createdAt = :createdAt"),
    @NamedQuery(name = "Showmovie.findByUpdatedAt", query = "SELECT s FROM Showmovie s WHERE s.updatedAt = :updatedAt"),
    @NamedQuery(name = "Showmovie.findByStatus", query = "SELECT s FROM Showmovie s WHERE s.status = :status")})
public class Showmovie implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "show_id")
    private Long showId;
    @Column(name = "show_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date showTime;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "showId")
    private Collection<Booking> bookingCollection;
    @JoinColumn(name = "movie_id", referencedColumnName = "movie_id")
    @ManyToOne
    private Movie movieId;
    @JoinColumn(name = "screen_id", referencedColumnName = "screen_id")
    @ManyToOne
    private Screen screenId;

    public Showmovie() {
    }

    public Showmovie(Long showId) {
        this.showId = showId;
    }

    public Showmovie(Long showId, Date createdAt, Date updatedAt) {
        this.showId = showId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getShowId() {
        return showId;
    }

    public void setShowId(Long showId) {
        this.showId = showId;
    }

    public Date getShowTime() {
        return showTime;
    }

    public void setShowTime(Date showTime) {
        this.showTime = showTime;
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

    public Collection<Booking> getBookingCollection() {
        return bookingCollection;
    }

    public void setBookingCollection(Collection<Booking> bookingCollection) {
        this.bookingCollection = bookingCollection;
    }

    public Movie getMovieId() {
        return movieId;
    }

    public void setMovieId(Movie movieId) {
        this.movieId = movieId;
    }

    public Screen getScreenId() {
        return screenId;
    }

    public void setScreenId(Screen screenId) {
        this.screenId = screenId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (showId != null ? showId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Showmovie)) {
            return false;
        }
        Showmovie other = (Showmovie) object;
        if ((this.showId == null && other.showId != null) || (this.showId != null && !this.showId.equals(other.showId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Showmovie[ showId=" + showId + " ]";
    }
    
}
