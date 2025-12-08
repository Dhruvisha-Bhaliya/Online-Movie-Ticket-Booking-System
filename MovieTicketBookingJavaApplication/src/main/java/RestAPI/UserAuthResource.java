/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package RestAPI;

import AuthServiceAPI.AuthServiceAPI;
import RestAPIStructure.ResponseFormatter;
import entity.User;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

/**
 * REST Web Service
 *
 * @author DELL
 */
@Path("user/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserAuthResource {

    @EJB
    private AuthServiceAPI authService;

    private Response respond(RestAPIStructure.ApiResponse<?> apiResponse) {
        if (apiResponse.isStatus()) {
            return ResponseFormatter.success(apiResponse.getStatus_code(), apiResponse.getMessage(), apiResponse.getData());
        } else {
            return ResponseFormatter.error(apiResponse.getStatus_code(), apiResponse.getMessage(), null);
        }
    }

    @POST
    @Path("/register")
    public Response register(User user) {
        var apiResponse = authService.registerUser(user);
        return respond(apiResponse);
    }

    @POST
    @Path("/login")
    public Response login(Map<String, String> loginData) {
        var apiResponse = authService.loginUser(loginData.get("email"), loginData.get("password"));
        return respond(apiResponse);
    }

    @PUT
    @Path("/profile")
    public Response updateProfile(User updatedUser, @Context HttpHeaders headers) {
        String authHeader = headers.getHeaderString("Authorization");
        var apiResponse = authService.updateUserProfile(updatedUser, authHeader);
        return respond(apiResponse);
    }

    @PUT
    @Path("/updatePassword")
    public Response updatePassword(Map<String, String> requestBody, @Context HttpHeaders headers) {
        String authHeader = headers.getHeaderString("Authorization");
        String oldPassword = requestBody.get("oldPassword");
        String newPassword = requestBody.get("newPassword");
        var apiResponse = authService.updateUserPassword(oldPassword, newPassword, authHeader);
        return respond(apiResponse);
    }

    @DELETE
    @Path("/delete")
    public Response deleteAccount(@Context HttpHeaders headers) {
        String authHeader = headers.getHeaderString("Authorization");
        var apiResponse = authService.deleteUser(authHeader);
        return respond(apiResponse);
    }
}
