/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package AdminBean;

import entity.Admin;
import entity.RoleMaster;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import util.PasswordUtil;

/**
 *
 * @author DELL
 */
@Stateless
public class AllAdminLogin implements AllAdminLoginLocal {

    @PersistenceContext(unitName = "myMovie")
    private EntityManager em;

    @Override
    public Admin loginAdmin(String email, String password) {
        try {
            Admin admin = em.createQuery(
                    "SELECT a FROM Admin a WHERE a.email = :email AND a.status = 'active'",
                    Admin.class)
                    .setParameter("email", email)
                    .getSingleResult();

            PasswordUtil util = new PasswordUtil();
            if (util.verifyPassword(password, admin.getPassword())) {
                return admin;
            } else {
                return null;
            }

        } catch (Exception e) {
            System.out.println("Admin login error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<RoleMaster> getAllowedRoles(Long loggedRoleId) {

        if (loggedRoleId == 1) { // SUPER_ADMIN
            return em.createQuery(
                    "SELECT r FROM RoleMaster r WHERE r.roleId > 1",
                    RoleMaster.class).getResultList();
        }

        if (loggedRoleId == 2) { // ADMIN
            return em.createQuery(
                    "SELECT r FROM RoleMaster r WHERE r.roleId IN (3,4)",
                    RoleMaster.class).getResultList();
        }

        if (loggedRoleId == 3) { // MANAGER
            return em.createQuery(
                    "SELECT r FROM RoleMaster r WHERE r.roleId = 4",
                    RoleMaster.class).getResultList();
        }

        return List.of();
    }

    @Override
    public List<Admin> getAdminsByRole(Long loggedRoleId) {

        if (loggedRoleId == 1) {
            return em.createQuery("SELECT a FROM Admin a", Admin.class)
                    .getResultList();
        }

        if (loggedRoleId == 2) {
            return em.createQuery(
                    "SELECT a FROM Admin a WHERE a.role.roleId IN (3,4)",
                    Admin.class).getResultList();
        }

        if (loggedRoleId == 3) {
            return em.createQuery(
                    "SELECT a FROM Admin a WHERE a.role.roleId = 4",
                    Admin.class).getResultList();
        }

        return List.of();
    }

    @Override
    public boolean createAdmin(Admin creator, String name, String email,
            String password, Long phone, Long roleId) {

        if (!isAllowed(creator.getRole().getRoleId(), roleId)) {
            return false;
        }

        if (!isValidEmailForRole(email, roleId)) {
            return false;
        }

        RoleMaster role = em.find(RoleMaster.class, roleId);

        Admin a = new Admin();
        a.setUsername(name);
        a.setEmail(email);
        a.setPassword(PasswordUtil.hashPassword(password));
        a.setPhoneno(BigInteger.valueOf(phone));
        a.setRole(role);
        a.setStatus("active");
        a.setCreatedAt(new Date());
        a.setUpdatedAt(new Date());

        em.persist(a);
        return true;
    }

    private boolean isValidEmailForRole(String email, Long roleId) {

        email = email.toLowerCase();

        switch (roleId.intValue()) {
            case 1: // SUPER_ADMIN
                return email.endsWith("@cinemaxhub.com")
                        && email.contains("sadmin");

            case 2: // ADMIN
                return email.endsWith("@cinemaxhub.com")
                        && email.contains("admin");

            case 3: // MANAGER
                return email.endsWith("@cinemaxhub.com")
                        && email.contains("manager");

            case 4: // STAFF
                return email.endsWith("@cinemaxhub.com")
                        && email.contains("staff");

            default:
                return false;
        }
    }

    private boolean isAllowed(Long creatorRole, Long targetRole) {

        if (targetRole == null) {
            return false;
        }

        if (creatorRole == 1) {
            return targetRole > 1;
        }
        if (creatorRole == 2) {
            return targetRole == 3 || targetRole == 4;
        }
        if (creatorRole == 3) {
            return targetRole == 4;
        }

        return false;
    }

    @Override
    public List<RoleMaster> getAllRoles() {
        return em.createNamedQuery("RoleMaster.findAll").getResultList();
    }

    @Override
    public List<Admin> getAllAdmins() {
        return em.createNamedQuery("Admin.findAll").getResultList();
    }

//    @Override
//    public void createAdmin(String name, String email, String password, Long phone, Long roleId) {
//        RoleMaster role = em.find(RoleMaster.class, roleId);
//
//        Admin a = new Admin();
//        a.setUsername(name);
//        a.setEmail(email);
//        a.setPassword(new PasswordUtil().hashPassword(password));
//        a.setPhoneno(BigInteger.valueOf(phone));
//        a.setRole(role);
//        a.setStatus("active");
//        a.setCreatedAt(new Date());
//        a.setUpdatedAt(new Date());
//
//        em.persist(a);
//    }
    
    @Override
    public void updateRoleDescription(Long roleId, String description) {

        RoleMaster role = em.find(RoleMaster.class, roleId);

        if (role != null) {
            role.setDescription(description);
            em.merge(role);
        }
    }

    @Override
    public void deleteAdmin(Long id) {
        Admin a = em.find(Admin.class, id);
        if (a != null) {
            em.remove(a);
        }
    }

    @Override
    public Admin findAdminByEmail(String email) {
        try {
            return em.createQuery(
                    "SELECT a FROM Admin a WHERE a.email = :email",
                    Admin.class
            )
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void updateAdmin(Admin admin) {
        admin.setUpdatedAt(new Date());
        em.merge(admin);
    }
}
