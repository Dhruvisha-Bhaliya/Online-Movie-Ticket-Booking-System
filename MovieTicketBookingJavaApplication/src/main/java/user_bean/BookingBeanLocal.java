/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package user_bean;

import entity.Booking;
import jakarta.ejb.Local;
import java.util.List;

/**
 *
 * @author HP
 */
@Local
public interface BookingBeanLocal {

    void createBooking(Booking booking);
    void editBooking(Booking booking);
    void removeBooking(Booking booking);
    
    Booking findBooking(Long bookingId);
    List<Booking> findAllBooking();
    List<Booking> findByUserId(Long userId);
    List<Booking> findByShowId(Long showId);
    Booking cancelBooking(Long bookingId);
    Long countBookedSeatsByShow(Long showId);
    List<Booking> findActiveBookings();
}
