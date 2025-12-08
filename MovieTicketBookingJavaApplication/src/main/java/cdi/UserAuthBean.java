/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package cdi;

import AdminBean.AllAdminLoginLocal;
import AdminBean.UserRegistrationLoginBeanLocal;
import entity.Admin;
import entity.User;
import jakarta.ejb.EJB;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;
import util.PasswordUtil;

/**
 *
 * @author DELL
 */
@Named(value = "userAuthBean")
@SessionScoped
public class UserAuthBean implements Serializable {

    public UserAuthBean() {
    }
    @EJB
    private UserRegistrationLoginBeanLocal userService;

    @EJB
    private AllAdminLoginLocal adminService;

    private String username;
    private String email;
    private String password;
    private Long phoneno;
    private String newPassword;
    private String confirmPassword;
    private Admin loggedAdmin;
    private User loggedUser;

    public String register() {

        String hashedPass = PasswordUtil.hashPassword(password);
        boolean ok = userService.register(username, email, hashedPass, phoneno);

        if (ok) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Registration Successful!"));
            return "login.xhtml?faces-redirect=true";
        }

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Registration failed"));
        return null;
    }

    public String login() {
        Admin admin = adminService.loginAdmin(email, password);
        if (admin != null) {
            loggedAdmin = admin;

            FacesContext.getCurrentInstance().getExternalContext()
                    .getSessionMap().put("admin", admin);

            int role = admin.getRole().getRoleId().intValue();

            switch (role) {
                case 1:
                    return "/admin/Admin_panel.xhtml?faces-redirect=true";
                case 2:
                    return "/admin/dashboard.xhtml?faces-redirect=true";
                case 3:
                    return "/manager/dashboard.xhtml?faces-redirect=true";
                case 4:
                    return "/staff/dashboard.xhtml?faces-redirect=true";
            }
        }
        User user = userService.login(email, password);

        if (user != null) {
            loggedUser = user;
            FacesContext.getCurrentInstance().getExternalContext()
                    .getSessionMap().put("user", user);

            return "/index.xhtml?faces-redirect=true";
        }
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Invalid email or password", null));
        return null;
    }

    public String changePassword() {
        if (newPassword == null || confirmPassword == null || !newPassword.equals(confirmPassword)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Passwords do not match!", null));
            return null;
        }

        boolean ok = userService.updatePassword(email, newPassword);

        if (ok) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Password updated successfully!"));
            return "login.xhtml?faces-redirect=true";
        }

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email not found!", null));
        return null;
    }

    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().put("logoutSuccess", true);
        context.getExternalContext().invalidateSession();
        return "index.xhtml?faces-redirect=true";
    }

    public UserRegistrationLoginBeanLocal getUserService() {
        return userService;
    }

    public void setUserService(UserRegistrationLoginBeanLocal userService) {
        this.userService = userService;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Long getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(Long phoneno) {
        this.phoneno = phoneno;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

}
