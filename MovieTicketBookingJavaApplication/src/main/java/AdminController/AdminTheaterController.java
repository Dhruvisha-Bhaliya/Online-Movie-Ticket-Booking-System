/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AdminController;

import entity.Theater;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import user_bean.TheaterBeanLocal;

/**
 *
 * @author DELL
 */
@Named("adminTheaterController")
@SessionScoped
public class AdminTheaterController implements Serializable {

    @EJB
    private TheaterBeanLocal theaterBean;

    private Theater currentTheater;
    private List<Theater> theaterList;
    private boolean editMode = false;

    @PostConstruct
    public void init() {
        currentTheater = new Theater();
        loadTheaters();
    }

    private void loadTheaters() {
        theaterList = theaterBean.findallTheater();
    }

    public Theater getCurrentTheater() {
        return currentTheater;
    }

    public void setCurrentTheater(Theater currentTheater) {
        this.currentTheater = currentTheater;
    }

    public List<Theater> getTheaterList() {
        loadTheaters();
        return theaterList;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public String saveTheater() {

        Date now = new Date();

        if (!editMode) {
            currentTheater.setCreatedAt(now);
            currentTheater.setStatus("ACTIVE");
        }

        currentTheater.setUpdatedAt(now);

        if (editMode) {
            theaterBean.editTheater(currentTheater);
        } else {
            theaterBean.createTheater(currentTheater);
        }

        currentTheater = new Theater();
        editMode = false;
        loadTheaters();

        return "admin_theaters?faces-redirect=true";
    }

    public void editTheater(Theater theater) {
        this.currentTheater = theater;
        this.editMode = true;
    }

    public void deleteTheater(Theater theater) {
        theaterBean.removeTheater(theater);
        loadTheaters();
    }

    public void resetForm() {
        currentTheater = new Theater();
        editMode = false;
    }
}
