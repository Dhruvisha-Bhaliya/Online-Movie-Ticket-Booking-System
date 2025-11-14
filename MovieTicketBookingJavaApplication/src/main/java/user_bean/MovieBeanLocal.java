/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package user_bean;

import entity.Movie;
import jakarta.ejb.Local;
import java.util.List;

/**
 *
 * @author DELL
 */
@Local
public interface MovieBeanLocal {
    void addMovie(Movie movie);
    void updateMovie(Movie movie);
    void deleteMovie(Long movieId);
    Movie findMovie(Long movieId);
    List<Movie> getAllMovies();
    
    List<Movie> searchMovie(String query);
}
