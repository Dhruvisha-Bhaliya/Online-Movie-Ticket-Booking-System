/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package AdminBean;

import entity.Admin;
import entity.RoleMaster;
import jakarta.ejb.Local;
import java.util.List;

/**
 *
 * @author DELL
 */
@Local
public interface AllAdminLoginLocal {

    Admin loginAdmin(String email, String password);
    // ROLE OPERATIONS

    List<RoleMaster> getAllRoles();

    // ADMIN OPERATIONS
    List<Admin> getAllAdmins();

    void createAdmin(String name, String email, String password, Long phone, Long roleId);

    void deleteAdmin(Long id);

    // You can add update logic if required
//    void updateAdmin(Admin admin);

}
