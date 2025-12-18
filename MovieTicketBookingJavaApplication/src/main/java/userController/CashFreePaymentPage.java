/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package userController;

import cdi.UserAuthBean;
import entity.Booking;
import entity.Payment;
import entity.User;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Scanner;
import user_bean.BookingBeanLocal;
import user_bean.PaymentBeanLocal;
import user_bean.UserBeanLocal;

/**
 *
 * @author HP
 */
@Named("cashfreePaymentController")
@SessionScoped
public class CashFreePaymentPage implements Serializable {
//
//    private final String APP_ID = "";
//    private final String SECRET_KEY = "";
//    private final String CF_ORDER_TOKEN_URL = "";
//
//    // @Inject
//    // private MovieBookingController movieBookingController;
//    // @Inject
//    // private BookingService bookingService;
//    private String orderId;
//    private double amount;
//    private String cftoken;
//
//    @EJB
//    private UserBeanLocal userBean;
//    private User currentUser;
//
//    @Inject
//    private UserAuthBean userAuthBean;
//
//    @EJB
//    private PaymentBeanLocal paymentBean;
//
//    @EJB
//    private BookingBeanLocal bookingBean;
//
//    @PostConstruct
//    public void init() {
//        FacesContext fc = FacesContext.getCurrentInstance();
//
//        currentUser = userAuthBean.getLoggedUser();
//        if (currentUser == null) {
//            System.err.println("No logged-in user found!");
//        } else {
//            System.out.println("Logged-in user: " + currentUser.getEmail());
//        }
//    }
//
//    public String getOrderId() {
//        return orderId;
//    }
//
//    public double getAmount() {
//        return amount;
//    }
//
//    public String getCftoken() {
//        return cftoken;
//    }
//
//    public String preparePayment(String orderId, double amount) {
//        this.orderId = orderId;
//        this.amount = amount;
//        this.cftoken = generateOrderToken();
//        System.out.println("Generated CF Token: " + cftoken);
//
//        if (this.cftoken != null) {
//            return "movie_cashFreePaymentMethod?faces-redirect=true";
//
//        } else {
//            return "confirmBooking?faces-redirect=true&error=payment_failed";
//        }
//    }
//
//    private String generateOrderToken() {
//        try {
//            if (currentUser == null) {
//                return null;
//            }
//
//            // 🔥 FORCE DB REFRESH BEFORE PAYMENT
//            currentUser = userBean.findUser(currentUser.getUserId());
//
//            if (currentUser.getEmail() == null || currentUser.getPhoneno() == null) {
//                System.err.println("User details incomplete for payment");
//                return null;
//            }
//
//            String customerName = currentUser.getUsername();
//            String customerEmail = currentUser.getEmail();
//            String customerPhone = currentUser.getPhoneno().toString();
//
//            String jsonInputString = String.format(
//                    "{"
//                    + "\"order_id\":\"%s\","
//                    + "\"order_amount\":%.2f,"
//                    + "\"order_currency\":\"INR\","
//                    + "\"customer_details\":{"
//                    + "\"customer_id\":\"user_%s\","
//                    + "\"customer_phone\":\"%s\","
//                    + "\"customer_name\":\"%s\","
//                    + "\"customer_email\":\"%s\""
//                    + "},"
//                    + "\"order_meta\":{"
//                    + "\"return_url\":\"http://localhost:8080/MovieTicketBookingJavaApplication/PaymentCallBack.xhtml?order_id=%s&order_status={order_status}\","
//                    + "\"notify_url\":\"https://localhost:8080/MovieTicketBookingJavaApplication/WebHookServlet\""
//                    + "}"
//                    + "}",
//                    orderId,
//                    amount,
//                    customerPhone,
//                    customerPhone,
//                    customerName,
//                    customerEmail,
//                    orderId
//            );
//
//            URL url = new URL(CF_ORDER_TOKEN_URL);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "application/json");
//            conn.setRequestProperty("Accept", "application/json");
//            conn.setRequestProperty("x-client-id", APP_ID);
//            conn.setRequestProperty("x-client-secret", SECRET_KEY);
//            conn.setRequestProperty("x-api-version", "2023-08-01");
//
//            conn.setDoOutput(true);
//
//            try (OutputStream os = conn.getOutputStream()) {
//                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
//                os.write(input, 0, input.length);
//            }
//
//            int responseCode = conn.getResponseCode();
//            System.out.println("Cashfree Response Code: " + responseCode);
//
//            InputStream responseStream;
//
//            if (responseCode == HttpURLConnection.HTTP_OK
//                    || responseCode == HttpURLConnection.HTTP_CREATED) {
//
//                responseStream = conn.getInputStream();
//
//            } else {
//                responseStream = conn.getErrorStream();
//            }
//
//            if (responseStream == null) {
//                System.err.println("Cashfree returned empty response stream");
//                return null;
//            }
//
//            try (Scanner scanner = new Scanner(responseStream, StandardCharsets.UTF_8.name())) {
//                String response = scanner.useDelimiter("\\A").next();
//                System.out.println("Cashfree Raw Response: " + response);
//
//                if (responseCode == HttpURLConnection.HTTP_OK
//                        || responseCode == HttpURLConnection.HTTP_CREATED) {
//
//                    int start = response.indexOf("\"payment_session_id\":\"");
//                    if (start == -1) {
//                        System.err.println("payment_session_id not found");
//                        return null;
//                    }
//
//                    start += "\"payment_session_id\":\"".length();
//                    int end = response.indexOf("\"", start);
//
//                    return response.substring(start, end);
//
//                } else {
//                    System.err.println("Cashfree API Error Response");
//                    return null;
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//
//        }
//    }
//
//    public void setOrderId(String orderId) {
//        this.orderId = orderId;
//    }
//
//    public void setAmount(double amount) {
//        this.amount = amount;
//    }
//
//    public void setCftoken(String cftoken) {
//        this.cftoken = cftoken;
//    }
//
//    public void setCurrentUser(User currentUser) {
//        this.currentUser = currentUser;
//    }
//
//     public String startPayment(Long bookingId) {
//
//        Booking booking = bookingBean.findBooking(bookingId);
//        if (booking == null) {
//            return "confirmBooking.xhtml?faces-redirect=true&error=booking_not_found";
//        }
//        double amt = booking.getTotalAmount();
//
//        String orderId = "ORD_" + bookingId + "_" + System.currentTimeMillis();
//
//        // 🔹 CREATE PENDING PAYMENT RECORD
//        Payment payment = new Payment();
//        payment.setTransactionId(orderId);
//        payment.setBookingId(booking);
//        payment.setAmount((long) (amt * 100));
//        payment.setPaymentMethod("CASHFREE");
//        payment.setPaymentStatus("PENDING");
//        payment.setStatus("ACTIVE");
//        payment.setPaymentDate(new Date());
//        payment.setCreatedAt(new Date());
//        payment.setUpdatedAt(new Date());
//
//        paymentBean.createPayment(payment);
//
//        // 🔹 GENERATE CASHFREE ORDER TOKEN
//        this.orderId = orderId;
//        this.amount = amt;
//        this.cftoken = generateOrderToken();
//
//        if (cftoken != null) {
//            return "movie_cashFreePaymentMethod.xhtml?faces-redirect=true";
//        } else {
//            return "confirmBooking.xhtml?faces-redirect=true&error=payment_failed";
//        }
//
//    }
//
}
