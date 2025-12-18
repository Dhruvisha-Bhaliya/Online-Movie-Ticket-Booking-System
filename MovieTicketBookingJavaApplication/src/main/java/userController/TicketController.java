/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package userController;

import entity.Booking;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import user_bean.BookingBeanLocal;

/**
 *
 * @author HP
 */
@Named("ticketViewController")
@ViewScoped
public class TicketController implements Serializable{
    @EJB
    private BookingBeanLocal bookingBean;

    // Use a URL parameter to pass the booking ID or retrieve it from a session/request attribute.
    private Long bookingId; 
    private Booking booking;

    public void loadTicket() {
        if (bookingId != null) {
            this.booking = bookingBean.findBooking(bookingId);
            // Add a check here if booking is null or status is not CONFIRMED
            if (this.booking == null || !"CONFIRMED".equals(this.booking.getBookingStatus())) {
                // Redirect to error page or home if ticket is not valid
            }
        }
    }

    // Getters and Setters for bookingId and booking
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public Booking getBooking() { return booking; }
}
