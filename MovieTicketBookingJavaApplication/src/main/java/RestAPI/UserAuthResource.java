/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package RestAPI;


import AuthServiceAPI.AuthServiceAPI;
import RestAPIStructure.ResponseFormatter;
import entity.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

/**
 * REST Web Service
 *
 * @author DELL
 */
@Path("/user/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserAuthResource {
 private final AuthServiceAPI authService = new AuthServiceAPI();

    @POST
    @Path("/register")
    public Response register(User user) {
        try {
            var apiResponse = authService.registerUser(user);
            return ResponseFormatter.success(
                    apiResponse.getStatus_code(),
                    apiResponse.getMessage(),
                    apiResponse.getData()
            );
        } catch (Exception e) {
            return ResponseFormatter.error(500, "User registration failed", e.getMessage());
        }
    }

    @POST
    @Path("/login")
    public Response login(Map<String, String> loginData) {
        try {
            var apiResponse = authService.loginUser(loginData.get("email"), loginData.get("password"));
            return ResponseFormatter.success(
                    apiResponse.getStatus_code(),
                    apiResponse.getMessage(),
                    apiResponse.getData()
            );
        } catch (Exception e) {
            return ResponseFormatter.error(500, "User login failed", e.getMessage());
        }
    }
}