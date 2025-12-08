/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package AuthServiceAPI;

import AdminBean.AdminServiceLocal;
import AdminBean.UserServiceLocal;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
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

    @EJB
    private UserServiceLocal userService;

    private static final String EMAIL_DOMAIN = "cinemaxhub.com";
    private final PasswordUtil passwordUtil = new PasswordUtil();

    private RoleMaster findRoleByName(String roleName) {
        try {
            TypedQuery<RoleMaster> q = em.createQuery(
                    "SELECT r FROM RoleMaster r WHERE r.role = :roleName", RoleMaster.class);
            q.setParameter("roleName", roleName);
            return q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    private String firstFromUsername(String username) {
        if (username == null) {
            return "user";
        }
        String t = username.trim().toLowerCase();
        if (t.isEmpty()) {
            return "user";
        }
        String[] parts = t.split("\\s+");
        String first = parts[0].replaceAll("[^a-z0-9]", "");
        return first.isEmpty() ? "user" : first;
    }

    private String generateRoleEmail(String username, String role) {
        String base = firstFromUsername(username);
        String suffix;
        switch (role) {
            case "SUPER_ADMIN":
                suffix = ".sadmin@";
                break;
            case "ADMIN":
                suffix = ".admin@";
                break;
            case "MANAGER":
                suffix = ".manager@";
                break;
            case "STAFF":
                suffix = ".staff@";
                break;
            default:
                suffix = ".user@";
                break;
        }
        return base + suffix + EMAIL_DOMAIN;
    }

    private String ensureUniqueEmail(String candidate) {
        String email = candidate;
        int i = 1;
        while (adminService.findByEmail(email) != null || userService.findByEmail(email) != null) {
            int at = candidate.indexOf("@");
            String left = candidate.substring(0, at);
            String right = candidate.substring(at);
            int lastDot = left.lastIndexOf('.');
            String basePart = (lastDot != -1) ? left.substring(0, lastDot) : left;
            email = basePart + i + left.substring(lastDot) + right;
            i++;
            if (i > 1000) {
                break;
            }
        }
        return email;
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private boolean isPasswordStrong(String p) {
        return p != null && p.length() >= 6;
    }

    // ==========================================================
    // 🛡️ ADMIN MANAGEMENT (CRUD)
    // ==========================================================
    public ApiResponse<Map<String, Object>> registerAdmin(Admin admin, String creatorAuthHeader) {
        try {
            if (admin == null || admin.getUsername() == null || admin.getUsername().isEmpty()) {
                return new ApiResponse<>(false, 400, "Invalid request/Username required", null);
            }
            if (!isPasswordStrong(admin.getPassword())) {
                return new ApiResponse<>(false, 400, "Password must be >= 6 characters", null);
            }

            List<Admin> existing = adminService.findAll();
            boolean isBootstrap = existing == null || existing.isEmpty();

            String creatorRole = null;
            if (creatorAuthHeader != null) {
                String token = JwtUtil.getTokenFromHeader(creatorAuthHeader);
                if (token != null && JwtUtil.validateToken(token)) {
                    creatorRole = JwtUtil.extractRole(token);
                }
            }

            String assignRole;
            if (isBootstrap) {
                assignRole = "SUPER_ADMIN";
            } else {
                if (creatorRole == null || !List.of("SUPER_ADMIN", "ADMIN", "MANAGER").contains(creatorRole)) {
                    return new ApiResponse<>(false, 403, "Insufficient privileges or token required.", null);
                }
                assignRole = switch (creatorRole) {
                    case "SUPER_ADMIN" ->
                        "ADMIN";
                    case "ADMIN" ->
                        "MANAGER";
                    case "MANAGER" ->
                        "STAFF";
                    default ->
                        null;
                };
                if (assignRole == null) {
                    return new ApiResponse<>(false, 403, "Creator cannot create any more admin roles.", null);
                }
            }
            String candidate = generateRoleEmail(admin.getUsername(), assignRole);
            String finalEmail = ensureUniqueEmail(candidate);

            RoleMaster roleEntity = findRoleByName(assignRole);
            if (roleEntity == null) {
                return new ApiResponse<>(false, 500, "Role not found: " + assignRole, null);
            }
            admin.setRole(roleEntity);
            admin.setEmail(finalEmail);
            admin.setPassword(PasswordUtil.hashPassword(admin.getPassword()));
            admin.setStatus(admin.getStatus() == null || admin.getStatus().isEmpty() ? "active" : admin.getStatus());
            admin.setCreatedAt(new Date());
            admin.setUpdatedAt(new Date());

            adminService.addadmin(admin);

            String token = JwtUtil.generateToken(admin.getEmail(), roleEntity.getRole());
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("email", admin.getEmail());
            data.put("role", roleEntity.getRole());
            data.put("username", admin.getUsername());

            return new ApiResponse<>(true, 200, "Admin registered successfully", data);
        } catch (Exception e) {
            return new ApiResponse<>(false, 500, "Admin registration failed: " + e.getMessage(), null);
        }
    }

    public ApiResponse<Map<String, Object>> loginAdmin(String email, String password) {
        try {
            if (!isValidEmail(email)) {
                return new ApiResponse<>(false, 400, "Invalid email format", null);
            }
            Admin a = adminService.findByEmail(email);
            if (a == null) {
                return new ApiResponse<>(false, 404, "Admin not found", null);
            }
            if (!passwordUtil.verifyPassword(password, a.getPassword())) {
                return new ApiResponse<>(false, 401, "Invalid credentials", null);
            }
            if ("inactive".equals(a.getStatus())) {
                return new ApiResponse<>(false, 403, "Account is inactive/blocked.", null);
            }

            String token = JwtUtil.generateToken(a.getEmail(), a.getRole().getRole());
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("email", a.getEmail());
            data.put("role", a.getRole().getRole());
            data.put("username", a.getUsername());
            data.put("phoneno", a.getPhoneno());

            return new ApiResponse<>(true, 200, "Login successful", data);
        } catch (Exception e) {
            return new ApiResponse<>(false, 500, "Login failed: " + e.getMessage(), null);
        }
    }

    public ApiResponse<Admin> updateAdminProfile(Admin updatedAdmin, String authHeader) {
        String token = JwtUtil.getTokenFromHeader(authHeader);
        if (token == null || !JwtUtil.validateToken(token)) {
            return new ApiResponse<>(false, 401, "Invalid or missing token.", null);
        }
        String email = JwtUtil.extractEmail(token);
        Admin existingAdmin = adminService.findByEmail(email);

        if (existingAdmin == null) {
            return new ApiResponse<>(false, 404, "Admin not found.", null);
        }

        try {
            if (updatedAdmin.getUsername() != null && !updatedAdmin.getUsername().isEmpty()) {
                existingAdmin.setUsername(updatedAdmin.getUsername());
            }
            if (updatedAdmin.getPhoneno() != null && updatedAdmin.getPhoneno() != 0L) {
                existingAdmin.setPhoneno(updatedAdmin.getPhoneno());
            }
            existingAdmin.setUpdatedAt(new Date());

            Admin mergedAdmin = adminService.updateAdmin(existingAdmin);

            return new ApiResponse<>(true, 200, "Admin profile updated successfully.", mergedAdmin);
        } catch (Exception e) {
            return new ApiResponse<>(false, 500, "Profile update failed: " + e.getMessage(), null);
        }
    }

    public ApiResponse<Map<String, Object>> updateAdminPassword(String oldPassword, String newPassword, String authHeader) {
        String token = JwtUtil.getTokenFromHeader(authHeader);
        if (token == null || !JwtUtil.validateToken(token)) {
            return new ApiResponse<>(false, 401, "Invalid or missing token.", null);
        }
        String email = JwtUtil.extractEmail(token);
        Admin admin = adminService.findByEmail(email);

        if (admin == null) {
            return new ApiResponse<>(false, 404, "Admin not found.", null);
        }
        if (!passwordUtil.verifyPassword(oldPassword, admin.getPassword())) {
            return new ApiResponse<>(false, 401, "Invalid old password.", null);
        }
        if (!isPasswordStrong(newPassword)) {
            return new ApiResponse<>(false, 400, "New password must be >= 6 characters.", null);
        }

        try {
            admin.setPassword(PasswordUtil.hashPassword(newPassword));
            admin.setUpdatedAt(new Date());
            adminService.updateAdmin(admin);

            return new ApiResponse<>(true, 200, "Admin Password updated successfully.", null);
        } catch (Exception e) {
            return new ApiResponse<>(false, 500, "Password update failed: " + e.getMessage(), null);
        }
    }

    public ApiResponse<String> deleteAdmin(String authHeader) {
        String token = JwtUtil.getTokenFromHeader(authHeader);
        if (token == null || !JwtUtil.validateToken(token)) {
            return new ApiResponse<>(false, 401, "Invalid or missing token.", null);
        }

        String email = JwtUtil.extractEmail(token);
        Admin adminToDelete = adminService.findByEmail(email);

        if (adminToDelete == null) {
            return new ApiResponse<>(false, 404, "Admin not found.", null);
        }

        try {
            adminService.deleteAdmin(adminToDelete);
            return new ApiResponse<>(true, 200, "Admin account deleted successfully.", null);

        } catch (Exception e) {
            return new ApiResponse<>(false, 500, "Delete failed: " + e.getMessage(), null);
        }
    }

    // ==========================================================
    // USER MANAGEMENT (CRUD)
    // ==========================================================
    public ApiResponse<Map<String, Object>> registerUser(User user) {
        try {
            if (user == null || user.getUsername() == null || user.getUsername().isEmpty()) {
                return new ApiResponse<>(false, 400, "Invalid request/Username required", null);
            }
            if (!isValidEmail(user.getEmail())) {
                return new ApiResponse<>(false, 400, "Invalid email format", null);
            }
            if (!isPasswordStrong(user.getPassword())) {
                return new ApiResponse<>(false, 400, "Password must be >= 6 characters", null);
            }
            if (adminService.findByEmail(user.getEmail()) != null || userService.findByEmail(user.getEmail()) != null) {
                return new ApiResponse<>(false, 409, "Email already registered.", null);
            }

            RoleMaster role = findRoleByName("CUSTOMER");
            if (role == null) {
                return new ApiResponse<>(false, 500, "Role CUSTOMER not found", null);
            }

            user.setRole(role);
            user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
            user.setStatus(user.getStatus() == null || user.getStatus().isEmpty() ? "active" : user.getStatus());
            user.setCreatedAt(new Date());
            user.setUpdatedAt(new Date());

            userService.addUser(user);

            String token = JwtUtil.generateToken(user.getEmail(), role.getRole());
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("email", user.getEmail());
            data.put("role", role.getRole());
            data.put("username", user.getUsername());

            return new ApiResponse<>(true, 200, "User registered successfully", data);
        } catch (Exception e) {
            return new ApiResponse<>(false, 500, "User registration failed: " + e.getMessage(), null);
        }
    }

    public ApiResponse<Map<String, Object>> loginUser(String email, String password) {
        try {
            if (!isValidEmail(email)) {
                return new ApiResponse<>(false, 400, "Invalid email format", null);
            }
            User u = userService.findByEmail(email);
            if (u == null) {
                return new ApiResponse<>(false, 404, "User not found", null);
            }
            if (!passwordUtil.verifyPassword(password, u.getPassword())) {
                return new ApiResponse<>(false, 401, "Invalid credentials", null);
            }
            if ("inactive".equals(u.getStatus())) {
                return new ApiResponse<>(false, 403, "Account is inactive/blocked.", null);
            }

            String token = JwtUtil.generateToken(u.getEmail(), u.getRole().getRole());
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("email", u.getEmail());
            data.put("role", u.getRole().getRole());
            data.put("username", u.getUsername());

            return new ApiResponse<>(true, 200, "Login successful", data);
        } catch (Exception e) {
            return new ApiResponse<>(false, 500, "Login failed: " + e.getMessage(), null);
        }
    }

    public ApiResponse<User> updateUserProfile(User updatedUser, String authHeader) {
        String token = JwtUtil.getTokenFromHeader(authHeader);
        if (token == null || !JwtUtil.validateToken(token)) {
            return new ApiResponse<>(false, 401, "Invalid or missing token.", null);
        }
        String email = JwtUtil.extractEmail(token);
        User existingUser = userService.findByEmail(email);

        if (existingUser == null) {
            return new ApiResponse<>(false, 404, "User not found.", null);
        }

        try {
            if (updatedUser.getUsername() != null && !updatedUser.getUsername().isEmpty()) {
                existingUser.setUsername(updatedUser.getUsername());
            }
            if (updatedUser.getPhoneno() != null && updatedUser.getPhoneno() != 0L) {
                existingUser.setPhoneno(updatedUser.getPhoneno());
            }
            existingUser.setUpdatedAt(new Date());

            User mergedUser = userService.updateUser(existingUser);

            return new ApiResponse<>(true, 200, "User profile updated successfully.", mergedUser);
        } catch (Exception e) {
            return new ApiResponse<>(false, 500, "Profile update failed: " + e.getMessage(), null);
        }
    }

    public ApiResponse<Map<String, Object>> updateUserPassword(String oldPassword, String newPassword, String authHeader) {
        String token = JwtUtil.getTokenFromHeader(authHeader);
        if (token == null || !JwtUtil.validateToken(token)) {
            return new ApiResponse<>(false, 401, "Invalid or missing token.", null);
        }
        String email = JwtUtil.extractEmail(token);
        User user = userService.findByEmail(email);

        if (user == null) {
            return new ApiResponse<>(false, 404, "User not found.", null);
        }
        if (!passwordUtil.verifyPassword(oldPassword, user.getPassword())) {
            return new ApiResponse<>(false, 401, "Invalid old password.", null);
        }
        if (!isPasswordStrong(newPassword)) {
            return new ApiResponse<>(false, 400, "New password must be >= 6 characters.", null);
        }

        try {
            user.setPassword(PasswordUtil.hashPassword(newPassword));
            user.setUpdatedAt(new Date());
            userService.updateUser(user);

            return new ApiResponse<>(true, 200, "User Password updated successfully.", null);
        } catch (Exception e) {
            return new ApiResponse<>(false, 500, "Password update failed: " + e.getMessage(), null);
        }
    }

    public ApiResponse<String> deleteUser(String authHeader) {
        String token = JwtUtil.getTokenFromHeader(authHeader);
        if (token == null || !JwtUtil.validateToken(token)) {
            return new ApiResponse<>(false, 401, "Invalid or missing token.", null);
        }
        String email = JwtUtil.extractEmail(token);
        User userToDelete = userService.findByEmail(email);

        if (userToDelete == null) {
            return new ApiResponse<>(false, 404, "User not found.", null);
        }

        try {
            userService.deleteUser(userToDelete);

            return new ApiResponse<>(true, 200, "User account deactivated successfully.", null);
        } catch (Exception e) {
            return new ApiResponse<>(false, 500, "Deactivation failed: " + e.getMessage(), null);
        }
    }
}
