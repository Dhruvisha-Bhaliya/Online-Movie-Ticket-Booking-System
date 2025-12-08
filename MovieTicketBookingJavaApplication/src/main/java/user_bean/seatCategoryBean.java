/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package user_bean;

import entity.Screen;
import entity.Seat;
import entity.SeatCategory;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author HP
 */
@Stateless
public class seatCategoryBean implements seatCategoryBeanLocal {

    @PersistenceContext(unitName = "myMovie")
    private EntityManager em;

    @Override
    public SeatCategory findById(Long id) {
        return em.find(SeatCategory.class, id);
    }

    @Override
    public List<SeatCategory> findAll() {
        return em.createNamedQuery("SeatCategory.findAll", SeatCategory.class).getResultList();
    }

    @Override
    public SeatCategory save(SeatCategory category) {
        if (category.getCategoryId() == null) {
            em.persist(category);
            return category;
        } else {
          
            return em.merge(category);
        }
    }

    @Override
    public void delete(SeatCategory category) {
        em.remove(em.merge(category));
    }

    @Override
    public List<SeatCategory> findByScreenId(Long screenId) {
        return em.createQuery(
                "SELECT s FROM SeatCategory s WHERE s.screenId.screenId = :screenId",
                SeatCategory.class
        )
                .setParameter("screenId", screenId)
                .getResultList();
    }
    
   

}
