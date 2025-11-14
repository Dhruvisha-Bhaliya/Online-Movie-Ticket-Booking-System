/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package user_bean;

import entity.Screen;
import entity.Seat;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DELL
 */
@Stateless
public class SeatBean implements SeatBeanLocal {

    @PersistenceContext(unitName = "myMovie")
    private EntityManager em;

    @Override
    public void createSeat(Seat seat) {
        em.persist(seat);
    }

    @Override
    public void editSeat(Seat seat) {
        em.merge(seat);
    }

    @Override
    public void removeSeat(Seat seat) {
        em.remove(em.merge(seat));
    }

    @Override
    public Seat findSeat(Object SeatId) {
        return em.find(Seat.class, SeatId);
    }

    @Override
    public List<Seat> findAllSeats() {
        return em.createNamedQuery("Seat.findAll", Seat.class).getResultList();
    }

    @Override
    public List<Seat> findSeatsByScreen(Screen screen) {
        return em.createQuery("SELECT s FROM Seat s WHERE s.screenId = :screen", Seat.class).setParameter("screen", screen).getResultList();
    }

    @Override
    public List<Seat> findSeatsByScreenAndStatus(Long screenId, String status) {
        try {
            return em.createQuery(
                    "SELECT s FROM Seat s WHERE s.screenId.screenId = :screenId AND s.seatStatus = :status",
                    Seat.class
            )
                    .setParameter("screenId", screenId)
                    .setParameter("status", status)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
