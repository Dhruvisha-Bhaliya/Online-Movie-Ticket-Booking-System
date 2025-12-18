/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package user_bean;

import entity.Booking;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;

/**
 *
 * @author HP
 */
@Stateless
public class BookingBean implements BookingBeanLocal {

    @PersistenceContext(unitName = "myMovie")
    private EntityManager em;

    @Override
    public void createBooking(Booking booking) {
        em.persist(booking);
        em.flush();

    }

    @Override
    public void editBooking(Booking booking) {
        em.merge(booking);
    }

    @Override
    public void removeBooking(Booking booking) {
        em.remove(em.merge(booking));
    }

    @Override
    public Booking findBooking(Long bookingId) {
        return em.find(Booking.class, bookingId);
    }

    @Override
    public List<Booking> findAllBooking() {
        return em.createQuery("SELECT b FROM Booking b", Booking.class).getResultList();
    }

    @Override
    public List<Booking> findByUserId(Long userId) {
        return em.createQuery("SELECT b FROM Booking b WHERE b.userId.userId = :uid", Booking.class)
                .setParameter("uid", userId)
                .getResultList();
    }

    @Override
    public List<Booking> findByShowId(Long showId) {
        return em.createQuery("SELECT b FROM Booking b WHERE b.showId.showId = :sid", Booking.class)
                .setParameter("sid", showId)
                .getResultList();
    }

    @Override
    public Booking cancelBooking(Long bookingId) {
        Booking booking = em.find(Booking.class, bookingId);
        if (booking != null) {
            booking.setBookingStatus("Cancelled");
            em.merge(booking);
        }
        return booking;
    }

    @Override
    public Long countBookedSeatsByShow(Long showId) {
        return em.createQuery(
                "SELECT COUNT(b) FROM Booking b WHERE b.showId.showId = :sid AND b.bookingStatus <> 'Cancelled'",
                Long.class)
                .setParameter("sid", showId)
                .getSingleResult();
    }

    @Override
    public List<Booking> findActiveBookings() {
        return em.createQuery("SELECT b FROM Booking b WHERE b.bookingStatus <> 'Cancelled'", Booking.class)
                .getResultList();
    }

   
}
