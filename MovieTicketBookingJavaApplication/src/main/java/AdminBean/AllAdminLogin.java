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
    public List<RoleMaster> getAllRoles() {
        return em.createNamedQuery("RoleMaster.findAll").getResultList();
    }

    @Override
    public List<Admin> getAllAdmins() {
        return em.createNamedQuery("Admin.findAll").getResultList();
    }

    @Override
    public void createAdmin(String name, String email, String password, Long phone, Long roleId) {
        RoleMaster role = em.find(RoleMaster.class, roleId);

        Admin a = new Admin();
        a.setUsername(name);
        a.setEmail(email);
        a.setPassword(new PasswordUtil().hashPassword(password));
        a.setPhoneno(BigInteger.valueOf(phone));
        a.setRole(role);
        a.setStatus("active");
        a.setCreatedAt(new Date());
        a.setUpdatedAt(new Date());

        em.persist(a);
    }

    @Override
    public void deleteAdmin(Long id) {
        Admin a = em.find(Admin.class, id);
        em.remove(a);
    }

//    @Override
//    public void updateAdmin(Admin admin) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
//    }
}
