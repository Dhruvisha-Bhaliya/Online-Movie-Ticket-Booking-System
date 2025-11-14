/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package user_bean;

import entity.Seat;
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

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public List<Long> findBookedSeatIdsByShow(Long showId) {
        return em.createQuery(
                "SELECT s.seatId FROM Booking b "
                + "JOIN b.seatCollection s "
                + "WHERE b.showId.showId = :showId "
                + "AND b.bookingStatus <> 'Cancelled'",
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
}
