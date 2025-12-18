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
@FacesValidator("passwordValidator")
public class PasswordValidator implements Validator<String> {

    @Override
    public void validate(FacesContext context, UIComponent component, String password)
            throws ValidatorException {
        if (password == null || password.isEmpty()) {
            return;
        }
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[@$!%*?&#].*");
        if (password.length() < 6) {
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Weak Password: must be at least 6 characters",
                    null));
        }
        int score = 0;
        if (hasUpper) {
            score++;
        }
        if (hasLower) {
            score++;
        }
        if (hasDigit) {
            score++;
        }
        if (hasSpecial) {
            score++;
        }

        if (score <= 2) {
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    "Medium Password: Add more character types for strong password",
                    null));
        }
    }
}
