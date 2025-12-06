/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package RestAPI;

import RestAPIStructure.ResponseFormatter;
import entity.Movie;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import user_bean.MovieBeanLocal;

/**
 *
 * @author DELL
 */
@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {

    @EJB
    private MovieBeanLocal movieBean;

    @Context
    private UriInfo uriInfo;

    @GET
    public Response getAll() {
        try {
            List<Movie> movies = movieBean.getAllMovies();
            return ResponseFormatter.success(200, "Movies fetched successfully", movies);
        } catch (Exception e) {
            return ResponseFormatter.error(500, "Failed to fetch movies", e.getMessage());
        }
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Long id) {
        Movie m = movieBean.findMovie(id);
        if (m == null) {
            return ResponseFormatter.error(404, "Movie not found", null);
        }

        return ResponseFormatter.success(200, "Movie fetched successfully", m);
    }

    @POST
    public Response create(Movie movie) {
        try {
            movieBean.addMovie(movie);

            Map<String, Object> resp = new HashMap<>();
            resp.put("status", true);
            resp.put("status_code", 201);
            resp.put("message", "Movie created successfully");
            resp.put("data", movie);

            return Response.status(201).entity(resp).build();

        } catch (Exception e) {

            Map<String, Object> error = new HashMap<>();
            error.put("status", false);
            error.put("status_code", 400);
            error.put("message", "Failed to create movie");
            error.put("errors", e.getMessage());

            return Response.status(400).entity(error).build();
        }
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Long id, Movie updated) {
        Movie existing = movieBean.findMovie(id);
        if (existing == null) {
            return ResponseFormatter.error(404, "Movie not found", null);
        }

        try {
            for (var f : Movie.class.getDeclaredFields()) {
                f.setAccessible(true);

                if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                    continue;
                }
                if (java.lang.reflect.Modifier.isFinal(f.getModifiers())) {
                    continue;
                }
                if (f.getName().equals("movieId")) {
                    continue;
                }

                Object val = f.get(updated);
                if (val != null) {
                    f.set(existing, val);
                }
            }

            movieBean.updateMovie(existing);
            return ResponseFormatter.success(200, "Movie updated successfully", existing);

        } catch (Exception e) {
            return ResponseFormatter.error(400, "Failed to update movie", e.getMessage());
        }
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        Movie existing = movieBean.findMovie(id);
        if (existing == null) {
            return ResponseFormatter.error(404, "Movie not found", null);
        }

        try {
            movieBean.deleteMovie(id);
            return ResponseFormatter.success(200, "Movie deleted successfully", null);
        } catch (Exception e) {
            return ResponseFormatter.error(400, "Failed to delete movie", e.getMessage());
        }
    }
}
