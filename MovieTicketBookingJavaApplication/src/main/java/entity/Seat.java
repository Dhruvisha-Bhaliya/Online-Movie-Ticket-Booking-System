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
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
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
@Table(name = "seat")
@NamedQueries({
    @NamedQuery(name = "Seat.findAll", query = "SELECT s FROM Seat s"),
    @NamedQuery(name = "Seat.findBySeatId", query = "SELECT s FROM Seat s WHERE s.seatId = :seatId"),
    @NamedQuery(name = "Seat.findBySeatRow", query = "SELECT s FROM Seat s WHERE s.seatRow = :seatRow"),
    @NamedQuery(name = "Seat.findBySeatNumber", query = "SELECT s FROM Seat s WHERE s.seatNumber = :seatNumber"),
    @NamedQuery(name = "Seat.findBySeatStatus", query = "SELECT s FROM Seat s WHERE s.seatStatus = :seatStatus"),
    @NamedQuery(name = "Seat.findByCreatedAt", query = "SELECT s FROM Seat s WHERE s.createdAt = :createdAt"),
    @NamedQuery(name = "Seat.findByUpdatedAt", query = "SELECT s FROM Seat s WHERE s.updatedAt = :updatedAt"),
    @NamedQuery(name = "Seat.findByStatus", query = "SELECT s FROM Seat s WHERE s.status = :status")})
public class Seat implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "seat_id")
    private Long seatId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "seat_row")
    private Character seatRow;
    @Basic(optional = false)
    @NotNull
    @Column(name = "seat_number")
    private int seatNumber;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "seat_status")
    private String seatStatus;
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
    @ManyToMany(mappedBy = "seatCollection")
    private Collection<Booking> bookingCollection;
    @JoinColumn(name = "screen_id", referencedColumnName = "screen_id")
    @ManyToOne(optional = false)
    private Screen screenId;
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    @ManyToOne
    private SeatCategory categoryId;

    public Seat() {
    }

    public Seat(Long seatId) {
        this.seatId = seatId;
    }

    public Seat(Long seatId, Character seatRow, int seatNumber, String seatStatus, Date createdAt, Date updatedAt) {
        this.seatId = seatId;
        this.seatRow = seatRow;
        this.seatNumber = seatNumber;
        this.seatStatus = seatStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public Character getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(Character seatRow) {
        this.seatRow = seatRow;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatStatus() {
        return seatStatus;
    }

    public void setSeatStatus(String seatStatus) {
        this.seatStatus = seatStatus;
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

    public Screen getScreenId() {
        return screenId;
    }

    public void setScreenId(Screen screenId) {
        this.screenId = screenId;
    }

    public SeatCategory getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(SeatCategory categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (seatId != null ? seatId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Seat)) {
            return false;
        }
        Seat other = (Seat) object;
        if ((this.seatId == null && other.seatId != null) || (this.seatId != null && !this.seatId.equals(other.seatId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Seat[ seatId=" + seatId + " ]";
    }
    
}
