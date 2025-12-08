/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package userController;

import entity.Movie;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import user_bean.MovieBeanLocal;

/**
 *
 * @author DELL
 */

@Named("moviePageController")
@SessionScoped
public class MoviePageController implements Serializable{

    @EJB
    private MovieBeanLocal movieBean;

    private List<Movie> allMovies;
    private List<Movie> filteredMovies; 

    // Filter properties
    private String selectedLanguage;
    private String selectedGenre;

    @PostConstruct
    public void init() {
        this.allMovies = movieBean.getAllMovies();
        this.filteredMovies = this.allMovies; 
    }

   
    public void applyFilters() {
       final String languageFilter = (selectedLanguage != null) ? selectedLanguage.trim().toLowerCase() : null;
        final String genreFilter = (selectedGenre != null) ? selectedGenre.trim().toLowerCase() : null;

        filteredMovies = allMovies.stream()
            
            
            .filter(m -> {
                if (languageFilter == null || languageFilter.isEmpty()) {
                    return true;
                }
                return m.getLanguages().toLowerCase().contains(languageFilter);
            })
            .filter(m -> {
                if (genreFilter == null || genreFilter.isEmpty()) {
                    return true;
                }
                
                String primaryGenre = (m.getGenre() != null) ? m.getGenre().toLowerCase() : "";
                String subGenres = (m.getSubGenres() != null) ? m.getSubGenres().toLowerCase() : "";
                return primaryGenre.contains(genreFilter) || subGenres.contains(genreFilter);
            })
            .collect(Collectors.toList());
    
    }

   
    public List<Movie> getFilteredMovies() {
        return filteredMovies;
    }

    public String getSelectedLanguage() {
        return selectedLanguage;
    }

    public void setSelectedLanguage(String selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }

    public String getSelectedGenre() {
        return selectedGenre;
    }

    public void setSelectedGenre(String selectedGenre) {
        this.selectedGenre = selectedGenre;
    }

 
    public List<String> getUniqueLanguages() {
       if (allMovies == null) return List.of();
        return allMovies.stream()
            .flatMap(m -> List.of(m.getLanguages().split(",")).stream()) 
            .map(String::trim)
            .distinct()
            .collect(Collectors.toList());
    }

    public List<String> getUniqueGenres() {
        if (allMovies == null) return List.of();
        return allMovies.stream()
            .map(Movie::getGenre)
            .distinct()
            .collect(Collectors.toList());
    }
}


