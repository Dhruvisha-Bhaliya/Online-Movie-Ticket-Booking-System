/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package RestAPI;

import RestAPIStructure.ResponseFormatter;
import RestAPIStructure.SecurityRoles;
import entity.Screen;
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
import user_bean.ScreenBeanLocal;

/**
 * REST Web Service
 *
 * @author DELL
 */
@Path("/screens")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ScreenResource {

    @EJB
    private ScreenBeanLocal screenBean;

    @Context
    private UriInfo uriInfo;

    // ✅ Get all screens (anyone can view)
    @GET
    public Response getAll() {
        try {
            List<Screen> screens = screenBean.findAllScreen();
            return ResponseFormatter.success(200, "Screens fetched successfully", screens);
        } catch (Exception e) {
            return ResponseFormatter.error(500, "Failed to fetch screens", e.getMessage());
        }
    }

    // ✅ Get screen by ID (anyone can view)
    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Long id) {
        Screen s = screenBean.findScreen(id);
        if (s == null) {
            return ResponseFormatter.error(404, "Screen not found", null);
        }

        return ResponseFormatter.success(200, "Screen fetched successfully", s);
    }

    // ✅ Add new screen (restricted roles)
    @POST
    @RolesAllowed({
        SecurityRoles.ADMIN,
        SecurityRoles.SUPER_ADMIN,
        SecurityRoles.MANAGER,
        SecurityRoles.STAFF
    })
    public Response create(Screen screen) {
        try {
            screenBean.createScreen(screen);
            URI created = uriInfo.getAbsolutePathBuilder()
                    .path(String.valueOf(screen.getScreenId()))
                    .build();
            return Response.created(created)
                    .entity(ResponseFormatter.success(201, "Screen created successfully", screen))
                    .build();
        } catch (Exception e) {
            return ResponseFormatter.error(400, "Failed to create screen", e.getMessage());
        }
    }

    // ✅ Update screen details
    @PUT
    @Path("{id}")
    @RolesAllowed({
        SecurityRoles.ADMIN,
        SecurityRoles.SUPER_ADMIN,
        SecurityRoles.MANAGER
    })
    public Response update(@PathParam("id") Long id, Screen updated) {
        Screen existing = screenBean.findScreen(id);
        if (existing == null) {
            return ResponseFormatter.error(404, "Screen not found", null);
        }

        try {
            updated.setScreenId(id);
            screenBean.editScreen(updated);
            return ResponseFormatter.success(200, "Screen updated successfully", updated);
        } catch (Exception e) {
            return ResponseFormatter.error(400, "Failed to update screen", e.getMessage());
        }
    }

    // ✅ Delete screen (only admin/super-admin)
    @DELETE
    @Path("{id}")
    @RolesAllowed({
        SecurityRoles.ADMIN,
        SecurityRoles.SUPER_ADMIN
    })
    public Response delete(@PathParam("id") Long id) {
        Screen existing = screenBean.findScreen(id);
        if (existing == null) {
            return ResponseFormatter.error(404, "Screen not found", null);
        }

        try {
            screenBean.removeScreen(existing);
            return ResponseFormatter.success(200, "Screen deleted successfully", null);
        } catch (Exception e) {
            return ResponseFormatter.error(400, "Failed to delete screen", e.getMessage());
        }
    }
}
