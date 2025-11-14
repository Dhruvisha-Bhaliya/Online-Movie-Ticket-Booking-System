/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package user_bean;

import entity.Showmovie;
import jakarta.ejb.Local;
import java.util.Date;
import java.util.List;

/**
 *
 * @author DELL
 */
@Local
public interface ShowBeanLocal {

    void createShow(Showmovie show);
    void editShow(Showmovie show);
    void removeShow(Showmovie show);
    Showmovie find(Object Showid);
    List<Showmovie> findAllShows();
    
    List<Showmovie> findShowByMovieId(Long movieId);
    List<Date> findAvailableDates(Long movieId);
    
    List<Showmovie> findShowsByMovieDateAndLanguage(Long movieId, Date date, String language);
    
}
