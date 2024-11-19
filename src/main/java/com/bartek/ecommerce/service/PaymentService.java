package com.bartek.ecommerce.service;

import com.bartek.ecommerce.dto.PaymentDetailsDto;
import com.bartek.ecommerce.entity.Order;
import com.bartek.ecommerce.entity.Payment;

public interface PaymentService {
    Payment processPayment(Order order, PaymentDetailsDto paymentDetailsDto);
}
