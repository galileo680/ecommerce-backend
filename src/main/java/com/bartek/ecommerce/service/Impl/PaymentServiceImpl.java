package com.bartek.ecommerce.service.Impl;

import com.bartek.ecommerce.dto.PaymentDetailsDto;
import com.bartek.ecommerce.entity.Order;
import com.bartek.ecommerce.entity.Payment;
import com.bartek.ecommerce.enums.PaymentStatus;
import com.bartek.ecommerce.service.PaymentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Override
    public Payment processPayment(Order order, PaymentDetailsDto paymentDetails) {

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod(paymentDetails.getPaymentMethod());
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setTransactionId("TXN123456789");

        return payment;
    }
}
