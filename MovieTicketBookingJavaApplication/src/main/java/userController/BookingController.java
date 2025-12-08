/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package userController;

import entity.BookedSeat;
import entity.BookedSeatPK;
import entity.Booking;
import entity.Movie;
import entity.Screen;
import entity.Seat;
import entity.SeatCategory;
import entity.Showmovie;
import entity.User;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import user_bean.BookingHelperBeanLocal;
import user_bean.MovieBeanLocal;
import user_bean.SeatBeanLocal;
import user_bean.ShowBeanLocal;
import user_bean.UserBeanLocal;
import user_bean.seatCategoryBeanLocal;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.ComponentSystemEvent;
import java.util.Collection;
import user_bean.BookingBeanLocal;

/**
 *
 * @author DELL
 */
@Named("movieBookingController")
@SessionScoped
public class BookingController implements Serializable {

    @EJB
    private MovieBeanLocal movieBean;
    @EJB
    private ShowBeanLocal showBean;

    @EJB
    private seatCategoryBeanLocal seatCategoryBean;

    @EJB
    private BookingHelperBeanLocal helperBean;
    @EJB
    private UserBeanLocal userBean; 

    @EJB
    private SeatBeanLocal seatBean;

    @EJB
    private BookingBeanLocal bookingBean;

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
    private Long currentUserId = 1L;

    private List<Date> availableDates;
    private Date selectedDate;
    private Long minPrice;
    private Long maxPrice;

    private List<Seat> seatsForScreen;
    private String selectedSeatIdsString;

    private List<SeatCategory> categoriesForShow;
    private SeatCategory lastRenderedCategory;

    private User currentUser;
    private Booking currentBooking;

    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String bookingParam = params.get("bookingId");
        String movieIdParam = params.get("movieId");
        String showIdParam = params.get("showId");
        String numSeatsParam = params.get("numSeats");

