/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package user_bean;

import entity.Screen;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

/**
 *
 * @author DELL
 */
@Stateless
public class ScreenBean implements ScreenBeanLocal {

    @PersistenceContext(unitName = "myMovie")
    private EntityManager em;

    @Override
    public void createScreen(Screen screen) {
        em.persist(screen);
    }

    @Override
    public void editScreen(Screen screen) {
        em.merge(screen);
    }

    @Override
    public void removeScreen(Screen screen) {
        em.remove(em.merge(screen));
    }

    @Override
    public Screen findScreen(Object ScreenId) {
        return em.find(Screen.class, ScreenId);
    }

    @Override
    public List<Screen> findAllScreen() {
        return em.createQuery("SELECT s FROM Screen s", Screen.class).getResultList();
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
