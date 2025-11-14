/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package converter;

import entity.Theater;
import jakarta.ejb.EJB;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import user_bean.TheaterBeanLocal;

/**
 *
 * @author DELL
 */
//@FacesConverter(value="theaterConverter", managed=true)
//@RequestScoped
@jakarta.inject.Named("theaterConverter")
public class TheaterConverter implements Converter<Theater> {

    @EJB
    private TheaterBeanLocal theaterBean;

    // If itemValue is #{t} (a Theater object), getAsString() converts it to the ID string.
    @Override
    public String getAsString(FacesContext context, UIComponent component, Theater theater) {
        if (theater == null) {
            return "";
        }
        return theater.getTheaterId().toString(); // Returns the ID as a string
    }

// If form submits the ID string, getAsObject() converts it back to the Theater object.
    @Override
    public Theater getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return theaterBean.find(Long.parseLong(value)); // Finds the object by ID
    }
}
