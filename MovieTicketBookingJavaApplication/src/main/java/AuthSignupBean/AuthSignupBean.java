/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package AuthSignupBean;

import entity.RoleMaster;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author DELL
 */
@Named("authSignupBean")
@SessionScoped
public class AuthSignupBean implements Serializable {

    private String username;
    private String email;
    private String password;
    private Long selectedRole;
    private List<RoleMaster> roleList;
    private Long selectedRoleId;
    private String message;

    @EJB
    private AuthServiceLocal authService;

    public List<RoleMaster> getRoleList() {
        if (roleList == null) {
            roleList = authService.getAllRoles();
        }
        return roleList;
    }

    public String signUp() {
        if (selectedRole == null) {
            return null;
        }

        authService.signUp(username, email, password, selectedRole);
        RoleMaster role = roleList.stream()
                .filter(r -> r.getRoleId().equals(selectedRole))
                .findFirst()
                .orElse(null);

        if (role != null && role.getRole().equalsIgnoreCase("Customer")) {
            return "index.xhtml?faces-redirect=true";
        } else {
            return "/admin/admin_dashboard.xhtml?faces-redirect=true";
        }
    }

    public String login() {
        RoleMaster role = authService.findRoleById(selectedRoleId);
        if (role == null) {
            message = "Invalid role selected.";
            return null;
        }

        String roleName = role.getRole().toLowerCase();
        String expectedEmailPattern = username + "@" + roleName + ".com";
        if (!roleName.equals("customer") && !roleName.equals("user")) {
            if (!email.equalsIgnoreCase(expectedEmailPattern)) {
                message = "Invalid email format for role " + roleName
                        + ". Expected: " + expectedEmailPattern;
                return null;
            }
        }
        boolean valid = authService.validateLogin(username, email, password, roleName);
        if (valid) {
            message = "Login successful as " + roleName;
            return "index.xhtml.xhtml?faces-redirect=true";
        } else {
            message = "Invalid credentials!";
            return null;
        }
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

    public Long getSelectedRole() {
        return selectedRole;
    }

    public void setSelectedRole(Long selectedRole) {
        this.selectedRole = selectedRole;
    }

    public String getMessage() {
        return message;
    }

    public Long getSelectedRoleId() {
        return selectedRoleId;
    }

    public void setSelectedRoleId(Long selectedRoleId) {
        this.selectedRoleId = selectedRoleId;
    }

}
