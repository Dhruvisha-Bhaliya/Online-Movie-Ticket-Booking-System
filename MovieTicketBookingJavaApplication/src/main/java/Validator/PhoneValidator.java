/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Validator;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

/**
 *
 * @author DELL
 */
@FacesValidator("phoneValidator")
public class PhoneValidator implements Validator<Object> {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException {

        if (value == null) {
            return;
        }

        // The value is a Long (because your bean property is Long)
        Long phoneLong = (Long) value;

        // Convert to string for regex checking
        String phone = String.valueOf(phoneLong);

        // Validate phone format (10 digits)
        if (!phone.matches("^[0-9]{10}$")) {
            throw new ValidatorException(
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Phone number must be 10 digits", null)
            );
        }
    }
}
