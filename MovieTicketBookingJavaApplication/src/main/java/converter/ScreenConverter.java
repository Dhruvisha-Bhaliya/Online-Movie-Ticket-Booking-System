/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package converter;

import entity.Screen;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import user_bean.ScreenBeanLocal;

/**
 *
 * @author DELL
 */
@jakarta.inject.Named("screenConverter")
@RequestScoped
public class ScreenConverter implements Converter<Screen>{
	@EJB
    // Ensure you have this ScreenBeanLocal interface and implementation
    private ScreenBeanLocal screenBean; 

    @Override
    public Screen getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            Long id = Long.parseLong(value);
            return screenBean.findScreen(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value for Screen ID: " + value, e);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Screen screen) {
        if (screen == null || screen.getScreenId() == null) return "";
        return screen.getScreenId().toString(); 
    }
    
}
