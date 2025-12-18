/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package userController;

import cdi.UserAuthBean;
import entity.BookedSeat;
import entity.Booking;
import entity.Movie;
import entity.Screen;
import entity.Seat;
import entity.SeatCategory;
import entity.Showmovie;
import entity.User;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
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
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import java.math.BigInteger;
import user_bean.BookingBeanLocal;
import user_bean.PaymentBeanLocal;

/**
 *
 * @author DELL
 */
@Named("movieBookingController")
@ViewScoped
public class BookingController implements Serializable {

    private static final long serialVersionUID = 1L;
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

    @Inject
    private UserAuthBean userAuthBean;

    @Inject
    private CashFreePaymentPage cashFreePayment;
    
    @EJB
    private PaymentBeanLocal paymentBean;

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
    private Long currentUserId;

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
    private String movieIdParam;

    private String fieldName;
    private String newValue;

    private Long bookingId;
    private double totalAmountWithFees;
    private Booking booking;

    @PostConstruct
    public void init() {

        FacesContext fc = FacesContext.getCurrentInstance();

        currentUser = userAuthBean.getLoggedUser();
        if (currentUser == null) {
            System.err.println("No logged in user found via userAuthBean! Booking may fail unless user logs in.");
        } else {
            currentUserId = currentUser.getUserId();
        }

        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        String bookingParam = params.get("bookingId");
        String showIdParam = params.get("showId");
        String numSeatsParam = params.get("numSeats");
        movieIdParam = params.get("movieId");

        if (bookingParam != null) {
            try {
                long bookingId = Long.parseLong(bookingParam);
                currentBooking = bookingBean.findBooking(bookingId);

                if (currentBooking != null) {
                    selectedShow = currentBooking.getShowId();
                    selectedMovie = selectedShow.getMovieId();
                    currentMovieId = selectedMovie.getMovieId();
                    selectedSeats = currentBooking.getBookedSeatCollection()
                            .stream().map(BookedSeat::getSeat)
                            .collect(Collectors.toList());
                    totalAmount = currentBooking.getTotalAmount();
                }

            } catch (Exception ex) {
                System.err.println("Invalid booking param");
            }
        }

        if (selectedMovie == null && movieIdParam != null) {
            try {
                currentMovieId = Long.parseLong(movieIdParam);
                selectedMovie = movieBean.findMovie(currentMovieId);
            } catch (Exception e) {
                selectedMovie = null;
            }
        }

        if (showIdParam != null) {
            try {
                currentShowId = Long.parseLong(showIdParam);
                selectedShow = showBean.find(currentShowId);

                numberOfSeats = (numSeatsParam != null && !numSeatsParam.isEmpty())
                        ? Integer.parseInt(numSeatsParam)
                        : 1;

                if (selectedShow != null) {

                    if (selectedMovie == null) {
                        selectedMovie = selectedShow.getMovieId();
                    }

                    currentMovieId = selectedMovie.getMovieId();
                    Screen screen = selectedShow.getScreenId();
                    seatsForScreen = seatBean.findSeatsByScreen(screen.getScreenId());

                    bookedSeatIds = helperBean.findBookedSeatIdsByShow(currentShowId);

                    if (currentBooking == null) {
                        selectedSeats = new ArrayList<>();
                        totalAmount = 0;
                    }
                }

            } catch (Exception e) {
                System.err.println("Invalid show param");
            }
        }

        if (selectedShow == null && currentMovieId != null) {
            loadMovieAndShows();
            availableDates = showBean.findAvailableDates(currentMovieId);
            if (selectedDate == null && availableDates != null && !availableDates.isEmpty()) {
                selectedDate = availableDates.get(0);
            }

            if (selectedDate != null) {
                filterByDate(selectedDate);
            }
        }

        if (currentUserId != null) {
            try {
                currentUser = userBean.findUser(currentUserId);
            } catch (Exception e) {
                System.err.println("user reload filed");
            }

        }
        if (selectedShow != null && selectedMovie == null) {
            selectedMovie = selectedShow.getMovieId();
            if (selectedMovie != null) {
                currentMovieId = selectedMovie.getMovieId();
                System.out.println("DEBUG: Movie auto-recovered from show.");
            }
        }

        if (selectedMovie == null) {
            System.out.println("DEBUG: Movie context FAILED.");
        } else {
            System.out.println("DEBUG: Movie context OK.");
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

        User loggedUser = userAuthBean.getLoggedUser();
        if (loggedUser == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Login Required", "Please login to continue booking."));
            return null;
        }

        try {
            Date now = new Date();
            
            User managedUser = userBean.findUser(loggedUser.getUserId());
            Showmovie managedShow = showBean.find(selectedShow.getShowId());
            
            Booking booking = new Booking();
            booking.setBookingTime(now);
            booking.setCreatedAt(now);
            booking.setUpdatedAt(now);
            booking.setBookingStatus("PENDING_PAYMENT"); // IMPORTANT
            booking.setTotalAmount(totalAmount);
            booking.setUserId(managedUser);
            booking.setShowId(managedShow);
            booking.setStatus("active");
            
             List<Seat> seatsToBook = new ArrayList<>(selectedSeats);
            helperBean.createBookingWithSeats(booking, seatsToBook);
            Long bookingId = booking.getBookingId();
            
            if(bookingId == null){
                throw new RuntimeException("Booking ID was not generated by EJB persistence");
            }
            
            System.out.println("DEBUG: bookingID : "+bookingId);
            return "payment_processesConfirmdetail.xhtml?faces-redirect=true&bookingId=" + bookingId;

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_FATAL,
                            "Booking Failed", "Please try again"));
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
            this.currentShowId = selectedShowIdForBooking;

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
    public String getMovieIdParam() {
        return movieIdParam;
    }

    public void setMovieIdParam(String movieIdParam) {
        this.movieIdParam = movieIdParam;
    }

    public String updateUserField() {

        if (currentUser == null) {
            return null;
        }

        try {
            if ("email".equals(fieldName)) {
                currentUser.setEmail(newValue);
            } else if ("phoneno".equals(fieldName)) {
                String sanitized = newValue.replaceAll("[^0-9]", "");
                currentUser.setPhoneno(new BigInteger(sanitized));
            }

            userBean.updateUser(currentUser);

            User refreshedUser = userBean.findUser(currentUser.getUserId());

            this.currentUser = refreshedUser;

            FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getSessionMap()
                    .put("loggedInUser", refreshedUser);

            FacesContext.getCurrentInstance().addMessage(
                    null, new FacesMessage("Updated successfully")
            );

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(
                    null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Update failed", "Please try again")
            );
        }

        return null;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

//    public String proceedToPayment() {
//
//        String orderId = "ORD_" + System.currentTimeMillis();
//        double amount = (double) getTotalAmountWithFees();
//        if (amount <= 0.0) {
//            FacesContext.getCurrentInstance().addMessage(null,
//                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Total amount is zero. Cannot proceed to pay."));
//            return null; // Stop the process
//        }
//        return cashFreePayment.preparePayment(orderId, amount);
//    }

}
