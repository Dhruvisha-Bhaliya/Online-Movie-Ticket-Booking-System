/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package user_bean;

import entity.Screen;
import entity.Seat;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Collections;
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
    public List<Screen> findAll() {
        System.out.println("DEBUG: Attempting to fetch all screens...");
        try {
            // Ensure the NamedQuery "Screen.findAll" is correctly defined in the Screen entity.
            TypedQuery<Screen> query = em.createNamedQuery("Screen.findAll", Screen.class);
            List<Screen> results = query.getResultList();
            System.out.println("DEBUG: Successfully fetched " + results.size() + " screens.");
            return results;
        } catch (Exception e) {
            // Log the exception to the console to see why the list is empty.
            System.err.println("ERROR: Failed to fetch screens from database in ScreenEJB.findAll().");
            e.printStackTrace();
            // Return an empty list to prevent a complete application crash.
            return Collections.emptyList();
        }
    }

}
