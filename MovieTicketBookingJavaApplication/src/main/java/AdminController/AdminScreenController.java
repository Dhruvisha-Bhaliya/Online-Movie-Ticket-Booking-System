/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AdminController;

import entity.Screen;
import entity.Theater;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import user_bean.ScreenBeanLocal;
import user_bean.TheaterBeanLocal;

/**
 *
 * @author DELL
 */
@Named("adminScreenController")
@SessionScoped
public class AdminScreenController implements Serializable {

    @EJB
    private ScreenBeanLocal screenBean;

    @EJB
    private TheaterBeanLocal theaterBean; // Inject the Theater bean to get the list of options

    private Screen currentScreen;
    private List<Screen> screenList;
    private List<Theater> theaterList; // List for the dropdown menu
    private boolean editMode = false;

    @PostConstruct
    public void init() {
        currentScreen = new Screen();
        loadData();
    }

    private void loadData() {
        screenList = screenBean.findAllScreen();
        theaterList = theaterBean.findallTheater(); // Assuming findallTheater() is the correct method
    }

    // --- Action Methods ---
    public String saveScreen() {
        if (editMode) {
            screenBean.editScreen(currentScreen);
        } else {
            screenBean.createScreen(currentScreen);
        }

        // Reset state
        currentScreen = new Screen();
        editMode = false;
        loadData();
        return "admin_screens?faces-redirect=true";
    }

    public void editScreen(Screen screen) {
        this.currentScreen = screen;
        this.editMode = true;
    }

    public void deleteScreen(Screen screen) {
        screenBean.removeScreen(screen);
        loadData();
    }

    public void resetForm() {
        currentScreen = new Screen();
        editMode = false;
    }

    // --- Getters and Setters ---
    public Screen getCurrentScreen() {
        return currentScreen;
    }

    public void setCurrentScreen(Screen currentScreen) {
        this.currentScreen = currentScreen;
    }

    public List<Screen> getScreenList() {
        loadData();
        return screenList;
    }

    public List<Theater> getTheaterList() {
        return theaterList;
    } // Getter for the dropdown items

    public boolean isEditMode() {
        return editMode;
    }

}
