/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package AuthSignupBean;

import entity.Admin;
import entity.RoleMaster;
import entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

/**
 *
 * @author DELL
 */
@Stateless
public class AuthService implements AuthServiceLocal {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<RoleMaster> getAllRoles() {
        System.out.println("Fetching roles from database...");

        List<RoleMaster> list = em.createQuery("SELECT r FROM RoleMaster r", RoleMaster.class)
                .getResultList();
        for (RoleMaster r : list) {
            System.out.println("Fetched role: " + r.getRole());
        }
        System.out.println("Total roles fetched: " + list.size());

        return list;
    }

    @Override
    public String login(String email, String password) {
        List<Admin> admins = em.createQuery(
                "SELECT a FROM Admin a WHERE a.email=:email AND a.password=:pwd", Admin.class
        )
                .setParameter("email", email)
                .setParameter("pwd", password)
                .getResultList();
        if (!admins.isEmpty()) {
            return "admin_Dashboard.xhtml?faces-redirect=true";
        }

        List<User> users = em.createQuery(
                "SELECT u FROM User u WHERE u.email=:email AND u.password=:pwd", User.class
        )
                .setParameter("email", email)
                .setParameter("pwd", password)
                .getResultList();
        if (!users.isEmpty()) {
            return "index.xhtml?faces-redirect=true";
        }

        return null;
    }

    @Override
    public void signUp(String username, String email, String password, Long roleId) {
        RoleMaster role = em.find(RoleMaster.class,
                roleId);
        if (role == null) {
            return;
        }

        if (role.getRole().equalsIgnoreCase("Customer")) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setRole(role);
            em.persist(user);
        } else {
            Admin admin = new Admin();
            admin.setUsername(username);
            admin.setEmail(email);
            admin.setPassword(password);
            admin.setRole(role);
            em.persist(admin);
        }
    }

    @Override
    public RoleMaster findRoleById(Long roleId) {
        return em.find(RoleMaster.class, roleId);
    }

    @Override
    public boolean validateLogin(String username, String email, String password, String roleName) {
        try {
            Long count;
            if (roleName.equalsIgnoreCase("admin")) {
                count = em.createQuery(
                        "SELECT COUNT(a) FROM Admin a WHERE a.username = :u AND a.email = :e AND a.password = :p AND a.role.role = :r",
                        Long.class)
                        .setParameter("u", username)
                        .setParameter("e", email)
                        .setParameter("p", password)
                        .setParameter("r", "Admin")
                        .getSingleResult();
            } else {
                count = em.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.username = :u AND u.email = :e AND u.password = :p AND u.role.role = :r",
                        Long.class)
                        .setParameter("u", username)
                        .setParameter("e", email)
                        .setParameter("p", password)
                        .setParameter("r", "Customer")
                        .getSingleResult();
            }
            return count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
