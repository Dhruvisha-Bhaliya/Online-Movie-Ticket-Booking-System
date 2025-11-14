/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package userController;


import entity.Movie;
import entity.Seat; 
import entity.Showmovie;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import user_bean.BookingHelperBeanLocal;
import user_bean.MovieBeanLocal;
import user_bean.ShowBeanLocal;
import user_bean.UserBeanLocal;

/**
 *
 * @author DELL
 */
@Named("movieBookingController")
@ViewScoped
public class BookingController implements Serializable {

    @EJB
    private MovieBeanLocal movieBean;
    @EJB
    private ShowBeanLocal showBean;
    
    @EJB
    private BookingHelperBeanLocal helperBean;
    @EJB
    private UserBeanLocal userBean; // For demonstration, assume user is known

    // --- State Variables (Data) ---
    private Long currentMovieId;
    private Movie selectedMovie;
    private Long currentShowId;
    private Showmovie selectedShow;
    private Map<String, List<Showmovie>> showsByTheater;

    // --- Booking State ---
    private List<Seat> allSeats;
    private List<Long> bookedSeatIds;
    private List<Seat> selectedSeats;
    private long totalAmount = 0;
    private Long currentUserId = 1L; // **TODO: Replace with actual logged-in user ID**

    private List<Date> availableDates;
    private Date selectedDate;
    private Long minPrice;
    private Long maxPrice;

    @PostConstruct
    public void init() {
        // Initial setup for the showtime page (booking.xhtml)
        String movieIdParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("movieId");
        if (movieIdParam != null) {
            try {
                this.currentMovieId = Long.parseLong(movieIdParam);
                loadMovieAndShows();
            } catch (NumberFormatException e) {
                System.err.println("Invalid Movie ID format.");
            }
        }

    }

    // 1. Load Movie and Shows for the main showtime page
    /*private void loadMovieAndShows() {
        if (currentMovieId != null) {
            selectedMovie = movieBean.findMovie(currentMovieId);
            if (selectedMovie != null) {
                availableDates = showBean.findAvailableDates(currentMovieId);
                
                if(availableDates != null && !availableDates.isEmpty())
                {
                    selectedDate = availableDates.get(0);
                }
                List<Showmovie> allShows = showBean.findShowByMovieId(currentMovieId);
                for (Showmovie s : allShows) {
                    System.out.println("DEBUG: Show ID " + s.getShowId() + " Time = " + s.getShowTime());
                }
                groupShowsByTheater(allShows);
            }
        }
    }*/
    private void loadMovieAndShows() {
        if (currentMovieId == null) {
            return;
        }

        selectedMovie = movieBean.findMovie(currentMovieId);
        if (selectedMovie == null) {
            return;
        }

        // Load all available dates
        availableDates = showBean.findAvailableDates(currentMovieId);
        if (availableDates != null && !availableDates.isEmpty()) {
            selectedDate = availableDates.get(0);
            filterByDate(selectedDate); // default filter to first date
        }
    }

    /*public void filterByDate(Date date) {
        this.selectedDate = date;
        List<Showmovie> filteredShows = findFilteredShows();
        groupShowsByTheater(filteredShows);
    }*/
    public void filterByDate(Date date) {
        this.selectedDate = date;
        List<Showmovie> filteredShows = showBean.findShowsByMovieDateAndLanguage(currentMovieId, selectedDate, "Hindi");
        groupShowsByTheater(filteredShows);
        calculatePriceRange(filteredShows); // ✅ now price updates dynamically
    }

    private List<Showmovie> findFilteredShows() {
        if (selectedDate == null) {
            // Handle case where no date is available
            return new ArrayList<>();
        }

        // This method will call a new EJB method that filters by both date AND language (Hindi)
        return showBean.findShowsByMovieDateAndLanguage(currentMovieId, selectedDate, "Hindi");
    }

    // Utility to group shows for the BookMyShow style layout
    /*private void groupShowsByTheater(List<Showmovie> allShows) {
        showsByTheater = allShows.stream()
            .collect(Collectors.groupingBy(show -> 
                show.getScreenId().getTheaterId().getTheaterName() + 
                ": " + show.getScreenId().getTheaterId().getAddress()));
    }*/
    private void groupShowsByTheater(List<Showmovie> allShows) {
        showsByTheater = allShows.stream()
                .collect(Collectors.groupingBy(show -> {
                    // Add null checks for nested entities (Screen and Theater)
                    if (show != null && show.getScreenId() != null && show.getScreenId().getTheaterId() != null) {
                        return show.getScreenId().getTheaterId().getTheaterName()
                                + ": " + show.getScreenId().getTheaterId().getAddress();
                    }
                    // Fallback key if data is incomplete, preventing a NullPointerException
                    return "Theater Data Missing";
                }));
    }

    // 2. Action to select a showtime and move to seat selection
    public String selectShowtime(Long showId) {
        this.currentShowId = showId;
        this.selectedShow = showBean.find(showId);
        this.selectedSeats = new ArrayList<>(); // Clear previous selection
        this.totalAmount = 0;

        if (selectedShow != null) {
            // Load all seats for the screen
            allSeats = helperBean.findAllSeatsByScreenId(selectedShow.getScreenId().getScreenId());
            // Load currently booked seats
            bookedSeatIds = helperBean.findBookedSeatIdsByShow(showId);

            return "seatSelection.xhtml?faces-redirect=true"; // Navigate to the seat selection page
        }
        return null; // Stay on the current page if show not found
    }

