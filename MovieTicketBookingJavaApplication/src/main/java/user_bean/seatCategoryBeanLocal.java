/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package user_bean;

import entity.SeatCategory;
import jakarta.ejb.Local;
import java.util.List;

/**
 *
 * @author HP
 */
@Local
public interface seatCategoryBeanLocal {
    SeatCategory findById(Long id);
    List<SeatCategory> findAll();
    SeatCategory save(SeatCategory category);
    void delete(SeatCategory category);
    List<SeatCategory> findByScreenId(Long screenId);
    
}
