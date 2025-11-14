/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package RestAPI;

import RestAPIStructure.ResponseFormatter;
import RestAPIStructure.SecurityRoles;
import entity.Screen;
import entity.Seat;
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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
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

    @Context
    private UriInfo uriInfo;

    // ✅ Anyone can view all seats
    @GET
    public Response getAll() {
        try {
            List<Seat> seats = seatBean.findAllSeats();
            return ResponseFormatter.success(200, "Seats fetched successfully", seats);
        } catch (Exception e) {
            return ResponseFormatter.error(500, "Failed to fetch seats", e.getMessage());
        }
    }

    // ✅ Get a specific seat by ID
    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Long id) {
        Seat s = seatBean.findSeat(id);
        if (s == null) {
            return ResponseFormatter.error(404, "Seat not found", null);
        }

        return ResponseFormatter.success(200, "Seat fetched successfully", s);
    }

    // ✅ Only ADMIN, SUPER_ADMIN, MANAGER, STAFF can add a new seat
    @POST
    @RolesAllowed({
        SecurityRoles.ADMIN,
        SecurityRoles.SUPER_ADMIN,
        SecurityRoles.MANAGER,
        SecurityRoles.STAFF
    })
    public Response create(Seat seat) {
        try {
            seatBean.createSeat(seat);
            URI created = uriInfo.getAbsolutePathBuilder()
                    .path(String.valueOf(seat.getSeatId()))
                    .build();
            return Response.created(created)
                    .entity(ResponseFormatter.success(201, "Seat created successfully", seat))
                    .build();
        } catch (Exception e) {
            return ResponseFormatter.error(400, "Failed to create seat", e.getMessage());
        }
    }

    // ✅ Update existing seat — only ADMIN, SUPER_ADMIN, MANAGER
    @PUT
    @Path("{id}")
    @RolesAllowed({
        SecurityRoles.ADMIN,
        SecurityRoles.SUPER_ADMIN,
        SecurityRoles.MANAGER
    })
    public Response update(@PathParam("id") Long id, Seat updated) {
        Seat existing = seatBean.findSeat(id);
        if (existing == null) {
            return ResponseFormatter.error(404, "Seat not found", null);
        }

        try {
            updated.setSeatId(id);
            seatBean.editSeat(updated);
            return ResponseFormatter.success(200, "Seat updated successfully", updated);
        } catch (Exception e) {
            return ResponseFormatter.error(400, "Failed to update seat", e.getMessage());
        }
    }

    // ✅ Delete a seat — only ADMIN or SUPER_ADMIN
    @DELETE
    @Path("{id}")
    @RolesAllowed({
        SecurityRoles.ADMIN,
        SecurityRoles.SUPER_ADMIN
    })
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

    // ✅ Get all seats by screen (for filter dropdown in UI)
    @GET
    @Path("/screen/{screenId}")
    public Response getSeatsByScreen(@PathParam("screenId") Long screenId) {
        try {
            // ✅ Create a Screen object using the provided screenId
            Screen screen = new Screen();
            screen.setScreenId(screenId);

            // ✅ Pass the Screen object to the bean method
            List<Seat> seats = seatBean.findSeatsByScreen(screen);

            return ResponseFormatter.success(200, "Seats fetched for screen successfully", seats);
        } catch (Exception e) {
            return ResponseFormatter.error(500, "Failed to fetch seats by screen", e.getMessage());
        }
    }

    // ✅ Get seats by screen and status (Available, Booked, Under Maintenance)
    @GET
    @Path("/filter")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "USER"})
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
