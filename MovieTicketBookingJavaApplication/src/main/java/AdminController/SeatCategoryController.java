/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AdminController;

import entity.Screen;
import entity.SeatCategory;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import user_bean.ScreenBeanLocal;
import user_bean.seatCategoryBeanLocal;

/**
 *
 * @author HP
 */
@Named("seatCategoryController")
@SessionScoped
public class SeatCategoryController implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private seatCategoryBeanLocal seatCategoryEJB;

    @EJB
    private ScreenBeanLocal screenbean; // To fetch screen list for the dropdown

    private SeatCategory newCategory;
    private SeatCategory selectedCategory;
    private List<SeatCategory> categoryList;
    private List<Screen> screenList; // List of all screens for the dropdown

    @PostConstruct
    public void init() {
        newCategory = new SeatCategory();
        // Set default status
        newCategory.setStatus("active");

        loadCategories();
        loadScreens();
    }

    // --- Data Loading Methods ---
    private void loadCategories() {
        categoryList = seatCategoryEJB.findAll();
    }

    private void loadScreens() {
        try {
            screenList = screenbean.findAllScreen();
            // Diagnostic check: System.out.println("Fetched " + screenList.size() + " screens.");
        } catch (Exception e) {
            System.err.println("Error fetching screen list: " + e.getMessage());
            // Add a FacesMessage to inform the user about the failure
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Screen Error", "Failed to load screens. Check server logs."));
        }
    }

    // --- CRUD Operations ---
    public void createCategory() {
        try {
            // Basic validation
            if (newCategory.getScreenId() == null || newCategory.getCategoryName().isEmpty() || newCategory.getPrice().compareTo(BigInteger.ZERO) <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please fill all required fields correctly."));
                return;
            }
            // Ensure start row is lexicographically before or equal to end row
            if (newCategory.getRowStart().compareTo(newCategory.getRowEnd()) > 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Start Row must be before or equal to End Row."));
                return;
            }

            seatCategoryEJB.save(newCategory);
            loadCategories(); // Refresh list

            // Reset for next creation
            newCategory = new SeatCategory();
            newCategory.setStatus("active");

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Seat Category created successfully."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to create category: " + e.getMessage()));
        }
    }

    public void updateCategory() {
        try {
            if (selectedCategory == null || selectedCategory.getCategoryId() == null) {
                return;
            }

            // Ensure start row is lexicographically before or equal to end row
            if (selectedCategory.getRowStart().compareTo(selectedCategory.getRowEnd()) > 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Start Row must be before or equal to End Row."));
                return;
            }

            seatCategoryEJB.save(selectedCategory);
            loadCategories(); // Refresh list

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Seat Category updated successfully."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to update category: " + e.getMessage()));
        }
    }

    public void deleteCategory() {
        try {
            if (selectedCategory == null || selectedCategory.getCategoryId() == null) {
                return;
            }

            seatCategoryEJB.delete(selectedCategory);
            loadCategories(); // Refresh list
            selectedCategory = null; // Clear selection

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Seat Category deleted successfully."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete category: " + e.getMessage()));
        }
    }

    // Used to set the selected category when the user clicks 'Edit'
    public void selectCategory(SeatCategory category) {
        this.selectedCategory = category;
    }

    // --- Getters and Setters ---
    public SeatCategory getNewCategory() {
        return newCategory;
    }

    public void setNewCategory(SeatCategory newCategory) {
        this.newCategory = newCategory;
    }

    public SeatCategory getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(SeatCategory selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public List<SeatCategory> getCategoryList() {
        return categoryList;
    }

    public List<Screen> getScreenList() {
        return screenList;
    }

    // You'll need to implement the Screen entity and EJB similar to SeatCategory.
    // Since I don't have those, I will mock them for compilation purposes in a placeholder file.
}
