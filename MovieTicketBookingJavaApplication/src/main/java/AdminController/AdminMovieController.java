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
            if (uploadedFile == null || uploadedFile.getSize() == 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Upload Error", "Please select a poster image."));
                return null;
            }
            String fileName = uploadedFile.getSubmittedFileName();
            String relativeWebPath = fileName;
            String contextPath = FacesContext.getCurrentInstance().getExternalContext()
                    .getRealPath("");
            if (contextPath == null) {
                throw new IOException("Server cannot resolve the web content path for uploads.");
            }
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
            movie = new Movie();
            uploadedFile = null;
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
            if (uploadedFile != null && uploadedFile.getSize() > 0) {
                String fileName = uploadedFile.getSubmittedFileName();
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

                movie.setPosterImagePath(fileName);
            }

            movieBean.updateMovie(movie);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Movie updated successfully!"));

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