        if (bookingParam != null) {
            try {
                Long bookingId = Long.parseLong(bookingParam);
                this.currentBooking = bookingBean.findBooking(bookingId);

                if (this.currentBooking != null) {
                    
                    this.selectedShow = this.currentBooking.getShowId();
                    this.selectedMovie = this.selectedShow != null ? this.selectedShow.getMovieId() : null;
                    this.totalAmount = this.currentBooking.getTotalAmount(); 

                    
                    this.selectedSeats = this.currentBooking.getBookedSeatCollection().stream()
                            .map(BookedSeat::getSeat)
                            .collect(Collectors.toList());
                    
                } else {
                    System.err.println("Booking not found for ID: " + bookingId);
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid Booking ID format.");
            }
        }

        
        if (movieIdParam != null) {
            try {
                this.currentMovieId = Long.parseLong(movieIdParam);
                this.selectedMovie = movieBean.findMovie(this.currentMovieId);
            } catch (NumberFormatException e) {
                System.err.println("Invalid Movie ID format.");
            }
        }

        
        if (showIdParam != null) {
            try {
                this.currentShowId = Long.parseLong(showIdParam);
                this.selectedShow = showBean.find(this.currentShowId);

                try {
                    if (numSeatsParam != null && !numSeatsParam.isEmpty()) {
                        this.numberOfSeats = Integer.parseInt(numSeatsParam);
                    } else {
                        this.numberOfSeats = 1;
                    }
                } catch (Exception e) {
                    this.numberOfSeats = 1;
                }

                if (this.selectedShow != null && selectedShow.getMovieId() != null) {
                    this.currentMovieId = selectedShow.getMovieId().getMovieId();
                }

                
                if (this.selectedShow != null && selectedShow.getScreenId() != null) {
                    //Long screenIdToFetch = this.selectedShow.getScreenId().getScreenId();
                    Screen screen = selectedShow.getScreenId();

                    if (screen.getScreenId() != null) {

                        this.seatsForScreen = seatBean.findSeatsByScreen(screen.getScreenId());

                        System.out.println("Loaded seats: " + seatsForScreen.size());

                    }
                   
                    this.bookedSeatIds = helperBean.findBookedSeatIdsByShow(this.currentShowId);

                    
                    if (currentBooking == null) {
                        this.selectedSeats = new ArrayList<>();
                        this.totalAmount = 0;
                    }

                    
                    if (this.currentMovieId == null && this.selectedShow.getMovieId() != null) {
                        this.currentMovieId = this.selectedShow.getMovieId().getMovieId();
                        this.selectedMovie = this.selectedShow.getMovieId();
                    }
                } else {
                    System.err.println("Show or Screen details missing for seat loading.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid Show ID or Number of Seats format.");

            }
        }

       
        if (this.currentMovieId != null && showIdParam == null) {
            loadMovieAndShows();
        }

        // 4. Filter shows (only necessary for the main booking page)
        if (this.selectedDate != null && this.currentMovieId != null && this.showsByTheater == null && showIdParam == null) {
            filterByDate(selectedDate);
        }

        if (this.selectedMovie == null) {
            System.out.println("DEBUG: Movie context failed to load in init(). Page will be blank.");
        } else {
            System.out.println("DEBUG: Movie context loaded successfully for ID: " + this.currentMovieId);
        }

        try {
            currentUser = userBean.findUser(currentUserId);
        } catch (Exception e) {
            currentUser = null;
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
        } else {
            this.selectedDate = null;
            this.showsByTheater = new java.util.HashMap<>();
        }
    }

    public void filterByDate(Date date) {
        this.selectedDate = date;
        List<Showmovie> filteredShows = showBean.findShowsByMovieDateAndLanguage(currentMovieId, selectedDate, "Hindi");
        groupShowsByTheater(filteredShows);
        calculatePriceRange(filteredShows);

    }

    private List<Showmovie> findFilteredShows() {
        if (selectedDate == null) {
            // Handle case where no date is available
            return new ArrayList<>();
        }

       
        return showBean.findShowsByMovieDateAndLanguage(currentMovieId, selectedDate, "Hindi");
    }

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

   
    public String selectShowtime(Long showId) {
        this.currentShowId = showId;
        this.selectedShow = showBean.find(showId);
        this.selectedSeats = new ArrayList<>(); // Clear previous selection
        this.totalAmount = 0;

        if (selectedShow != null && currentMovieId != null) {
            // Load all seats for the screen
            allSeats = helperBean.findAllSeatsByScreenId(selectedShow.getScreenId().getScreenId());
            // Load currently booked seats
            bookedSeatIds = helperBean.findBookedSeatIdsByShow(showId);

            return "SeatSelection.xhtml?faces-redirect=true&showId=" + showId + "&movieId=" + currentMovieId; // Navigate to the seat selection page
        }
        return null;
    }

   
    public void toggleSeat(Seat seat) {
        if (isSeatBooked(seat.getSeatId())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Seat Booked", "This seat is already booked."));
            return;
        }

        if (selectedSeats.contains(seat)) {
            // Deselect
            selectedSeats.remove(seat);
        } else {
            // Select - Enforce constraint
            if (selectedSeats.size() < this.numberOfSeats) { // <<< CRITICAL CONSTRAINT CHECK
                selectedSeats.add(seat);
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Limit Reached", "You can only select " + this.numberOfSeats + " seats."));
                return;
            }
        }
        calculateTotalAmount();

        
        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("bookingSummary");
    }

    public boolean isSeatBooked(Long seatId) {
        return bookedSeatIds != null && bookedSeatIds.contains(seatId);
    }

    public boolean isSeatSelected(Seat seat) {
        return selectedSeats.contains(seat);
    }

    public void calculateTotalAmount() {
        this.totalAmount = 0; // Reset total
        if (selectedSeats != null && !selectedSeats.isEmpty()) {
            long sum = 0;
            for (Seat seat : selectedSeats) {
                // CRITICAL: Assuming your Seat entity has a relation to SeatCategory
                // and SeatCategory has a BigInteger price field.
                if (seat.getCategoryId() != null && seat.getCategoryId().getPrice() != null) {
                    sum += seat.getCategoryId().getPrice().longValue();
                }
            }
            this.totalAmount = sum;
        }
    }

    public String completeBooking() {
        if (selectedSeats == null || selectedSeats.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", "Please select seats."));
            return null;
        }

        try {
            // Step 1: Create base booking
            Booking newBooking = new Booking();
            newBooking.setBookingTime(new Date());
            newBooking.setBookingStatus("Confirmed");

            newBooking.setTotalAmount(totalAmount);
            User user = userBean.findUser(currentUserId);
            newBooking.setUserId(user);
            newBooking.setShowId(selectedShow);
            newBooking.setCreatedAt(new Date());
            newBooking.setUpdatedAt(new Date());

            bookingBean.createBooking(newBooking);
            Long bookingId = newBooking.getBookingId();
            if (bookingId == null) {
                throw new RuntimeException("Booking ID is null after persist!");
            }

            Collection<BookedSeat> bookedSeatList = new ArrayList<>();

            for (Seat seat : selectedSeats) {

                BookedSeatPK pk = new BookedSeatPK(bookingId, seat.getSeatId());

                BookedSeat bs = new BookedSeat();
                bs.setBookedSeatPK(pk);
                bs.setBooking(newBooking);
                bs.setSeat(seat);

                bookedSeatList.add(bs);
            }

           
            newBooking.setBookedSeatCollection(bookedSeatList);
            bookingBean.editBooking(newBooking);

            return "payment_processesConfirmdetail.xhtml?faces-redirect=true&bookingId=" + bookingId;

        } catch (Exception ex) {
            ex.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_FATAL,
                            "Booking Failed", ex.getMessage()));
            return null;
        }
    }

    /*  public void cancelBookingIfWithin2Hours(Booking booking) {
        Date now = new Date();
        long diff = now.getTime() - booking.getCreatedAt().getTime();
        if (diff <= 2 * 60 * 60 * 1000) { // 2 hours in milliseconds
            booking.setBookingStatus("Cancelled");
            booking.setStatus("Inactive");
            bookingRepository.save(booking); // save changes
        } else {
            System.out.println("Cannot cancel, more than 2 hours passed.");
        }
    }*/
    private void calculatePriceRange(List<Showmovie> shows) {
        if (shows == null || shows.isEmpty()) {
            minPrice = 0L;
            maxPrice = 0L;
            return;
        }

        List<Long> allPrices = new ArrayList<>();

        
        Set<Screen> uniqueScreens = shows.stream()
                .map(Showmovie::getScreenId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        for (Screen screen : uniqueScreens) {
 
            List<SeatCategory> categories = seatCategoryBean.findByScreenId(screen.getScreenId());
            categories.stream()
                    .filter(cat -> cat.getPrice() != null)
                    .map(cat -> cat.getPrice().longValue())
                    .forEach(allPrices::add);
        }

        if (allPrices.isEmpty()) {
            minPrice = 0L;
            maxPrice = 0L;
            return;
        }

        minPrice = allPrices.stream().min(Long::compare).orElse(0L);
        maxPrice = allPrices.stream().max(Long::compare).orElse(0L);
    }

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
        SimpleDateFormat sdf = new SimpleDateFormat("EEE");
        return sdf.format(date).toUpperCase();
    }

    public String formatMonth(Date date) {
        if (date == null) {
            return "N/A";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MMM");
        return sdf.format(date).toUpperCase();
    }

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

    private Long selectedShowIdForBooking;
    private int numberOfSeats = 1;

    /*public String proceedToSeatLayout() {
        // 1. Get the selected Show object using the selectedShowIdForBooking
        selectedShow = showBean.find(selectedShowIdForBooking);

        if (selectedShow != null) {
            // 2. Fetch all seats for the screen associated with this show
            // Note: Assuming a Show object has a relation to a Screen object.
            Screen screen = selectedShow.getScreenId(); // Assuming getScreenId() returns the Screen object
            if (screen != null) {
                // Fetch all seats (Available and Booked) for this screen
                seatsForScreen = seatBean.findSeatsByScreen(screen);
            }

            // 3. Initialize selectedSeats list
            selectedSeats = new ArrayList<>();

            // 4. Return the navigation outcome to seats.xhtml
            return "SeatSelection.xhtml?faces-redirect=true";
        }

        // Handle error or return to current page
        return "booking.xhtml";
    }*/
    // In userController.BookingController.java
    public String proceedToSeatLayout() {
        
        selectedShow = showBean.find(selectedShowIdForBooking);

        
        if (this.selectedMovie == null && this.currentMovieId != null) {
            this.selectedMovie = movieBean.findMovie(currentMovieId);
        }

        if (selectedShow != null && selectedMovie != null) {
        
            Screen screen = selectedShow.getScreenId();
            if (screen != null) {
                Long screenIdToFetch = screen.getScreenId();
                seatsForScreen = seatBean.findSeatsByScreen(screenIdToFetch);
            }

            bookedSeatIds = helperBean.findBookedSeatIdsByShow(selectedShowIdForBooking);

            this.selectedSeats = new ArrayList<>();
            this.totalAmount = 0;

            return "SeatSelection.xhtml?faces-redirect=true&showId=" + selectedShowIdForBooking + "&movieId=" + currentMovieId + "&numSeats=" + numberOfSeats;
        }

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not load show or movie details for Booking."));
        return null;
    }

    public Long getSelectedShowIdForBooking() {
        return selectedShowIdForBooking;
    }

    public void setSelectedShowIdForBooking(Long selectedShowIdForBooking) {
        this.selectedShowIdForBooking = selectedShowIdForBooking;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public List<Seat> getSeatsForScreen() {
        return seatsForScreen;
    }

    public void setSeatsForScreen(List<Seat> seatsForScreen) {
        this.seatsForScreen = seatsForScreen;
    }

    public Map<Character, List<Seat>> getSeatsGroupedByRow() {
        if (seatsForScreen == null || seatsForScreen.isEmpty()) {
            return new java.util.HashMap<>();
        }

        List<Seat> sortedSeats = seatsForScreen.stream()
                .sorted(Comparator
                        .comparing(Seat::getSeatRow)
                        .thenComparing(Seat::getSeatNumber))
                .collect(Collectors.toList());

        return sortedSeats.stream()
                .collect(Collectors.groupingBy(Seat::getSeatRow));
    }

    public String getSelectedSeatIdsString() {
        return selectedSeatIdsString;
    }

    public void setSelectedSeatIdsString(String selectedSeatIdsString) {
        this.selectedSeatIdsString = selectedSeatIdsString;
    }

    public List<Integer> getNumberOfSeatsOptions() {
        return Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    public List<Map.Entry<Character, List<Seat>>> getSortedSeatsGroupedByRowEntries() {
        if (getSeatsGroupedByRow() == null || getSeatsGroupedByRow().isEmpty()) {
            return new ArrayList<>();
        }

        Map<Character, List<Seat>> seatsMap = getSeatsGroupedByRow();

 
        return seatsMap.entrySet().stream()
            
                .sorted(Map.Entry.comparingByKey())
                
                .collect(Collectors.toList());
    }

    public String updateSeatSelection() {
        
        calculateTotalAmount();
        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("bookingSummary");
        return null;
    }

    public List<SeatCategory> getCategoriesForSelectedScreen() {
        if (selectedShow != null && selectedShow.getScreenId() != null) {
            Long screenId = selectedShow.getScreenId().getScreenId();
            return seatCategoryBean.findByScreenId(screenId);
        }
        return new ArrayList<>();
    }

    public SeatCategory getLastRenderedCategory() {
        return lastRenderedCategory;
    }

    public void setLastRenderedCategory(SeatCategory lastRenderedCategory) {
        this.lastRenderedCategory = lastRenderedCategory;
    }

    public SeatCategory categoryNameForRow(List<Seat> seatsInRow) {
        try {
            if (seatsInRow == null || seatsInRow.isEmpty()) {
                return null;
            }

            return seatsInRow.get(0).getCategoryId();

        } catch (Exception e) {
            System.err.println("Error in categoryNameForRow: " + e.getMessage());

            return null;
        }

    }

    public void rowRenderListener(ComponentSystemEvent event) throws AbortProcessingException {
        Object rowSeatsObj = event.getComponent().getAttributes().get("rowSeats");

        if (rowSeatsObj != null) {
            List<Seat> rowSeats = (List<Seat>) rowSeatsObj;
            if (!rowSeats.isEmpty()) {
                this.lastRenderedCategory = rowSeats.get(0).getCategoryId();
            }
        }
    }

    public boolean shouldRenderCategoryHeader(List<Seat> currentSeats) {
        if (currentSeats == null || currentSeats.isEmpty()) {
            return false;
        }
        SeatCategory currentCategory = currentSeats.get(0).getCategoryId();
        if (lastRenderedCategory == null || !lastRenderedCategory.equals(currentCategory)) {
            lastRenderedCategory = currentCategory;
            return true; 
        }
        return false; 

    }

    public String formatCategoryForCSS(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return "default";
        }
      
        return categoryName.toLowerCase().replaceAll("\\s+", "-").replaceAll("[^a-z0-9\\-]+", "");
    }

    public Long getPriceForCategoryRow(List<Seat> rowSeats) {
        SeatCategory cat = categoryNameForRow(rowSeats);
        if (cat != null && cat.getPrice() != null) {
            return cat.getPrice().longValue();
        }
        return 0L;
    }

    public boolean isFirstRowForCategory(String rowStr) {
        if (rowStr == null || rowStr.isEmpty()) {
            return false;
        }
        char row = rowStr.charAt(0);
        List<SeatCategory> categories = getCategoriesForSelectedScreen();
        if (categories == null || categories.isEmpty()) {
            return false;
        }

        for (SeatCategory cat : categories) {
            if (row >= cat.getRowStart().charAt(0) && row <= cat.getRowEnd().charAt(0)) {
                return row == cat.getRowStart().charAt(0);
            }
        }
        return false;
    }

    public SeatCategory getCategoryForRow(Character row) {
        if (row == null) {
            return null;
        }

        List<SeatCategory> categories = getCategoriesForSelectedScreen();
        if (categories == null) {
            return null;
        }

        for (SeatCategory cat : categories) {
            if (row >= cat.getRowStart().charAt(0) && row <= cat.getRowEnd().charAt(0)) {
                return cat;
            }
        }
        return null;
    }

    public String getSelectedSeatsAsString() {
        List<Seat> seats = (currentBooking != null)
                ? currentBooking.getBookedSeatCollection()
                        .stream()
                        .map(BookedSeat::getSeat)
                        .collect(Collectors.toList())
                : selectedSeats;

        if (seats == null || seats.isEmpty()) {
            return "No seats selected";
        }

        return seats.stream()
                .sorted(Comparator.comparing(Seat::getSeatRow)
                        .thenComparing(Seat::getSeatNumber))
                .map(seat -> seat.getSeatRow() + "" + seat.getSeatNumber())
                .collect(Collectors.joining(", "));
    }

    public long getSubTotalAmountWithoutFees() {
        return (currentBooking != null) ? currentBooking.getTotalAmount() : totalAmount;
    }

    public long getTotalAmountWithFees() {
        long subTotal = getSubTotalAmountWithoutFees(); 
        return subTotal
                + getConvenienceFees()
                + getDonationAmount();
    }

    public long getConvenienceFees() {
        return 30;
    }

    public long getDonationAmount() {
        if (selectedSeats == null) {
            return 0;
        }
        return selectedSeats.size() * 2;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public Booking getCurrentBooking() {
        return currentBooking;
    }

    public int getBookedSeatCount() {
        if (currentBooking != null && currentBooking.getBookedSeatCollection() != null) {
            return currentBooking.getBookedSeatCollection().size();
        }
        return 0;
    }

    public void updateUser() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
        String field = params.get("field");
        String newValue = params.get("newValue");

        if (field != null && newValue != null) {
            if ("email".equals(field)) {
                currentUser.setEmail(newValue);
            } else if ("phoneno".equals(field)) {
                try {
                    String sanitized = newValue.replaceAll("[^0-9]", "");
                    currentUser.setPhoneno(new java.math.BigInteger(sanitized));
                } catch (NumberFormatException e) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                    "Invalid Phone Number", "Please enter a valid numeric phone number."));
                    return;
                }
            }

            userBean.updateUser(currentUser); 

            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Updated!", field + " updated successfully."));
        }
    }
    
    /*private String editField;
    private String editValue;

    public String getEditField() {
        return editField;
    }

    public void setEditField(String editField) {
        this.editField = editField;
    }

    public String getEditValue() {
        return editValue;
    }

    public void setEditValue(String editValue) {
        this.editValue = editValue;
    }
    
    */

}
