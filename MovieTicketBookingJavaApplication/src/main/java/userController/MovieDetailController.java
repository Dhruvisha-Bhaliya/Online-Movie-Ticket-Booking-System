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
    private Long movieId; 
    private Movie currentMovie;

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
        loadMovie(); 
    }
    
  
    public void loadMovie() {
        if (this.movieId != null) {
            this.currentMovie = movieBean.findMovie(this.movieId);
            
           if (this.currentMovie == null) {
                System.out.println("ERROR: Movie with ID " + this.movieId + " not found.");
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Movie Not Found", 
                                     "The requested movie could not be located."));
            }
        }
    }

    public Long getMovieId() {
        return movieId;
    }

    public Movie getCurrentMovie() {
        return currentMovie;
    }
}
