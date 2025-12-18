/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package user_bean;

import entity.BookedSeat;
import entity.BookedSeatPK;
import entity.Booking;
import entity.Seat;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

/**
 *
 * @author DELL
 */
@Stateless
public class BookingHelperBean implements BookingHelperBeanLocal {

    @PersistenceContext(unitName = "myMovie")
    private EntityManager em;

    @EJB
    private SeatBeanLocal seatBean;

    @Override
    public List<Long> findBookedSeatIdsByShow(Long showId) {
        return em.createQuery(
                "SELECT bs.seat.seatId FROM BookedSeat bs "
                + "WHERE bs.booking.showId.showId = :showId "
                + "AND bs.booking.bookingStatus <> 'Cancelled'",
                Long.class)
                .setParameter("showId", showId)
                .getResultList();
    }

    @Override
    public List<Seat> findAllSeatsByScreenId(Long screenId) {
        return em.createQuery(
                "SELECT s FROM Seat s WHERE s.screenId.screenId = :screenId ORDER BY s.seatRow, s.seatNumber", entity.Seat.class)
                .setParameter("screenId", screenId)
                .getResultList();
    }

    @Override
    public void createBookingWithSeats(Booking booking, List<Seat> seats) {
        em.persist(booking);
        em.flush(); // Ensure ID is generated before creating children

        //Long bookingId = booking.getBookingId();
        // 2. Create and persist the BookedSeat entries
        for (Seat seat : seats) {

            Seat managedSeat = em.find(Seat.class, seat.getSeatId());

            BookedSeat bookedSeat = new BookedSeat();
            bookedSeat.setBookedSeatPK(
                    new BookedSeatPK(booking.getBookingId(), managedSeat.getSeatId())
            );
            bookedSeat.setBooking(booking);
            bookedSeat.setSeat(managedSeat);

            booking.getBookedSeatCollection().add(bookedSeat); // 🔥 VERY IMPORTANT
            em.persist(bookedSeat);
        }

    }
}
