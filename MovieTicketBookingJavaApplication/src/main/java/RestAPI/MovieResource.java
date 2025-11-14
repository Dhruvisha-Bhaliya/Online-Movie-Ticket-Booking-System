/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package RestAPI;

import RestAPIStructure.ResponseFormatter;
import RestAPIStructure.SecurityRoles;
import entity.Movie;
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

    // ✅ Anyone can view movies (public access)
    @GET
    public Response getAll() {
        try {
            List<Movie> movies = movieBean.getAllMovies();
            return ResponseFormatter.success(200, "Movies fetched successfully", movies);
        } catch (Exception e) {
            return ResponseFormatter.error(500, "Failed to fetch movies", e.getMessage());
        }
    }

    // ✅ Anyone can view movie details
    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Long id) {
        Movie m = movieBean.findMovie(id);
        if (m == null)
            return ResponseFormatter.error(404, "Movie not found", null);

        return ResponseFormatter.success(200, "Movie fetched successfully", m);
    }

    // ✅ Only ADMIN, SUPER_ADMIN, MANAGER, STAFF can add new movies
    @POST
    @RolesAllowed({
        SecurityRoles.ADMIN,
        SecurityRoles.SUPER_ADMIN,
        SecurityRoles.MANAGER,
        SecurityRoles.STAFF
    })
    public Response create(Movie movie) {
        try {
            movieBean.addMovie(movie);
            URI created = uriInfo.getAbsolutePathBuilder()
                                 .path(String.valueOf(movie.getMovieId()))
                                 .build();
            return Response.created(created)
                    .entity(ResponseFormatter.success(201, "Movie created successfully", movie))
                    .build();
        } catch (Exception e) {
            return ResponseFormatter.error(400, "Failed to create movie", e.getMessage());
        }
    }

    // ✅ Only ADMIN, SUPER_ADMIN, MANAGER can update movie
    @PUT
    @Path("{id}")
    @RolesAllowed({
        SecurityRoles.ADMIN,
        SecurityRoles.SUPER_ADMIN,
        SecurityRoles.MANAGER
    })
    public Response update(@PathParam("id") Long id, Movie updated) {
        Movie existing = movieBean.findMovie(id);
        if (existing == null)
            return ResponseFormatter.error(404, "Movie not found", null);

        try {
            updated.setMovieId(id);
            movieBean.updateMovie(updated);
            return ResponseFormatter.success(200, "Movie updated successfully", updated);
        } catch (Exception e) {
            return ResponseFormatter.error(400, "Failed to update movie", e.getMessage());
        }
    }

    // ✅ Only ADMIN or SUPER_ADMIN can delete movie
    @DELETE
    @Path("{id}")
    @RolesAllowed({
        SecurityRoles.ADMIN,
        SecurityRoles.SUPER_ADMIN
    })
    public Response delete(@PathParam("id") Long id) {
        Movie existing = movieBean.findMovie(id);
        if (existing == null)
            return ResponseFormatter.error(404, "Movie not found", null);

        try {
            movieBean.deleteMovie(id);
            return ResponseFormatter.success(200, "Movie deleted successfully", null);
        } catch (Exception e) {
            return ResponseFormatter.error(400, "Failed to delete movie", e.getMessage());
        }
    }
}