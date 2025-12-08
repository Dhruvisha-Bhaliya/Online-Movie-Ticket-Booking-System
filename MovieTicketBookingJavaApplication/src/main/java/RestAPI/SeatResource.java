/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package RestAPI;

import RestAPIStructure.ResponseFormatter;
import entity.Screen;
import entity.Seat;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.util.Date;
import java.util.List;
import user_bean.ScreenBeanLocal;
import user_bean.SeatBeanLocal;

/**
 * REST Web Service
 *
 * @author DELL
 */
@Path("/seats")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SeatResource {

    @EJB
    private SeatBeanLocal seatBean;
    @EJB
    private ScreenBeanLocal screenBean;

    @Context
    private UriInfo uriInfo;

    @GET
    public Response getAll() {
        try {
            List<Seat> seats = seatBean.findAllSeats();
            return ResponseFormatter.success(200, "Seats fetched successfully", seats);
        } catch (Exception e) {
            return ResponseFormatter.error(500, "Failed to fetch seats", e.getMessage());
        }
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Long id) {
        Seat s = seatBean.findSeat(id);
        if (s == null) {
            return ResponseFormatter.error(404, "Seat not found", null);
        }

        return ResponseFormatter.success(200, "Seat fetched successfully", s);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Seat seat) {
        try {
            Screen screenFromPayload = seat.getScreenId();

            if (screenFromPayload == null || screenFromPayload.getScreenId() == null) {
                return ResponseFormatter.error(400, "Missing Screen ID", "The 'screenId' object with the ID property must be provided in the request body.");
            }

            Long screenId = screenFromPayload.getScreenId();
            Screen managedScreen = screenBean.findScreen(screenId); // Now this works!

            if (managedScreen == null) {
                return ResponseFormatter.error(404, "Screen not found", "Screen with ID " + screenId + " does not exist.");
            }
            seat.setScreenId(managedScreen);
            Date now = new Date();
            seat.setCreatedAt(now);
            seat.setUpdatedAt(now);

            seatBean.createSeat(seat);
            return ResponseFormatter.success(
                    201,
                    "Seat created successfully",
                    seat
            );

        } catch (Exception e) {
            return ResponseFormatter.error(400, "Failed to create seat", e.getMessage());
        }
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Long id, Seat updated) {
        // 1. Find the existing managed entity
        Seat existing = seatBean.findSeat(id);
        if (existing == null) {
            return ResponseFormatter.error(404, "Seat not found", null);
        }

        try {
            updated.setSeatId(id);
            Screen screenToPersist;
            if (updated.getScreenId() != null && updated.getScreenId().getScreenId() != null) {
                Long newScreenId = updated.getScreenId().getScreenId();
                Screen managedNewScreen = screenBean.findScreen(newScreenId);

                if (managedNewScreen == null) {
                    return ResponseFormatter.error(404, "Invalid Screen ID", "The new Screen ID provided (" + newScreenId + ") does not exist.");
                }
                screenToPersist = managedNewScreen;

            } else {
                screenToPersist = existing.getScreenId();
            }
            updated.setScreenId(screenToPersist);
            updated.setCreatedAt(existing.getCreatedAt());
            updated.setUpdatedAt(new Date());
            seatBean.editSeat(updated);
            return ResponseFormatter.success(200, "Seat updated successfully", updated);
        } catch (Exception e) {
            return ResponseFormatter.error(400, "Failed to update seat", e.getMessage());
        }
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        Seat existing = seatBean.findSeat(id);
        if (existing == null) {
            return ResponseFormatter.error(404, "Seat not found", null);
        }

        try {
            seatBean.removeSeat(existing);
            return ResponseFormatter.success(200, "Seat deleted successfully", null);
        } catch (Exception e) {
            return ResponseFormatter.error(400, "Failed to delete seat", e.getMessage());
        }
    }

    @GET
    @Path("/screen/{screenId}")
    public Response getSeatsByScreen(@PathParam("screenId") Long screenId) {
        try {
            Screen screen = new Screen();
            screen.setScreenId(screenId);
            List<Seat> seats = seatBean.findSeatsByScreen(screen);

            return ResponseFormatter.success(200, "Seats fetched for screen successfully", seats);
        } catch (Exception e) {
            return ResponseFormatter.error(500, "Failed to fetch seats by screen", e.getMessage());
        }
    }

    @GET
    @Path("/filter")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSeatsByScreenAndStatus(
            @QueryParam("screenId") Long screenId,
            @QueryParam("status") String status) {
        try {
            if (screenId == null || status == null || status.isEmpty()) {
                return ResponseFormatter.error(400, "Missing 'screenId' or 'status' query parameters", null);
            }

            List<Seat> seats = seatBean.findSeatsByScreenAndStatus(screenId, status);
            return ResponseFormatter.success(200, "Filtered seats fetched successfully", seats);
        } catch (Exception e) {
            return ResponseFormatter.error(500, "Failed to fetch filtered seats", e.getMessage());
        }
    }
}
