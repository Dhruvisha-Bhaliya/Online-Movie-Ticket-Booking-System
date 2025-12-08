/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package user_bean;

import entity.Showmovie;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author DELL
 */
@Stateless
public class ShowBean implements ShowBeanLocal {

    @PersistenceContext(unitName = "myMovie")
    private EntityManager em;

    @Override
    public void createShow(Showmovie show) {
        em.persist(show);
    }

    @Override
    public void editShow(Showmovie show) {
        em.merge(show);
    }

    @Override
    public void removeShow(Showmovie show) {
        em.remove(em.merge(show));
    }

    @Override
    public Showmovie find(Object Showid) {
        return em.find(Showmovie.class, Showid);
    }

    @Override
    public List<Showmovie> findAllShows() {
        return em.createNamedQuery("Showmovie.findAll",Showmovie.class).getResultList();
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Override
    public List<Showmovie> findShowByMovieId(Long movieId) {
       
       List<Showmovie> shows = em.createQuery(
            /*"SELECT s FROM Showmovie s WHERE s.movieId.movieId = :movieId AND s.showTime >= :currentDate ORDER BY s.showTime", Showmovie.class)
            .setParameter("movieId", movieId)
            .setParameter("currentDate", new Date())
            .getResultList();*/
               
            /*"SELECT s FROM Showmovie s WHERE s.movieId.movieId = :movieId AND s.showTime >= :now ORDER BY s.showTime", Showmovie.class)
            .setParameter("movieId", movieId)
            .setParameter("now", new Date()) 
            .getResultList();*/
               
            /*"SELECT s FROM Showmovie s WHERE s.movieId.movieId = :movieId ORDER BY s.showTime", Showmovie.class)
            .setParameter("movieId", movieId)
            .getResultList();*/
               
            "SELECT s FROM Showmovie s " +
            "JOIN FETCH s.movieId m " +
            "JOIN FETCH s.screenId scr " + // Fetch the Screen entity
            "JOIN FETCH scr.theaterId t " + // Fetch the Theater entity
            "WHERE s.movieId.movieId = :movieId ORDER BY s.showTime", entity.Showmovie.class)
            .setParameter("movieId", movieId)
            .getResultList();
               
        
        for (Showmovie show : shows) {
            System.out.println("-> Show Time: " + show.getShowTime());
        }

        return shows;
              

    }

    @Override
    public List<Date> findAvailableDates(Long movieId) {
        return em.createQuery(
                "SELECT DISTINCT FUNCTION('DATE', s.showTime) FROM Showmovie s "
                + "WHERE s.movieId.movieId = :movieId "
                + "ORDER BY FUNCTION('DATE', s.showTime)", Date.class)
                .setParameter("movieId", movieId)
                .getResultList();
        
    }

    @Override
    public List<Showmovie> findShowsByMovieDateAndLanguage(Long movieId, Date date, String language) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Start of the day (00:00:00)
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startOfDay = calendar.getTime();

        // End of the day (23:59:59)
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endOfDay = calendar.getTime();

        List<Showmovie> shows =  em.createQuery(
                "SELECT s FROM Showmovie s "
                + "JOIN FETCH s.movieId m "
                + "JOIN FETCH s.screenId scr "
                + "JOIN FETCH scr.theaterId t "
                + "WHERE s.movieId.movieId = :movieId "
                + "  AND s.movieId.languages LIKE :languageFilter "
                + // Filter by language (e.g., Hindi)
                "  AND s.showTime BETWEEN :startOfDay AND :endOfDay "
                + // Filter by selected date
                "ORDER BY s.showTime", entity.Showmovie.class)
                .setParameter("movieId", movieId)
                .setParameter("languageFilter", "%" + language + "%") // Assuming languages is a string like "English, Hindi, Telugu"
                .setParameter("startOfDay", startOfDay)
                .setParameter("endOfDay", endOfDay)
                .getResultList();
        
        for (Showmovie s : shows) {
            System.out.println("Filtered Show: " + s.getShowId() + " | " + s.getShowTime());
        }

        return shows;

    }

 
    
}
