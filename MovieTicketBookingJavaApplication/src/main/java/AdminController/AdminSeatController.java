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
import java.util.Date;
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
    private Long selectedScreenId;
    private List<Screen> screenList;
    private List<Seat> seatList;
    private List<Seat> seatsForSelectedScreen;
    private boolean editMode = false;

    @PostConstruct
    public void init() {
        currentSeat = new Seat();
        loadScreenList();
        loadSeatList();
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

    public String saveSeat() {
        Screen selectedScreen = screenBean.findScreen(selectedScreenId);
        currentSeat.setScreenId(selectedScreen);
        if (currentSeat.getStatus() == null || currentSeat.getStatus().isEmpty()) {
            currentSeat.setStatus("ACTIVE");
        }
        if (editMode) {
            currentSeat.setUpdatedAt(new Date());
            seatBean.editSeat(currentSeat);
        } else {
            Date now = new Date();
            currentSeat.setCreatedAt(now);
            currentSeat.setUpdatedAt(now);
            seatBean.createSeat(currentSeat);
        }
        resetForm();
        updateSeatsForScreen();
        loadSeatList();

        return "admin_seats?faces-redirect=true";
    }

    public void editSeat(Seat seat) {
        this.currentSeat = seat;
        this.selectedScreenId = seat.getScreenId().getScreenId();
        this.editMode = true;
    }

    public void deleteSeat(Seat seat) {
        seatBean.removeSeat(seat);
        updateSeatsForScreen();
        loadSeatList();
    }

    public void resetForm() {
        currentSeat = new Seat();
        selectedScreenId = null;
        editMode = false;
    }

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
        updateSeatsForScreen();
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
