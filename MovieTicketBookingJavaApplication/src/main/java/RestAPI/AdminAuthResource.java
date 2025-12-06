/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package RestAPI;

import AuthServiceAPI.AuthServiceAPI;
import RestAPIStructure.ResponseFormatter;
import entity.Admin;
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
@Path("admin/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminAuthResource {

    @EJB
    private AuthServiceAPI authService;

    // Helper method to consolidate success/error response formatting
    private Response respond(RestAPIStructure.ApiResponse<?> apiResponse) {
        if (apiResponse.isStatus()) {
            return ResponseFormatter.success(apiResponse.getStatus_code(), apiResponse.getMessage(), apiResponse.getData());
        } else {
            return ResponseFormatter.error(apiResponse.getStatus_code(), apiResponse.getMessage(), null);
        }
    }

    // C - Register New Admin (Hierarchical Creation)
    // The creator's role is checked inside AuthServiceAPI via the Authorization header
    @POST
    @Path("/register")
    public Response register(@Context HttpHeaders headers, Admin admin) {
        String authHeader = headers.getHeaderString("Authorization");
        var apiResponse = authService.registerAdmin(admin, authHeader);
        return respond(apiResponse);
    }

    // R - Login Admin
    @POST
    @Path("/login")
    public Response login(Map<String, String> loginData) {
        var apiResponse = authService.loginAdmin(loginData.get("email"), loginData.get("password"));
        return respond(apiResponse);
    }

    // U - Update Admin Profile (Requires Authentication)
    @PUT
    @Path("/profile")
    public Response updateProfile(Admin updatedAdmin, @Context HttpHeaders headers) {
        String authHeader = headers.getHeaderString("Authorization");
        var apiResponse = authService.updateAdminProfile(updatedAdmin, authHeader);
        return respond(apiResponse);
    }

    // U - Update Admin Password (Requires Authentication)
    @PUT
    @Path("/updatePassword")
    public Response updatePassword(Map<String, String> requestBody, @Context HttpHeaders headers) {
        String authHeader = headers.getHeaderString("Authorization");
        String oldPassword = requestBody.get("oldPassword");
        String newPassword = requestBody.get("newPassword");
        var apiResponse = authService.updateAdminPassword(oldPassword, newPassword, authHeader);
        return respond(apiResponse);
    }

    // D - Deactivate/Delete Admin Account (Requires Authentication)
    @DELETE
    @Path("/delete")
    public Response deleteAccount(@Context HttpHeaders headers) {
        String authHeader = headers.getHeaderString("Authorization");
        var apiResponse = authService.deleteAdmin(authHeader);
        return respond(apiResponse);
    }
}
