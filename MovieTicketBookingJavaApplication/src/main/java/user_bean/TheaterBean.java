/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package user_bean;

import entity.Theater;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

/**
 *
 * @author DELL
 */
@Stateless
public class TheaterBean implements TheaterBeanLocal {
    @PersistenceContext(unitName = "myMovie")
    private EntityManager em;

    @Override
    public void createTheater(Theater theater) {
        em.persist(theater);
    }

    @Override
    public void editTheater(Theater theater) {
        em.merge(theater);
    }

    @Override
    public void removeTheater(Theater theater) {
        em.remove(em.merge(theater));
    }

    @Override
    public Theater find(Object id) {
        return em.find(Theater.class, id);
    }

    @Override
    public List<Theater> findallTheater() {
        return em.createNamedQuery("Theater.findAll", Theater.class).getResultList();
    }
    
    

   
}
