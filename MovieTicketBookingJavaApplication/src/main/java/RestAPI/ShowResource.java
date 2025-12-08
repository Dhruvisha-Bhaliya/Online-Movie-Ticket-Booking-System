/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package RestAPI;

import RestAPIStructure.ResponseFormatter;
import entity.Showmovie;
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
import user_bean.ShowBeanLocal;

/**
 * REST Web Service
 *
 * @author DELL
 */
@Path("/shows")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ShowResource {

    @EJB
    private ShowBeanLocal showBean;

    @Context
    private UriInfo uriInfo;

    @GET
    public Response getAll() {
        try {
            List<Showmovie> shows = showBean.findAllShows();
            return ResponseFormatter.success(200, "Shows fetched successfully", shows);
        } catch (Exception e) {
            return ResponseFormatter.error(500, "Failed to fetch shows", e.getMessage());
        }
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Long id) {
        Showmovie s = showBean.find(id);
        if (s == null) {
            return ResponseFormatter.error(404, "Show not found", null);
        }

        return ResponseFormatter.success(200, "Show fetched successfully", s);
    }

    @POST
    public Response create(Showmovie showmovie) {
        try {
            Date now = new Date();
            showmovie.setCreatedAt(now);
            showmovie.setUpdatedAt(now);

            showBean.createShow(showmovie);

            return ResponseFormatter.success(
                    201,
                    "Show created successfully",
                    showmovie
            );

        } catch (Exception e) {
            return ResponseFormatter.error(400, "Failed to create Show", e.getMessage());
        }
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Long id, Showmovie updatedData) {

        try {
            Showmovie existing = showBean.find(id);

            if (existing == null) {
                return ResponseFormatter.error(404, "Show not found", null);
            }
            existing.setShowTime(updatedData.getShowTime());
            existing.setMovieId(updatedData.getMovieId());
            existing.setScreenId(updatedData.getScreenId());
            existing.setStatus(updatedData.getStatus());

            existing.setUpdatedAt(new Date());
            showBean.editShow(existing);

            return ResponseFormatter.success(200, "Show updated successfully", existing);

        } catch (Exception e) {
            return ResponseFormatter.error(400, "Failed to update Show", e.getMessage());
        }
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        Showmovie existing = showBean.find(id);
        if (existing == null) {
            return ResponseFormatter.error(404, "Show not found", null);
        }

        try {
            showBean.removeShow(existing);
            return ResponseFormatter.success(200, "Show deleted successfully", null);
        } catch (Exception e) {
            return ResponseFormatter.error(400, "Failed to delete show", e.getMessage());
        }
    }

    @GET
    @Path("/movie/{movieId}")
    public Response getShowsByMovie(@PathParam("movieId") Long movieId) {
        try {
            List<Showmovie> shows = showBean.findShowByMovieId(movieId);
            return ResponseFormatter.success(200, "Shows fetched by movie successfully", shows);
        } catch (Exception e) {
            return ResponseFormatter.error(500, "Failed to fetch shows by movie", e.getMessage());
        }
    }

    @GET
    @Path("/movie/{movieId}/dates")
    public Response getAvailableDates(@PathParam("movieId") Long movieId) {
        try {
            List<Date> dates = showBean.findAvailableDates(movieId);
            return ResponseFormatter.success(200, "Available dates fetched successfully", dates);
        } catch (Exception e) {
            return ResponseFormatter.error(500, "Failed to fetch available dates", e.getMessage());
        }
    }

    @GET
    @Path("/movie/{movieId}/filter")
    public Response getShowsByMovieDateLanguage(
            @PathParam("movieId") Long movieId,
            @QueryParam("date") Date date,
            @QueryParam("language") String language) {
        try {
            if (date == null || language == null || language.isEmpty()) {
                return ResponseFormatter.error(400, "Missing 'date' or 'language' query parameters", null);
            }

            List<Showmovie> shows = showBean.findShowsByMovieDateAndLanguage(movieId, date, language);
            return ResponseFormatter.success(200, "Filtered shows fetched successfully", shows);
        } catch (Exception e) {
            return ResponseFormatter.error(500, "Failed to fetch filtered shows", e.getMessage());
        }
    }
}
