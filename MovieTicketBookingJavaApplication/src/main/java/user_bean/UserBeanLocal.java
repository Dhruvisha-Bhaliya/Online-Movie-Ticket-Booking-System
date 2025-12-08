/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package user_bean;

import entity.User;
import jakarta.ejb.Local;

/**
 *
 * @author DELL
 */
@Local
public interface UserBeanLocal {
    User findUser(Object userId);
    void updateUser(User userId);
}
