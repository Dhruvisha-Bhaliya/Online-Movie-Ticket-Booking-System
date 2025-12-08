/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

/**
 *
 * @author HP
 */
@Entity
@Table(name = "seat_category")
@NamedQueries({
    @NamedQuery(name = "SeatCategory.findAll", query = "SELECT s FROM SeatCategory s"),
    @NamedQuery(name = "SeatCategory.findByCategoryId", query = "SELECT s FROM SeatCategory s WHERE s.categoryId = :categoryId"),
    @NamedQuery(name = "SeatCategory.findByCategoryName", query = "SELECT s FROM SeatCategory s WHERE s.categoryName = :categoryName"),
    @NamedQuery(name = "SeatCategory.findByPrice", query = "SELECT s FROM SeatCategory s WHERE s.price = :price"),
    @NamedQuery(name = "SeatCategory.findByRowStart", query = "SELECT s FROM SeatCategory s WHERE s.rowStart = :rowStart"),
    @NamedQuery(name = "SeatCategory.findByRowEnd", query = "SELECT s FROM SeatCategory s WHERE s.rowEnd = :rowEnd"),
    @NamedQuery(name = "SeatCategory.findByStatus", query = "SELECT s FROM SeatCategory s WHERE s.status = :status")})
public class SeatCategory implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "category_id")
    private Long categoryId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "category_name")
    private String categoryName;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "price")
    private BigInteger price;
    @Size(max = 5)
    @Column(name = "row_start")
    private String rowStart;
    @Size(max = 5)
    @Column(name = "row_end")
    private String rowEnd;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "status")
    private String status;
    @OneToMany(mappedBy = "categoryId")
    private Collection<Seat> seatCollection;
    @JoinColumn(name = "screen_id", referencedColumnName = "screen_id")
    @ManyToOne(optional = false)
    private Screen screenId;

    public SeatCategory() {
    }

    public SeatCategory(Long categoryId) {
        this.categoryId = categoryId;
    }

    public SeatCategory(Long categoryId, String categoryName, BigInteger price, String status) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.price = price;
        this.status = status;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigInteger getPrice() {
        return price;
    }

    public void setPrice(BigInteger price) {
        this.price = price;
    }

    public String getRowStart() {
        return rowStart;
    }

    public void setRowStart(String rowStart) {
        this.rowStart = rowStart;
    }

    public String getRowEnd() {
        return rowEnd;
    }

    public void setRowEnd(String rowEnd) {
        this.rowEnd = rowEnd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Collection<Seat> getSeatCollection() {
        return seatCollection;
    }

    public void setSeatCollection(Collection<Seat> seatCollection) {
        this.seatCollection = seatCollection;
    }

    public Screen getScreenId() {
        return screenId;
    }

    public void setScreenId(Screen screenId) {
        this.screenId = screenId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (categoryId != null ? categoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SeatCategory)) {
            return false;
        }
        SeatCategory other = (SeatCategory) object;
        if ((this.categoryId == null && other.categoryId != null) || (this.categoryId != null && !this.categoryId.equals(other.categoryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.SeatCategory[ categoryId=" + categoryId + " ]";
    }
    
}
