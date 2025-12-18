/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package userController;

import entity.Payment;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import user_bean.PaymentBeanLocal;

/**
 *
 * @author HP
 */
@Named
@RequestScoped
public class PaymentCallBackController {

    private String orderId;
    private String orderStatusParam;
    private String finalVerificationStatus;
    
    private String cfOrderStatus;
    
    @EJB
    private PaymentBeanLocal paymentBean;

    public String getCfOrderStatus() {
        return cfOrderStatus;
    }

    public void setCfOrderStatus(String cfOrderStatus) {
        this.cfOrderStatus = cfOrderStatus;
    }
    
    

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatusParam() {
        return orderStatusParam;
    }

    public void setOrderStatusParam(String orderStatusParam) {
        this.orderStatusParam = orderStatusParam;
    }

    public String getFinalVerificationStatus() {
        return finalVerificationStatus;
    }

   public void verifyPayment() {

    if (orderId == null) {
        finalVerificationStatus = "PENDING";
        return;
    }

    Payment payment = paymentBean.findByTransctionId(orderId);

    if (payment == null) {
        finalVerificationStatus = "PENDING";
        return;
    }

    System.out.println("🔍 DB payment status = " + payment.getPaymentStatus());

    if ("PAID".equalsIgnoreCase(payment.getPaymentStatus())) {
        finalVerificationStatus = "SUCCESS";
    } else if ("FAILED".equalsIgnoreCase(payment.getPaymentStatus())) {
        finalVerificationStatus = "FAILURE";
    } else {
        finalVerificationStatus = "PENDING";
    }
}


}
