/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AdminController;

import entity.Movie;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import user_bean.MovieBeanLocal;

/**
 *
 * @author DELL
 */
@Named(value = "adminMovieController")
@SessionScoped
public class AdminMovieController implements Serializable {

    @EJB
    private MovieBeanLocal movieBean;
    private Part uploadedFile;

    private Movie movie = new Movie();
    private List<Movie> movieList;

    public List<Movie> getMovieList() {
        movieList = movieBean.getAllMovies();
        return movieList;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public String addMovie() {
        try {
            // --- 1. Validate File and Prevent NPE ---
            if (uploadedFile == null || uploadedFile.getSize() == 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Upload Error", "Please select a poster image."));
                return null;
            }

            // --- 2. Define Paths ---
            String fileName = uploadedFile.getSubmittedFileName();

            // **A. The Relative Path for the Browser/Database (Correct format)**
            String relativeWebPath = fileName;

            // **B. The Absolute Path for the Server's File System**
            // Use getRealPath() as the primary location, but be aware of its unreliability.
            String contextPath = FacesContext.getCurrentInstance().getExternalContext()
                    .getRealPath("");

            // Fallback or Error Check for getRealPath() failure
            if (contextPath == null) {
                // If the server can't map the path, we must use a fixed temporary location.
                // For now, re-throwing a fatal error as the image must be publicly accessible.
                throw new IOException("Server cannot resolve the web content path for uploads.");
            }

            // --- 3. Save File to Server Path ---
            File savedir = new File(contextPath + File.separator + "resources" + File.separator + "images");
            if (!savedir.exists()) {
                if (!savedir.mkdirs()) {
                    throw new IOException("Failed to create directory structure");

                }
            }

            File targetFile = new File(savedir, fileName);
            try (java.io.InputStream input = uploadedFile.getInputStream(); java.io.OutputStream output = new java.io.FileOutputStream(targetFile)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = input.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
            }

            movie.setPosterImagePath(relativeWebPath);

            movieBean.addMovie(movie);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Movie added Successfully!"));

            // 6. Reset Bean State for new entry/form clearance
            movie = new Movie();
            uploadedFile = null;

            // 7. Redirect to the movie list page
            return "admin_movies.xhtml?faces-redirect=true";

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_FATAL, "System Error",
                            "Failed to add movie: " + e.getMessage()));
            return null;
        }

    }

    public String editMovie(Movie m) {
        this.movie = m;
        return "edit_movie.xhtml?faces-redirect=true";
    }

    public String updateMovie() {
        try {
            // If a new file is uploaded, save it and update the image path
            if (uploadedFile != null && uploadedFile.getSize() > 0) {
                String fileName = uploadedFile.getSubmittedFileName();

                // Get the server path
                String contextPath = FacesContext.getCurrentInstance()
                        .getExternalContext().getRealPath("");

                File saveDir = new File(contextPath + File.separator + "resources" + File.separator + "images");
                if (!saveDir.exists()) {
                    saveDir.mkdirs();
                }

                File targetFile = new File(saveDir, fileName);
                try (java.io.InputStream input = uploadedFile.getInputStream(); java.io.OutputStream output = new java.io.FileOutputStream(targetFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = input.read(buffer)) > 0) {
                        output.write(buffer, 0, length);
                    }
                }

                // Update the movie's poster path
                movie.setPosterImagePath(fileName);
            }

            // Update the database record
            movieBean.updateMovie(movie);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Movie updated successfully!"));

            // Reset state and redirect back
            movie = new Movie();
            uploadedFile = null;
            return "admin_movies.xhtml?faces-redirect=true";

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Update Error", e.getMessage()));
            e.printStackTrace();
            return null;
        }
    }

    public void deleteMovie(Long movieId) {
        try {
            movieBean.deleteMovie(movieId);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Deleted", "Movie deleted successfully."));
            movieList = movieBean.getAllMovies(); // refresh table instantly
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Delete Error", e.getMessage()));
        }
    }

    public Part getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(Part uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

}
