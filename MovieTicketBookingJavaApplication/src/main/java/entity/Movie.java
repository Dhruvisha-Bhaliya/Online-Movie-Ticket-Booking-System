/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author DELL
 */
@Entity
@Table(name = "movie")
@NamedQueries({
    @NamedQuery(name = "Movie.findAll", query = "SELECT m FROM Movie m"),
    @NamedQuery(name = "Movie.findByMovieId", query = "SELECT m FROM Movie m WHERE m.movieId = :movieId"),
    @NamedQuery(name = "Movie.findByMovieTitle", query = "SELECT m FROM Movie m WHERE m.movieTitle = :movieTitle"),
    @NamedQuery(name = "Movie.findByDurationMin", query = "SELECT m FROM Movie m WHERE m.durationMin = :durationMin"),
    @NamedQuery(name = "Movie.findByGenre", query = "SELECT m FROM Movie m WHERE m.genre = :genre"),
    @NamedQuery(name = "Movie.findByTicketPrice", query = "SELECT m FROM Movie m WHERE m.ticketPrice = :ticketPrice"),
    @NamedQuery(name = "Movie.findByPosterImagePath", query = "SELECT m FROM Movie m WHERE m.posterImagePath = :posterImagePath"),
    @NamedQuery(name = "Movie.findByAvgRating", query = "SELECT m FROM Movie m WHERE m.avgRating = :avgRating"),
    @NamedQuery(name = "Movie.findByVoteCount", query = "SELECT m FROM Movie m WHERE m.voteCount = :voteCount"),
    @NamedQuery(name = "Movie.findByLanguages", query = "SELECT m FROM Movie m WHERE m.languages = :languages"),
    @NamedQuery(name = "Movie.findBySubGenres", query = "SELECT m FROM Movie m WHERE m.subGenres = :subGenres"),
    @NamedQuery(name = "Movie.findByCreatedAt", query = "SELECT m FROM Movie m WHERE m.createdAt = :createdAt"),
    @NamedQuery(name = "Movie.findByUpdatedAt", query = "SELECT m FROM Movie m WHERE m.updatedAt = :updatedAt"),
    @NamedQuery(name = "Movie.findByStatus", query = "SELECT m FROM Movie m WHERE m.status = :status")})
public class Movie implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "movie_id")
    @JsonbTransient
    private Long movieId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "movie_title")
    private String movieTitle;
    @Basic(optional = false)
    @NotNull
    @Column(name = "duration_min")
    private int durationMin;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "genre")
    private String genre;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ticket_price")
    private long ticketPrice;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "poster_image_path")
    private String posterImagePath;
    @Basic(optional = false)
    @NotNull
    @Column(name = "avg_rating")
    private float avgRating;
    @Basic(optional = false)
    @NotNull
    @Column(name = "vote_count")
    private int voteCount;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "languages")
    private String languages;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "sub_genres")
    private String subGenres;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "cast_crew")
    private String castCrew;
    @Basic(optional = false)
    @NotNull
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Basic(optional = false)
    @NotNull
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Size(max = 7)
    @Column(name = "status")
    private String status;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "movieId")
    @JsonbTransient
    private Collection<Feedback> feedbackCollection;
    @OneToMany(mappedBy = "movieId")
    @JsonbTransient
    private Collection<Showmovie> showmovieCollection;

    public Movie() {
    }

    public Movie(Long movieId) {
        this.movieId = movieId;
    }

    public Movie(Long movieId, String movieTitle, int durationMin, String genre, String description, long ticketPrice, String posterImagePath, float avgRating, int voteCount, String languages, String subGenres, String castCrew, Date createdAt, Date updatedAt) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.durationMin = durationMin;
        this.genre = genre;
        this.description = description;
        this.ticketPrice = ticketPrice;
        this.posterImagePath = posterImagePath;
        this.avgRating = avgRating;
        this.voteCount = voteCount;
        this.languages = languages;
        this.subGenres = subGenres;
        this.castCrew = castCrew;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public int getDurationMin() {
        return durationMin;
    }

    public void setDurationMin(int durationMin) {
        this.durationMin = durationMin;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(long ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public String getPosterImagePath() {
        return posterImagePath;
    }

    public void setPosterImagePath(String posterImagePath) {
        this.posterImagePath = posterImagePath;
    }

    public float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(float avgRating) {
        this.avgRating = avgRating;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getSubGenres() {
        return subGenres;
    }

    public void setSubGenres(String subGenres) {
        this.subGenres = subGenres;
    }

    public String getCastCrew() {
        return castCrew;
    }

    public void setCastCrew(String castCrew) {
        this.castCrew = castCrew;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Collection<Feedback> getFeedbackCollection() {
        return feedbackCollection;
    }

    public void setFeedbackCollection(Collection<Feedback> feedbackCollection) {
        this.feedbackCollection = feedbackCollection;
    }

    public Collection<Showmovie> getShowmovieCollection() {
        return showmovieCollection;
    }

    public void setShowmovieCollection(Collection<Showmovie> showmovieCollection) {
        this.showmovieCollection = showmovieCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (movieId != null ? movieId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Movie)) {
            return false;
        }
        Movie other = (Movie) object;
        if ((this.movieId == null && other.movieId != null) || (this.movieId != null && !this.movieId.equals(other.movieId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Movie[ movieId=" + movieId + " ]";
    }

}
