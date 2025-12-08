/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package user_bean;

import entity.Screen;
import entity.Seat;
import jakarta.ejb.Local;
import java.util.List;

/**
 *
 * @author DELL
 */
@Local
public interface SeatBeanLocal {
    
    void createSeat(Seat seat);
    void editSeat(Seat seat);
    void removeSeat(Seat seat);
    Seat findSeat(Object SeatId);
    List<Seat> findAllSeats();
    List<Seat> findSeatsByScreen(Screen screen);
    List<Seat> findSeatsByScreenAndStatus(Long screenId, String status);    
    List<Seat> findSeatsByScreen(Long screenId);
}
