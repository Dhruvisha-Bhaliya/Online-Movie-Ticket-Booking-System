/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package RestAPI;

import RestAPIStructure.ResponseFormatter;
import entity.Screen;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Date;
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

    @GET
    public Response getAll() {
        try {
            List<Screen> screens = screenBean.findAllScreen();
            return ResponseFormatter.success(200, "Screens fetched successfully", screens);
        } catch (Exception e) {
            return ResponseFormatter.error(500, "Failed to fetch screens", e.getMessage());
        }
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Long id) {
        Screen s = screenBean.findScreen(id);
        if (s == null) {
            return ResponseFormatter.error(404, "Screen not found", null);
        }

        return ResponseFormatter.success(200, "Screen fetched successfully", s);
    }

    @POST
    public Response create(Screen screen) {
        try {
            Date now = new Date();
            screen.setCreatedAt(now);
            screen.setUpdatedAt(now);

            screenBean.createScreen(screen);

            return ResponseFormatter.success(
                    201,
                    "Screen created successfully",
                    screen
            );

        } catch (Exception e) {
            return ResponseFormatter.error(400, "Failed to create screen", e.getMessage());
        }
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Long id, Screen updatedData) {

        try {
            Screen existing = screenBean.findScreen(id);

            if (existing == null) {
                return ResponseFormatter.error(404, "Screen not found", null);
            }

            existing.setScreenName(updatedData.getScreenName());
            existing.setCapacity(updatedData.getCapacity());
            existing.setTheaterId(updatedData.getTheaterId());
            existing.setScreenNumber(updatedData.getScreenNumber());
            existing.setStatus(updatedData.getStatus());

            existing.setUpdatedAt(new Date());
            screenBean.editScreen(existing);

            return ResponseFormatter.success(200, "Screen updated successfully", existing);

        } catch (Exception e) {
            return ResponseFormatter.error(400, "Failed to update screen", e.getMessage());
        }
    }

    @DELETE
    @Path("{id}")
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
