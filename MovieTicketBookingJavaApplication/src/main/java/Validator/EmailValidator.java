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
@FacesValidator("emailValidator")
public class EmailValidator implements Validator<String> {

    @Override
    public void validate(FacesContext context, UIComponent component, String value)
            throws ValidatorException {

        if (value == null || value.isEmpty()) {
            return;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        if (!value.matches(emailRegex)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Invalid email format", null));
        }
        String blockedPattern = ".*\\.(sadmin|admin|manager|staff)@cinemaxhub\\.com$";

        if (value.toLowerCase().matches(blockedPattern)) {
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "This type of email is not allowed for registration.",
                    null
            ));
        }
    }
}
