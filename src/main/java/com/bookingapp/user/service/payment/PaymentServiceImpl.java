package com.bookingapp.user.service.payment;

public class PaymentServiceImpl implements PaymentService {

    @Override
    public boolean addPaymentCard(String cardNumber, String expiryDate, String cvv, String userId) {
        // In a real application, you would interact with a payment gateway here.
        // For this mock implementation, we simply return true.
        System.out.println("Mock: Adding payment card for user " + userId);
        return true; // Simulate success
    }

    @Override
    public boolean paymentCharge(double amount, String paymentMethodId, String orderId) {
        // In a real application, you would interact with a payment gateway to charge the user.
        // For this mock implementation, we simply return true.
        System.out.println("Mock: Charging payment method " + paymentMethodId + " for order " + orderId + ", amount: " + amount);
        return true; // Simulate success
    }

}
