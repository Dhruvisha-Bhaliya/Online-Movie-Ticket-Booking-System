/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package RestAPIStructure;

/**
 *
 * @author DELL
 */
public class ErrorResponse {

    private boolean status = false;
    private int status_code;
    private String message;
    private Object errors;

    public ErrorResponse() {
    }

    public ErrorResponse(int status_code, String message, Object errors) {
        this.status_code = status_code;
        this.message = message;
        this.errors = errors;
    }

    public boolean isStatus() {
        return status;
    }

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getErrors() {
        return errors;
    }

    public void setErrors(Object errors) {
        this.errors = errors;
    }
}
