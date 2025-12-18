package Validator;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator("confirmPasswordValidator")
public class ConfirmPasswordValidator implements Validator<Object> {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException {

        String confirmPassword = (String) value;

        UIComponent newPasswordComponent
                = (UIComponent) component.getAttributes().get("newPasswordComponent");

        String newPassword = (String) ((jakarta.faces.component.UIInput) newPasswordComponent).getSubmittedValue();

        if (newPassword == null) {
            return;
        }

        if (!confirmPassword.equals(newPassword)) {
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Passwords do not match",
                    null
            ));
        }
    }
}
