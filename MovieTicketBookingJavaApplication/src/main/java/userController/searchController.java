/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package userController;

import entity.Movie;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import user_bean.MovieBeanLocal;

/**
 *
 * @author DELL
 */
@Named
@SessionScoped
public class searchController implements Serializable {

    @EJB
    private MovieBeanLocal movieBean;

    private String searchQuery;
    private List<Movie> filteredMovies;

    public void updateSuggestions() {
        filteredMovies = movieBean.searchMovie(searchQuery);
    }

 
    public void goToMovieDetail(Long movieId) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext()
                .redirect("MovieDetail.xhtml?movieId=" + movieId);
    }

    public String performSearch() throws IOException {
        if (filteredMovies != null && !filteredMovies.isEmpty()) {
           
            Long movieId = filteredMovies.get(0).getMovieId();
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect("MovieDetail.xhtml?movieId=" + movieId);
        }
        return null;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
         updateSuggestions(); 
        this.searchQuery = searchQuery;
    }

    public List<Movie> getFilteredMovies() {
        return filteredMovies;
    }

}
