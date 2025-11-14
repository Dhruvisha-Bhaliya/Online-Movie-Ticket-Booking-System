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

@Named("moviePageController") // ⭐ ADD THIS: Define the name used in XHTML
@SessionScoped
public class MoviePageController implements Serializable{

    @EJB
    private MovieBeanLocal movieBean;

    private List<Movie> allMovies;
    private List<Movie> filteredMovies; // This list will be used on the page

    // Filter properties
    private String selectedLanguage;
    private String selectedGenre;

    @PostConstruct
    public void init() {
        // Fetch all movies when the bean is created
        this.allMovies = movieBean.getAllMovies();
        this.filteredMovies = this.allMovies; // Initially, filtered list is all movies
    }

    // New Filter Action
    public void applyFilters() {
       final String languageFilter = (selectedLanguage != null) ? selectedLanguage.trim().toLowerCase() : null;
        final String genreFilter = (selectedGenre != null) ? selectedGenre.trim().toLowerCase() : null;

        filteredMovies = allMovies.stream()
            
            // 1. Language Filter (AND logic)
            .filter(m -> {
                if (languageFilter == null || languageFilter.isEmpty()) {
                    return true;
                }
                // Check if the movie's languages string contains the filter string
                return m.getLanguages().toLowerCase().contains(languageFilter);
            })
            
            // 2. Genre Filter (AND logic)
            .filter(m -> {
                if (genreFilter == null || genreFilter.isEmpty()) {
                    return true;
                }
                
                // Get both genre fields and check if EITHER contains the filter
                String primaryGenre = (m.getGenre() != null) ? m.getGenre().toLowerCase() : "";
                String subGenres = (m.getSubGenres() != null) ? m.getSubGenres().toLowerCase() : "";
                
                // Check if the primary genre or the sub-genres field contains the selected filter.
                return primaryGenre.contains(genreFilter) || subGenres.contains(genreFilter);
            })
            .collect(Collectors.toList());
    
    }

    // --- Getters and Setters ---
    public List<Movie> getFilteredMovies() {
        return filteredMovies;
    }

    // Getter and Setter for language filter
    public String getSelectedLanguage() {
        return selectedLanguage;
    }

    public void setSelectedLanguage(String selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }

    // Getter and Setter for genre filter
    public String getSelectedGenre() {
        return selectedGenre;
    }

    public void setSelectedGenre(String selectedGenre) {
        this.selectedGenre = selectedGenre;
    }

    // Helper to get unique languages/genres for the filter UI (implementation omitted for brevity)
    public List<String> getUniqueLanguages() {
       if (allMovies == null) return List.of();
        // Extract all language strings, split them, trim, and get distinct values.
        return allMovies.stream()
            .flatMap(m -> List.of(m.getLanguages().split(",")).stream()) // Assuming languages are comma-separated
            .map(String::trim)
            .distinct()
            .collect(Collectors.toList());
    }

    public List<String> getUniqueGenres() {
        if (allMovies == null) return List.of();
        return allMovies.stream()
            .map(Movie::getGenre) // Assuming Genre is a single string field
            .distinct()
            .collect(Collectors.toList());
    }
}


