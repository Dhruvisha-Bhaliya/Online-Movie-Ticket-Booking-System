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

    List<RoleMaster> getAllRoles();

    List<Admin> getAllAdmins();

    List<RoleMaster> getAllowedRoles(Long loggedRoleId);

    List<Admin> getAdminsByRole(Long loggedRoleId);

    boolean createAdmin(Admin creator,
            String name,
            String email,
            String password,
            Long phone,
            Long roleId);

    void deleteAdmin(Long id);

    Admin findAdminByEmail(String email);

    void updateAdmin(Admin admin);

    void updateRoleDescription(Long roleId, String description);

}
