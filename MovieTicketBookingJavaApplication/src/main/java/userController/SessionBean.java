/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package userController;

import entity.User;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;

/**
 *
 * @author HP
 */
@Named("sessionBean")
@SessionScoped
public class SessionBean implements Serializable{
    private static final long serialVersionUID = 1L;
    private User loggedInUser;

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public boolean isLoggedIn() {
        // Check if the user object exists
        return this.loggedInUser != null;
    }
    
    // This is the function you should call upon successful login
    // Assume you pass the User object from the EJB/Service layer
    public String doLogin(User user) {
        this.loggedInUser = user;
        // Optionally, redirect to homepage or desired view
        return "/index.xhtml?faces-redirect=true"; 
    }

    // 🎯 The Logout Method
    public String doLogout() {
        // 1. Invalidate HTTP Session
        FacesContext.getCurrentInstance().getExternalContext()
            .invalidateSession();
        
        // 2. Clear local bean state
        this.loggedInUser = null; 

        // 3. Redirect to the homepage/login page
        return "/index.xhtml?faces-redirect=true";
    }
}
