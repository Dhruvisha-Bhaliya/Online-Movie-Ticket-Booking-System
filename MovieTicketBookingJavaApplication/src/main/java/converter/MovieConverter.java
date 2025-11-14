/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package converter;

import entity.Movie;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import user_bean.MovieBeanLocal;

/**
 *
 * @author DELL
 */
@jakarta.inject.Named("movieConverter")
@RequestScoped
public class MovieConverter implements Converter<Movie>{
    @EJB
    // Ensure you have this MovieBeanLocal interface and implementation
    private MovieBeanLocal movieBean; 

    @Override
    public Movie getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            Long id = Long.parseLong(value);
            return movieBean.findMovie(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value for Movie ID: " + value, e);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Movie movie) {
        if (movie == null) return "";
        return movie.getMovieId().toString(); 
    }
}
