/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package AdminBean;

import entity.Admin;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 *
 * @author DELL
 */
@Stateless
public class AdminService implements AdminServiceLocal {

   @PersistenceContext(unitName = "myMovie")
    private EntityManager em;

    @Override
    public void addadmin(Admin admin) {
        em.persist(admin);
    }

    @Override
    public Admin findByEmailAndPassword(String email, String password) {
        try {
            TypedQuery<Admin> query = em.createQuery("SELECT a FROM Admin a WHERE a.email = :email AND a.password = :password", Admin.class);
            query.setParameter("email", email);
            query.setParameter("password", password);
            return query.getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Admin> getAllAdmins() {
        return em.createQuery("SELECT a FROM Admin a", Admin.class).getResultList();
    }

    @Override
    public void deleteAdmin(int admin_id) {
        Admin admin = em.find(Admin.class, admin_id);
        if (admin != null) {
            em.remove(admin);
        }
    }

    @Override
    public void updateAdmin(Admin admin) {
        em.merge(admin);
    }
}
