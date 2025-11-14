/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package AdminBean;

import entity.Admin;
import jakarta.ejb.Local;
import java.util.List;

/**
 *
 * @author DELL
 */
@Local
public interface AdminServiceLocal {
    void addadmin(Admin admin);
    Admin findByEmailAndPassword(String email,String password);
    List<Admin> getAllAdmins();
    void deleteAdmin(int admin_id);
    void updateAdmin(Admin admin);
}
