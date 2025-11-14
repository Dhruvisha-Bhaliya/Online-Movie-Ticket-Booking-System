/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package AuthServiceAPI;

import AdminBean.AdminServiceLocal;
import RestAPIStructure.ApiResponse;
import entity.Admin;
import entity.RoleMaster;
import entity.User;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import util.JwtUtil;
import util.PasswordUtil;

/**
 *
 * @author DELL
 */
@Stateless
public class AuthServiceAPI {
   
    @PersistenceContext(unitName = "myMovie")
    private EntityManager em;

    @EJB
    private AdminServiceLocal adminService;

    private final PasswordUtil passwordUtil = new PasswordUtil();
    private final JwtUtil jwtUtil = new JwtUtil();

    // ✅ USER REGISTRATION
    public ApiResponse<User> registerUser(User user) {
        try {
            RoleMaster role = findRoleByName("CUSTOMER");
            user.setRole(role);
            user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
            em.persist(user);

            return new ApiResponse<>(true, 200, "User registered successfully", user);
        } catch (Exception e) {
            return new ApiResponse<>(false, 500, "User registration failed: " + e.getMessage(), null);
        }
    }

    // ✅ USER LOGIN
    public ApiResponse<String> loginUser(String email, String password) {
        try {
            User user = findUserByEmail(email);
            if (user == null) {
                return new ApiResponse<>(false, 404, "User not found", null);
            }

            if (!passwordUtil.verifyPassword(password, user.getPassword())) {
                return new ApiResponse<>(false, 401, "Invalid credentials", null);
            }

            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().getRole());
            return new ApiResponse<>(true, 200, "Login successful", token);

        } catch (Exception e) {
            return new ApiResponse<>(false, 500, "Login failed: " + e.getMessage(), null);
        }
    }

    // ✅ ADMIN REGISTRATION
    public ApiResponse<Admin> registerAdmin(Admin admin) {
        try {
            RoleMaster role = assignAdminRoleByEmail(admin.getEmail());
            admin.setRole(role);
            admin.setPassword(PasswordUtil.hashPassword(admin.getPassword()));

            adminService.addadmin(admin);
            return new ApiResponse<>(true, 200, "Admin registered successfully", admin);
        } catch (Exception e) {
            return new ApiResponse<>(false, 500, "Admin registration failed: " + e.getMessage(), null);
        }
    }

    // ✅ ADMIN LOGIN
    public ApiResponse<String> loginAdmin(String email, String password) {
        try {
            Admin admin = adminService.findByEmailAndPassword(email, password);
            if (admin == null) {
                return new ApiResponse<>(false, 404, "Invalid email or password", null);
            }

            String token = jwtUtil.generateToken(admin.getEmail(), admin.getRole().getRole());
            return new ApiResponse<>(true, 200, "Login successful", token);

        } catch (Exception e) {
            return new ApiResponse<>(false, 500, "Login failed: " + e.getMessage(), null);
        }
    }

    // ✅ Helper: Find Role by name
    private RoleMaster findRoleByName(String roleName) {
        try {
            TypedQuery<RoleMaster> query = em.createQuery(
                "SELECT r FROM RoleMaster r WHERE r.roleName = :roleName", RoleMaster.class);
            query.setParameter("roleName", roleName);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    // ✅ Helper: Find User by email
    private User findUserByEmail(String email) {
        try {
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    // ✅ Helper: Role assignment logic for Admin
    private RoleMaster assignAdminRoleByEmail(String email) {
        email = email.toLowerCase();

        if (email.contains(".superadmin@")) return findRoleByName("SUPER_ADMIN");
        if (email.contains(".manager@")) return findRoleByName("MANAGER");
        if (email.contains(".staff@")) return findRoleByName("STAFF");
        if (email.contains(".admin@")) return findRoleByName("ADMIN");

        return findRoleByName("CUSTOMER");
    }
}