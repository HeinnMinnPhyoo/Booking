package com.bookingapp.user.service.payment;

public interface PaymentService {
    boolean addPaymentCard(String cardNumber, String expiryDate, String cvv, String userId);
    boolean paymentCharge(double amount, String paymentMethodId, String orderId);
}
