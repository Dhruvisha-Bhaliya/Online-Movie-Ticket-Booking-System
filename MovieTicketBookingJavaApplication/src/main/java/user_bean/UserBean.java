/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package user_bean;

import entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 *
 * @author DELL
 */
@Stateless
public class UserBean implements UserBeanLocal {
    @PersistenceContext(unitName = "myMovie")
    private EntityManager em;

    @Override
    public User findUser(Object userId) {
        return em.find(User.class, userId);
    }

    @Override
    public void updateUser(User userId) {
        em.merge(userId);
    }

    
}
