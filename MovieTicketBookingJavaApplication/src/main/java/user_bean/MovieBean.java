/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package user_bean;

import entity.Movie;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;



/**
 *
 * @author DELL
 */
@Stateless
public class MovieBean implements MovieBeanLocal {
    @PersistenceContext(unitName = "myMovie")  // change YourPU to your persistence-unit name
    private EntityManager em;

    @Override
    public void addMovie(Movie movie) {
        try {
            em.persist(movie);
        } catch (ConstraintViolationException e) {
            for(ConstraintViolation<?> v: e.getConstraintViolations()){
                System.out.println("Property: "+v.getPropertyPath());
                System.out.println("Ivalid value: "+v.getInvalidValue());
                System.out.println("Message: "+v.getMessage());
            }
            throw e;
        }
    }

    @Override
    public void updateMovie(Movie movie) {
         em.merge(movie);
    }

    @Override
    public void deleteMovie(Long movieId) {
        Movie m = em.find(Movie.class, movieId);
        if (m != null) {
            em.remove(m);
        }
    }

    @Override
    public Movie findMovie(Long movieId) {
        return em.find(Movie.class, movieId);
    }

    @Override
    public List<Movie> getAllMovies() {
        return em.createNamedQuery("Movie.findAll", Movie.class).getResultList();
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Override
    public List<Movie> searchMovie(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllMovies(); // If query is empty, return all movies
        }
        
        // Use JPQL for flexible searching: check if the title CONTAINS the query.
        // The LOWER() function and toLowerCase() in Java ensure case-insensitivity.
        return em.createQuery(
            "SELECT m FROM Movie m WHERE LOWER(m.movieTitle) LIKE :searchQuery", Movie.class)
            .setParameter("searchQuery", "%" + query.toLowerCase() + "%")
            .getResultList();
    }
    
    
}
