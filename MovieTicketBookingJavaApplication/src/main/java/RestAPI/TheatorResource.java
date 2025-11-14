/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package RestAPI;

import RestAPIStructure.ResponseFormatter;
import RestAPIStructure.SecurityRoles;
import entity.Theater;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import user_bean.TheaterBeanLocal;

/**
 * REST Web Service
 *
 * @author DELL
 */
@Path("/theators")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TheatorResource {

    @EJB
    private TheaterBeanLocal theaterBean;
    
    @Context
    private UriInfo uriInfo;

    // ✅ Anyone can view all theaters
    @GET
    public Response getAll() {
        try {
            List<Theater> theaters = theaterBean.findallTheater();
            return ResponseFormatter.success(200, "Theaters fetched successfully", theaters);
        } catch (Exception e) {
            return ResponseFormatter.error(500, "Failed to fetch theaters", e.getMessage());
        }
    }

    // ✅ Anyone can view specific theater details
    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Long id) {
        Theater t = theaterBean.find(id);
        if (t == null)
            return ResponseFormatter.error(404, "Theater not found", null);

        return ResponseFormatter.success(200, "Theater fetched successfully", t);
    }

    // ✅ Only ADMIN, SUPER_ADMIN, MANAGER, STAFF can add a new theater
    @POST
    @RolesAllowed({
        SecurityRoles.ADMIN,
        SecurityRoles.SUPER_ADMIN,
        SecurityRoles.MANAGER,
        SecurityRoles.STAFF
    })
    public Response create(Theater theater) {
        try {
            theaterBean.createTheater(theater);
            URI created = uriInfo.getAbsolutePathBuilder()
                    .path(String.valueOf(theater.getTheaterId()))
                    .build();
            return Response.created(created)
                    .entity(ResponseFormatter.success(201, "Theater created successfully", theater))
                    .build();
        } catch (Exception e) {
            return ResponseFormatter.error(400, "Failed to create theater", e.getMessage());
        }
    }

    // ✅ Update existing theater — allowed roles only
    @PUT
    @Path("{id}")
    @RolesAllowed({
        SecurityRoles.ADMIN,
        SecurityRoles.SUPER_ADMIN,
        SecurityRoles.MANAGER
    })
    public Response update(@PathParam("id") Long id, Theater updated) {
        Theater existing = theaterBean.find(id);
        if (existing == null) {
            return ResponseFormatter.error(404, "Theater not found", null);
        }

        try {
            updated.setTheaterId(id);
            theaterBean.editTheater(updated);
            return ResponseFormatter.success(200, "Theater updated successfully", updated);
        } catch (Exception e) {
            return ResponseFormatter.error(400, "Failed to update theater", e.getMessage());
        }
    }

    // ✅ Delete a theater — only ADMIN or SUPER_ADMIN
    @DELETE
    @Path("{id}")
    @RolesAllowed({
        SecurityRoles.ADMIN,
        SecurityRoles.SUPER_ADMIN
    })
    public Response delete(@PathParam("id") Long id) {
        Theater existing = theaterBean.find(id);
        if (existing == null) {
            return ResponseFormatter.error(404, "Theater not found", null);
        }

        try {
            theaterBean.removeTheater(existing);
            return ResponseFormatter.success(200, "Theater deleted successfully", null);
        } catch (Exception e) {
            return ResponseFormatter.error(400, "Failed to delete theater", e.getMessage());
        }
    }
}