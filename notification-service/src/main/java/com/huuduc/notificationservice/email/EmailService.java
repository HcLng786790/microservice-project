package com.huuduc.notificationservice.email;

import com.huuduc.notificationservice.dto.PurchaseProduct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.huuduc.notificationservice.email.EmailTemplates.ORDER_CONFIRMATION;
import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Transactional
    @Async
    public void sendOrderConfirmationEmail(
            String username,
            BigDecimal totalPrice,
            String orderNumber,
            List<PurchaseProduct> products
    ) throws MessagingException {

        // Giả định email test
        String testEmail ="user@gmail.com";

        // Tạo MimeMessage để gửi email
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, UTF_8.name());

        // Thiết lập email người gửi
        messageHelper.setFrom("huuduc@gmail.com");

        // Tên của template dùng cho email xác nhận đơn hàng
        final String templateName = ORDER_CONFIRMATION.getTemplate();

        // Tạo map để chứa các biến trong email template
        Map<String, Object> variables = new HashMap<>();
        variables.put("username", username);
        variables.put("totalPrice", totalPrice);
        variables.put("orderNumber", orderNumber);
        variables.put("products", products);

        // Thiết lập context cho email template, chứa các biến cần thiết
        Context context = new Context();
        context.setVariables(variables);

        // Thiết lập tiêu đề cho email
        messageHelper.setSubject(ORDER_CONFIRMATION.getSubject());

        try {
            // Tạo nội dung email từ template và biến đã thiết lập
            String htmlTemplate = templateEngine.process(templateName, context);
            messageHelper.setText(htmlTemplate, true);

            // Thiết lập địa chỉ email nhận
            messageHelper.setTo(testEmail);

            // Gửi email
            javaMailSender.send(mimeMessage);
            log.info("INFO - Email successfully sent to {} with template {} ", testEmail, templateName);

        } catch (MessagingException e) {
            log.warn("WARNING - Cannot send Email to {} ", testEmail);
        }

    }
}
