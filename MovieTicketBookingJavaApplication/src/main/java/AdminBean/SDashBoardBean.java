package AdminBean;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import entity.Admin;

@Named("sDashBoardBean")
@RequestScoped
public class SDashBoardBean implements Serializable {

    @PersistenceContext(unitName = "myMovie")
    private EntityManager em;

    private long totalAdmins;
    private long totalMovies;
    private long totalTheaters;
    private long totalScreens;

    private List<Admin> recentAdmins;

    private List<String> recentActivities;

    @PostConstruct
    public void init() {
        loadCounts();
        loadRecentAdmins();
        loadRecentActivities();
    }

    private void loadCounts() {
        totalAdmins = ((Number) em.createQuery("SELECT COUNT(a) FROM Admin a").getSingleResult()).longValue();
        totalMovies = ((Number) em.createQuery("SELECT COUNT(m) FROM Movie m").getSingleResult()).longValue();
        totalTheaters = ((Number) em.createQuery("SELECT COUNT(t) FROM Theater t").getSingleResult()).longValue();
        totalScreens = ((Number) em.createQuery("SELECT COUNT(s) FROM Screen s").getSingleResult()).longValue();
    }

    private void loadRecentAdmins() {
        recentAdmins = em.createQuery(
                "SELECT a FROM Admin a ORDER BY a.adminId DESC",
                Admin.class
        ).setMaxResults(5)
                .getResultList();
    }

    private void loadRecentActivities() {
        recentActivities = new ArrayList<>();
        recentActivities.add("Admin John added a new movie 'Avengers: Endgame'");
        recentActivities.add("Admin Lisa updated Theater #5");
        recentActivities.add("Admin Mike added Screen #3 in Theater #2");
        recentActivities.add("Admin Sarah deleted Movie 'Titanic'");
        recentActivities.add("Admin John added a new admin 'Robert'");
    }

    public long getTotalAdmins() {
        return totalAdmins;
    }

    public long getTotalMovies() {
        return totalMovies;
    }

    public long getTotalTheaters() {
        return totalTheaters;
    }

    public long getTotalScreens() {
        return totalScreens;
    }

    public List<Admin> getRecentAdmins() {
        return recentAdmins;
    }

    public List<String> getRecentActivities() {
        return recentActivities;
    }
}
