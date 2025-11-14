/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package user_bean;

import entity.Theater;
import jakarta.ejb.Local;
import java.util.List;

/**
 *
 * @author DELL
 */
@Local
public interface TheaterBeanLocal {
    
    void createTheater(Theater theater);
    void editTheater(Theater theater);
    void removeTheater(Theater theater);
    Theater find(Object id);
    List<Theater> findallTheater();

    
}
