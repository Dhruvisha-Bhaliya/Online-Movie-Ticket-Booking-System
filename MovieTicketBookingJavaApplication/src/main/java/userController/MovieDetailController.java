/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package userController;

import entity.Movie;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import user_bean.MovieBeanLocal;

/**
 *
 * @author DELL
 */
@Named(value="movieDetailController")
@ViewScoped
public class MovieDetailController implements Serializable{
    @EJB
    private MovieBeanLocal movieBean;
    
    // 1. This property will be set by the <f:param> tag in the URL.
    private Long movieId; 
    
    // 2. This property holds the movie data fetched from the database.
    private Movie currentMovie;

    /* * The setMovieId method is called by JSF when the URL parameter 'movieId' is present.
     */
    public void setMovieId(Long movieId) {
        this.movieId = movieId;
        loadMovie(); 
    }
    
    /*
     * Fetches the movie from the database using the captured ID.
     */
    public void loadMovie() {
        if (this.movieId != null) {
            this.currentMovie = movieBean.findMovie(this.movieId);
            
           if (this.currentMovie == null) {
                // 1. Log or Print an error
                System.out.println("ERROR: Movie with ID " + this.movieId + " not found.");
                
                // 2. Add a message to display to the user
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Movie Not Found", 
                                     "The requested movie could not be located."));
            }
        }
    }

    // --- Getters required by movieDetail.xhtml ---
    
    public Long getMovieId() {
        return movieId;
    }

    public Movie getCurrentMovie() {
        return currentMovie;
    }
}
