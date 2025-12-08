/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package AdminBean;

import entity.RoleMaster;
import entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Date;
import util.PasswordUtil;

/**
 *
 * @author DELL
 */
@Stateless(name = "UserRegistrationLoginBean")
public class UserRegistrationLoginBean implements UserRegistrationLoginBeanLocal {

    @PersistenceContext(unitName = "myMovie")
    private EntityManager em;

    @Override
    public boolean register(String username, String email, String password, Long phone) {
        try {
            User u = new User();
            u.setUsername(username);
            u.setEmail(email);
            u.setPassword(password);
            u.setPhoneno(phone);
            u.setStatus("active");
            u.setCreatedAt(new Date());
            u.setUpdatedAt(new Date());

            // Corrected: use Long instead of int
            u.setRole(getRoleByEmail(email));

            em.persist(u);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Assign roles based on email
    private RoleMaster getRoleByEmail(String email) {
        email = email.toLowerCase();
        Long roleId = 5L; // default = customer

        if (email.equals("dhruvi.sadmin@cinemaxhub.com")) {
            roleId = 1L; // Super Admin
        } else if (email.equals("khushi.admin@cinemaxhub.com")) {
            roleId = 2L; // Admin
        } else if (email.contains("@cinemaxhub.com") && email.contains("manager")) {
            roleId = 3L; // Manager
        } else if (email.contains("@cinemaxhub.com") && email.contains("staff")) {
            roleId = 4L; // Staff
        }

        // Fetch RoleMaster entity from DB
        return em.find(RoleMaster.class, roleId);
    }

    @Override
    public User login(String email, String password) {

        User u = findByEmail(email);

        if (u == null) {
            return null;
        }

        PasswordUtil util = new PasswordUtil();
        if (!util.verifyPassword(password, u.getPassword())) {
            return null;
        }

        return u;
    }

    @Override
    public User findByEmail(String email) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.email = :e", User.class)
                    .setParameter("e", email)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean updatePassword(String email, String newPassword) {
        try {
            User u = em.createQuery("SELECT u FROM User u WHERE u.email = :e", User.class)
                    .setParameter("e", email)
                    .getSingleResult();

            if (u == null) {
                return false;
            }

            // Hash the new password using your existing PasswordUtil
            String hashed = util.PasswordUtil.hashPassword(newPassword);
            u.setPassword(hashed);

            u.setUpdatedAt(new Date());
            em.merge(u);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
