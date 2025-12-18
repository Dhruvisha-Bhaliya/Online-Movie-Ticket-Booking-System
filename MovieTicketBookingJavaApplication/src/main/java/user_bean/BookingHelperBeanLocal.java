/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package user_bean;

import entity.Booking;
import entity.Seat;
import jakarta.ejb.Local;
import java.util.List;

/**
 *
 * @author DELL
 */
@Local
public interface BookingHelperBeanLocal {

    List<Long> findBookedSeatIdsByShow(Long showId);

    List<entity.Seat> findAllSeatsByScreenId(Long screenId);

    void createBookingWithSeats(Booking booking, List<Seat> seats);
}
