/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package converter;

import entity.Seat;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import user_bean.SeatBeanLocal;

/**
 *
 * @author DELL
 */
@FacesConverter(value = "seatConverter")
public class SeatConverter implements Converter<Seat> {

    @Inject
    SeatBeanLocal seatBean;

    @Override
    public Seat getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            Long id = Long.valueOf(value);
            return seatBean.findSeat(id);
        } catch (NumberFormatException e) {
            // Handle error, though unlikely if used correctly with f:selectItems
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Seat value) {
        if (value == null) {
            return "";
        }
        // Return the primary key as a String
        return value.getSeatId() != null ? value.getSeatId().toString() : "";
    }

}