    // 3. Action to toggle seat selection
    public void toggleSeat(Seat seat) {
        if (isSeatBooked(seat.getSeatId())) {
            // Cannot select an already booked seat
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Seat Booked", "This seat is already booked."));
            return;
        }

        if (selectedSeats.contains(seat)) {
            selectedSeats.remove(seat);
        } else {
            selectedSeats.add(seat);
        }
        calculateTotalAmount();
    }

    // Utility to check if a seat is booked (for coloring the seat map)
    public boolean isSeatBooked(Long seatId) {
        return bookedSeatIds != null && bookedSeatIds.contains(seatId);
    }

    // Utility to check if a seat is selected (for coloring the seat map)
    public boolean isSeatSelected(Seat seat) {
        return selectedSeats.contains(seat);
    }

    private void calculateTotalAmount() {
        if (selectedShow != null && selectedShow.getBasePrice() != null) {
            // Convert BigInteger to long for calculation.
            // selectedSeats.size() is int, cast to long before multiplying.
            totalAmount = (long) selectedSeats.size() * selectedShow.getBasePrice().longValue();
        } else {
            totalAmount = 0;
        }
    }

    /*public String completeBooking() {
        if (selectedSeats.isEmpty() || selectedShow == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No seats selected or show is invalid."));
            return null;
        }

        try {
            Booking newBooking = new Booking();
            newBooking.setBookingTime(new Date());
            newBooking.setTotalAmount(totalAmount);
            newBooking.setBookingStatus("Confirmed");

            // Set mandatory relationships
            User user = userBean.findUser(currentUserId);
            newBooking.setUserId(user);
            newBooking.setShowId(selectedShow);

            // ⚠️ FIX: You cannot set a List<Seat> directly. 
            // You must create the intermediate entity (BookedSeat) for the OneToMany relationship.
            Collection<BookedSeat> bookedSeats = new ArrayList<>();

            for (Seat seat : selectedSeats) {
                BookedSeat bs = new BookedSeat();

                // Link the composite entity fields
                bs.setBooking(newBooking); // Links to the Booking entity
                bs.setSeat(seat);          // Links to the Seat entity

                // If you are using a BookedSeatPK, you would initialize it here (not shown for brevity)
                bookedSeats.add(bs);
            }

            // Set the collection of BookedSeat objects on the new Booking entity.
            // This is the collection defined as @OneToMany in your Booking.java.
            newBooking.setBookedSeatCollection(bookedSeats);

            // IMPORTANT: The persist operation often needs to happen after the relationships are set.
            bookingBean.createBooking(newBooking);

            // Success message and redirect
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success!", "Booking confirmed for " + selectedSeats.size() + " seats."));

            return "bookingConfirmation.xhtml?faces-redirect=true&bookingId=" + newBooking.getBookingId();

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Booking Failed", "System error: " + e.getMessage()));
            return null;
        }
    }*/

    // ... (rest of the class getters and setters remain the same) ...

private void calculatePriceRange(List<Showmovie> shows) {
        if (shows == null || shows.isEmpty()) {
            minPrice = 0L;
            maxPrice = 0L;
            return;
        }

        // Find the minimum and maximum basePrice
        Double min = shows.stream()
                // Use a lambda to map BigInteger to long/double
                .mapToDouble(show -> show.getBasePrice() != null ? show.getBasePrice().doubleValue() : 0.0)
                .min().orElse(0.0);

        Double max = shows.stream()
                .mapToDouble(show -> show.getBasePrice() != null ? show.getBasePrice().doubleValue() : 0.0)
                .max().orElse(0.0);

        // Convert to Long for display (assuming prices are whole numbers)
        minPrice = min.longValue();
        maxPrice = max.longValue();
    }

    // --- Utility Methods (Time Formatting) ---
    public String formatTime(Date date) {
        if (date == null) {
            return "Not time  N/A";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        return sdf.format(date);
    }

    public String formatDay(Date date) {
        if (date == null) {
            return "N/A";
        }
        // Formats the day, e.g., "TUE"
        SimpleDateFormat sdf = new SimpleDateFormat("EEE");
        return sdf.format(date).toUpperCase();
    }

    public String formatMonth(Date date) {
        if (date == null) {
            return "N/A";
        }
        // Formats the month, e.g., "OCT"
        SimpleDateFormat sdf = new SimpleDateFormat("MMM");
        return sdf.format(date).toUpperCase();
    }

    // --- Getters & Setters for JSF Access ---
    public Movie getSelectedMovie() {
        return selectedMovie;
    }

    public Map<String, List<Showmovie>> getShowsByTheater() {
        return showsByTheater;
    }

    public Showmovie getSelectedShow() {
        return selectedShow;
    }

    public List<Seat> getAllSeats() {
        return allSeats;
    }

    public List<Seat> getSelectedSeats() {
        return selectedSeats;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
    }

    public List<Date> getAvailableDates() {
        return availableDates;
    }

    public void setAvailableDates(List<Date> availableDates) {
        this.availableDates = availableDates;
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }

    public Long getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Long minPrice) {
        this.minPrice = minPrice;
    }

    public Long getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Long maxPrice) {
        this.maxPrice = maxPrice;
    }

}
