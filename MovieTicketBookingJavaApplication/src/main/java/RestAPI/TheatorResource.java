/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package RestAPI;

import RestAPIStructure.ResponseFormatter;
import entity.Theater;
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

    @GET
    public Response getAll() {
        try {
            List<Theater> theaters = theaterBean.findallTheater();
            return ResponseFormatter.success(200, "Theaters fetched successfully", theaters);
        } catch (Exception e) {
            return ResponseFormatter.error(500, "Failed to fetch theaters", e.getMessage());
        }
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Long id) {
        Theater t = theaterBean.find(id);
        if (t == null) {
            return ResponseFormatter.error(404, "Theater not found", null);
        }

        return ResponseFormatter.success(200, "Theater fetched successfully", t);
    }

    @POST
    public Response create(Theater theater) {
        try {
            theater.setCreatedAt(new Date());
            theater.setUpdatedAt(new Date());
            theater.setStatus("ACTIVE");

            theaterBean.createTheater(theater);

            return ResponseFormatter.success(
                    201,
                    "Theater created successfully",
                    theater
            );

        } catch (Exception e) {
            return ResponseFormatter.error(400, "Failed to create theater", e.getMessage());
        }
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Long id, Theater updatedData) {
        try {
            Theater existing = theaterBean.find(id);

            if (existing == null) {
                return ResponseFormatter.error(404, "Theater not found", null);
            }

            existing.setTheaterName(updatedData.getTheaterName());
            existing.setCity(updatedData.getCity());
            existing.setAddress(updatedData.getAddress());
            existing.setStatus(updatedData.getStatus());
            existing.setUpdatedAt(new Date());

            theaterBean.editTheater(existing);

            return ResponseFormatter.success(200, "Theater updated successfully", existing);

        } catch (Exception e) {
            return ResponseFormatter.error(400, "Failed to update theater", e.getMessage());
        }
    }

    @DELETE
    @Path("{id}")
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
