/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package RestAPI;

import AuthServiceAPI.AuthServiceAPI;
import RestAPIStructure.ResponseFormatter;
import entity.Admin;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

/**
 * REST Web Service
 *
 * @author DELL
 */
@Path("admin/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminAuthResource {

    private AuthServiceAPI authService;

    @POST
    @Path("/register")
    public Response register(Admin admin) {
        try {
            var apiResponse = authService.registerAdmin(admin);
            return ResponseFormatter.success(
                    apiResponse.getStatus_code(),
                    apiResponse.getMessage(),
                    apiResponse.getData()
            );
        } catch (Exception e) {
            return ResponseFormatter.error(500, "Admin registration failed", e.getMessage());
        }
    }

    @POST
    @Path("/login")
    public Response login(Map<String, String> loginData) {
        try {
            var apiResponse = authService.loginAdmin(loginData.get("email"), loginData.get("password"));
            return ResponseFormatter.success(
                    apiResponse.getStatus_code(),
                    apiResponse.getMessage(),
                    apiResponse.getData()
            );
        } catch (Exception e) {
            return ResponseFormatter.error(500, "Admin login failed", e.getMessage());
        }
    }
}
