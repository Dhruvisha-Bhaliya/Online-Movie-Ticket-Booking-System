/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package AuthSignupBean;

import entity.RoleMaster;
import jakarta.ejb.Local;
import java.util.List;

/**
 *
 * @author DELL
 */
@Local
public interface AuthServiceLocal {

    List<RoleMaster> getAllRoles();

    void signUp(String username, String email, String password, Long roleId);

    String login(String email, String password);

    public boolean validateLogin(String username, String email, String password, String roleName);

    public RoleMaster findRoleById(Long roleId);
}
