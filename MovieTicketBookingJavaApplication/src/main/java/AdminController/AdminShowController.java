/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AdminController;

import entity.Movie;
import entity.Screen;
import entity.Showmovie;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import user_bean.MovieBeanLocal;
import user_bean.ScreenBeanLocal;
import user_bean.ShowBeanLocal;

/**
 *
 * @author DELL
 */
@Named("adminShowController")
@SessionScoped
public class AdminShowController implements Serializable {

    @EJB
    private ShowBeanLocal showBean;
    @EJB
    private MovieBeanLocal movieBean;
    @EJB
    private ScreenBeanLocal screenBean;

    private Showmovie currentShow;
    private List<Showmovie> showList;
    private List<Movie> movieList;
    private List<Screen> screenList;
    private boolean editMode = false;

    @PostConstruct
    public void init() {
        currentShow = new Showmovie();
        loadData();
    }

    private void loadData() {
        showList = showBean.findAllShows();
        movieList = movieBean.getAllMovies();
        screenList = screenBean.findAllScreen();
    }

    // --- CRUD Action Methods (Standard pattern) ---
    public String saveShow() {

        if (!editMode) {
            currentShow.setCreatedAt(new Date());
            currentShow.setStatus("ACTIVE");
        }

        currentShow.setUpdatedAt(new Date());

        if (editMode) {
            showBean.editShow(currentShow);
        } else {
            showBean.createShow(currentShow);
        }

        currentShow = new Showmovie();
        editMode = false;
        loadData();

        return "admin_shows?faces-redirect=true";
    }

    public void editShow(Showmovie show) {
        this.currentShow = show;
        this.editMode = true;
    }

    public void deleteShow(Showmovie show) {
        showBean.removeShow(show);
        loadData();
    }

    public void resetForm() {
        currentShow = new Showmovie();
        editMode = false;
    }

    // --- Getters and Setters ---
    public Showmovie getCurrentShow() {
        return currentShow;
    }

    public void setCurrentShow(Showmovie currentShow) {
        this.currentShow = currentShow;
    }

    public List<Showmovie> getShowList() {
        return showList;
    }

    public List<Movie> getMovieList() {
        return movieList;
    }

    public List<Screen> getScreenList() {
        return screenList;
    }

    public boolean isEditMode() {
        return editMode;
    }
}
