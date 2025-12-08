/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import entity.BookedSeatPK;
import entity.Booking;
import entity.Seat;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 *
 * @author HP
 */
@Entity
@Table(name = "booked_seat")
@NamedQueries({
    @NamedQuery(name = "BookedSeat.findAll", query = "SELECT b FROM BookedSeat b"),
    @NamedQuery(name = "BookedSeat.findByBookingid", query = "SELECT b FROM BookedSeat b WHERE b.bookedSeatPK.bookingid = :bookingid"),
    @NamedQuery(name = "BookedSeat.findByBookingseatId", query = "SELECT b FROM BookedSeat b WHERE b.bookedSeatPK.bookingseatId = :bookingseatId")})
public class BookedSeat implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected BookedSeatPK bookedSeatPK;
    @JoinColumn(name = "bookingid", referencedColumnName = "booking_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Booking booking;
    @JoinColumn(name = "bookingseat_id", referencedColumnName = "seat_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Seat seat;

    public BookedSeat() {
    }

    public BookedSeat(BookedSeatPK bookedSeatPK) {
        this.bookedSeatPK = bookedSeatPK;
    }

    public BookedSeat(long bookingid, long bookingseatId) {
        this.bookedSeatPK = new BookedSeatPK(bookingid, bookingseatId);
    }

    public BookedSeatPK getBookedSeatPK() {
        return bookedSeatPK;
    }

    public void setBookedSeatPK(BookedSeatPK bookedSeatPK) {
        this.bookedSeatPK = bookedSeatPK;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bookedSeatPK != null ? bookedSeatPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BookedSeat)) {
            return false;
        }
        BookedSeat other = (BookedSeat) object;
        if ((this.bookedSeatPK == null && other.bookedSeatPK != null) || (this.bookedSeatPK != null && !this.bookedSeatPK.equals(other.bookedSeatPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.BookedSeat[ bookedSeatPK=" + bookedSeatPK + " ]";
    }
    
}
