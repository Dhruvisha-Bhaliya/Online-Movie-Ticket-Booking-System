/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package AdminBean;

import entity.User;
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
public class UserService implements UserServiceLocal {

    @PersistenceContext(unitName = "myMovie")
    private EntityManager em;

    // CREATE
    @Override
    public void addUser(User user) {
        em.persist(user);
    }

    // READ
    @Override
    public User findByEmail(String email) {
        try {
            TypedQuery<User> q = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
            q.setParameter("email", email);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public User findById(Long id) {
        return em.find(User.class, id);
    }

    @Override
    public List<User> findAll() {
        TypedQuery<User> q = em.createQuery("SELECT u FROM User u", User.class);
        return q.getResultList();
    }

    // UPDATE
    @Override
    public User updateUser(User user) {
        return em.merge(user); // Return merged entity
    }

    // DELETE
    @Override
    public void deleteUser(User user) {
        User managed = em.find(User.class, user.getUserId());
        if (managed != null) {
            em.remove(managed);
            em.flush();
        }
    }
}
