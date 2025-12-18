package cdi;

import AdminBean.AllAdminLoginLocal;
import entity.Admin;
import entity.RoleMaster;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("adminRoleUserBean")
@ViewScoped
public class AdminRoleUserBean implements Serializable {

    @EJB
    private AllAdminLoginLocal adminService;

    private Admin loggedAdmin;
    private List<Admin> admins;
    private List<RoleMaster> roles;
    private Long editRoleId;
    private String oldDescription;

    private String name;
    private String email;
    private String password;
    private Long phone;
    private Long roleId;

    @PostConstruct
    public void init() {
        loggedAdmin = (Admin) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("admin");

        if (loggedAdmin != null) {
            admins = adminService.getAdminsByRole(
                    loggedAdmin.getRole().getRoleId());

            roles = adminService.getAllRoles();
        }
    }

    public void addUser() {

        FacesContext fc = FacesContext.getCurrentInstance();

        boolean created = adminService.createAdmin(
                loggedAdmin,
                name,
                email,
                password,
                phone,
                roleId
        );

        if (!created) {
            fc.addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Invalid Email",
                            "Email does not match selected role"
                    ));

            fc.getExternalContext()
                    .getRequestMap()
                    .put("showError", true);
            return;
        }

        admins = adminService.getAdminsByRole(
                loggedAdmin.getRole().getRoleId()
        );

        clear();

        fc.getExternalContext()
                .getRequestMap()
                .put("showSuccess", true);
    }

    public void deleteUser(Long id) {
        adminService.deleteAdmin(id);
        admins = adminService.getAdminsByRole(
                loggedAdmin.getRole().getRoleId());

        FacesContext.getCurrentInstance()
                .addMessage(null,
                        new FacesMessage("User deleted successfully"));
    }

    private void clear() {
        name = email = password = null;
        phone = roleId = null;
    }

    public void startEdit(RoleMaster role) {
        editRoleId = role.getRoleId();
        oldDescription = role.getDescription();
    }

    public void saveRole(RoleMaster role) {
        adminService.updateRoleDescription(
                role.getRoleId(),
                role.getDescription()
        );
        editRoleId = null;
    }

    public void cancelEdit() {
        for (RoleMaster r : roles) {
            if (r.getRoleId().equals(editRoleId)) {
                r.setDescription(oldDescription);
                break;
            }
        }
        editRoleId = null;
    }

    public String logout() {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();

        ec.invalidateSession();

        return "/login.xhtml?faces-redirect=true&logout=true";
    }

    public List<RoleMaster> getRoles() {
        return roles;
    }

    public Long getEditRoleId() {
        return editRoleId;
    }

    /* Getters & Setters */
    public List<Admin> getAdmins() {
        return admins;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
