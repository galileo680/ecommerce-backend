package com.bartek.ecommerce.service.Impl;

import com.bartek.ecommerce.dto.AddressDto;
import com.bartek.ecommerce.dto.OrderItemDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine emailTemplateEngine;


    public void sendAccountActivationEmail(
            String to,
            String name,
            String activationLink
    ) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("activationLink", activationLink);

        String htmlContent = emailTemplateEngine.process("email/confirm_account", context);
        sendHtmlMail(to, "Activate your account", htmlContent);
    }

    public void sendOrderConfirmationEmail(
            String to,
            String name,
            Long orderId,
            List<OrderItemDto> orderItems,
            BigDecimal totalAmount,
            AddressDto shippingAddress
    ) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("orderId", orderId);
        context.setVariable("orderItems", orderItems);
        context.setVariable("totalAmount", totalAmount);
        context.setVariable("shippingAddress", shippingAddress);

        String htmlContent = emailTemplateEngine.process("email/order_confirmation", context);
        sendHtmlMail(to, "Order Confirmation #" + orderId, htmlContent);
    }

    private void sendHtmlMail(String to, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
