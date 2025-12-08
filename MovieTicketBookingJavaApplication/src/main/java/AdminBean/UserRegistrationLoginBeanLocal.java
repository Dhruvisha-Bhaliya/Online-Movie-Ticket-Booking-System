/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package AdminBean;

import entity.User;
import jakarta.ejb.Local;

/**
 *
 * @author DELL
 */
@Local
public interface UserRegistrationLoginBeanLocal {

    boolean register(String username, String email, String password, Long phone);

    User login(String email, String password);

    User findByEmail(String email);

    boolean updatePassword(String email, String newPassword);
}
