/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package AdminBean;

import entity.Admin;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author DELL
 */
@Named("adminBean")
@SessionScoped
public class AdminBean implements Serializable {

    private Admin admin = new Admin();
    private String email;
    private String password;

    @EJB
    private AdminServiceLocal adminService;

    public String signUp() {
        try {
            adminService.addadmin(admin);
            return "AdminLogin.xhtml?faces-redirect=true";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "AdminLogin.xhtml?faces-redirect=true";
    }

    public String login() {
        Admin found = adminService.findByEmailAndPassword(email, password);
        if (found != null) {
            admin = found;
            return "/admin/admin_dashBoard.xhtml?faces-redirect=true";
        } else {
            return "AdminLogin.xhtml?error=true";
        }
    }

    public List<Admin> getAllAdmins() {
        return adminService.getAllAdmins();
    }

    public void deleteAdmin(int admin_id) {
        adminService.deleteAdmin(admin_id);
    }

    public void updateAdmin() {
        adminService.updateAdmin(admin);
    }

    // Getters & Setters
    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
