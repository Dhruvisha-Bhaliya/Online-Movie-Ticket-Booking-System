/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AdminController;

import entity.Screen;
import entity.Seat;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import user_bean.ScreenBeanLocal;
import user_bean.SeatBeanLocal;

/**
 *
 * @author DELL
 */
@Named("adminSeatController")
@SessionScoped
public class AdminSeatController implements Serializable {

    @EJB
    private SeatBeanLocal seatBean;
    @EJB
    private ScreenBeanLocal screenBean;

    private Seat currentSeat;
    private Long selectedScreenId; // For dropdown selection
    private List<Screen> screenList;
    private List<Seat> seatList; // All seats
    private List<Seat> seatsForSelectedScreen; // Seats filtered by screen
    private boolean editMode = false;

    @PostConstruct
    public void init() {
        currentSeat = new Seat();
        loadScreenList();
        loadSeatList(); // Load all seats initially
    }

    public void loadScreenList() {
        screenList = screenBean.findAllScreen();
    }

    private void loadSeatList() {
        seatList = seatBean.findAllSeats();
    }

    public void updateSeatsForScreen() {
        seatsForSelectedScreen = new ArrayList<>();
        if (selectedScreenId != null) {
            Screen screen = screenBean.findScreen(selectedScreenId);
            if (screen != null) {
                seatsForSelectedScreen = seatBean.findSeatsByScreen(screen);
            }
        }
    }

    // --- CRUD Action Methods ---
    public String saveSeat() {
        Screen selectedScreen = screenBean.findScreen(selectedScreenId);
        currentSeat.setScreenId(selectedScreen); // Set the foreign key

        if (editMode) {
            seatBean.editSeat(currentSeat);
        } else {
            seatBean.createSeat(currentSeat);
        }

        resetForm(); // Reset for next add/edit
        updateSeatsForScreen(); // Refresh the displayed list
        loadSeatList(); // Keep the main list updated for general purposes

        return "admin_seats?faces-redirect=true";
    }

    public void editSeat(Seat seat) {
        this.currentSeat = seat;
        this.selectedScreenId = seat.getScreenId().getScreenId(); // Set dropdown value
        this.editMode = true;
    }

    public void deleteSeat(Seat seat) {
        seatBean.removeSeat(seat);
        updateSeatsForScreen(); // Refresh the displayed list
        loadSeatList();
    }

    public void resetForm() {
        currentSeat = new Seat();
        selectedScreenId = null;
        editMode = false;
    }

    // --- Getters and Setters ---
    public Seat getCurrentSeat() {
        return currentSeat;
    }

    public void setCurrentSeat(Seat currentSeat) {
        this.currentSeat = currentSeat;
    }

    public Long getSelectedScreenId() {
        return selectedScreenId;
    }

    public void setSelectedScreenId(Long selectedScreenId) {
        this.selectedScreenId = selectedScreenId;
        updateSeatsForScreen(); // Auto-update when screen selection changes
    }

    public List<Screen> getScreenList() {
        return screenList;
    }

    public List<Seat> getSeatList() {
        return seatList;
    }

    public List<Seat> getSeatsForSelectedScreen() {
        return seatsForSelectedScreen;
    }

    public boolean isEditMode() {
        return editMode;
    }

}
