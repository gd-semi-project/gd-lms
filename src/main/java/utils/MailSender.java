package utils;

import java.net.PasswordAuthentication;
import java.util.Properties;

import com.mysql.cj.protocol.Message;

import jakarta.mail.*;
import jakarta.mail.internet.*;

public class MailSender {

    // SMTP 설정 + 메일 전송 유틸
	// 설정부분 properties 분리 가능
    public static void sendMail(String toEmail, String subject, String htmlContent) {
        final String fromEmail = "goheekwon9991@gmail.com";      // 발신 이메일
        final String smtpPassword = System.getenv("SMTP_PASSWORD"); // 앱비밀번호 서버 환경변수로 관리

        if (smtpPassword != null) {
            System.out.println("환경변수 읽기 성공");
            
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");  // TLS 사용

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, smtpPassword);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(fromEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject(subject);
                message.setContent(htmlContent, "text/html; charset=UTF-8");

                Transport.send(message);
                System.out.println("메일 전송 완료");

            } catch (MessagingException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("환경변수 'SMTP_PASSWORD'가 설정되지 않음");
        }
    }
}

