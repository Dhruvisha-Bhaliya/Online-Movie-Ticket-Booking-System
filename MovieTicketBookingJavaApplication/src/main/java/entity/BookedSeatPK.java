/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author HP
 */
@Embeddable
public class BookedSeatPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "bookingid")
    private long bookingid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "bookingseat_id")
    private long bookingseatId;

    public BookedSeatPK() {
    }

    public BookedSeatPK(long bookingid, long bookingseatId) {
        this.bookingid = bookingid;
        this.bookingseatId = bookingseatId;
    }

    public long getBookingid() {
        return bookingid;
    }

    public void setBookingid(long bookingid) {
        this.bookingid = bookingid;
    }

    public long getBookingseatId() {
        return bookingseatId;
    }

    public void setBookingseatId(long bookingseatId) {
        this.bookingseatId = bookingseatId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) bookingid;
        hash += (int) bookingseatId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BookedSeatPK)) {
            return false;
        }
        BookedSeatPK other = (BookedSeatPK) object;
        if (this.bookingid != other.bookingid) {
            return false;
        }
        if (this.bookingseatId != other.bookingseatId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.BookedSeatPK[ bookingid=" + bookingid + ", bookingseatId=" + bookingseatId + " ]";
    }
    
}
