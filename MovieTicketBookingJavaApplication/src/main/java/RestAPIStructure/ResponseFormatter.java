/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package RestAPIStructure;

import jakarta.ws.rs.core.Response;

/**
 *
 * @author DELL
 */
public class ResponseFormatter {

    public static <T> Response success(int statusCode, String message, T data) {
        ApiResponse<T> res = new ApiResponse<>(true, statusCode, message, data);
        return Response.status(statusCode).entity(res).build();
    }

    public static Response error(int statusCode, String message, Object errors) {
        ErrorResponse err = new ErrorResponse(statusCode, message, errors);
        return Response.status(statusCode).entity(err).build();
    }
}
