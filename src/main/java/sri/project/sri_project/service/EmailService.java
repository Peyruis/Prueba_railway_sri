package sri.project.sri_project.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void enviarCorreoBienvenida(String emailDestino, String nombre) throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("julcortezm.jys@gmail.com");
        message.setTo(emailDestino);
        message.setSubject("¡Bienvenido al Sistema de Riego Inteligente!");
        message.setText("Hola " + nombre + ",\n\n" +
                "Felicidades por estar en nuestro Sistema de Riego Inteligente (SRI).\n" +
                "Estamos emocionados de tenerte a bordo para optimizar el riego de tus cultivos con IoT.\n\n" +
                "Atentamente,\nEl Equipo de SRI");
        emailSender.send(message);
    }

    public void enviarReportePorCorreoConAdjunto(String emailDestino, String tituloReporte, byte[] pdfContent) throws Exception {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("julcortezm.jys@gmail.com");
        helper.setTo(emailDestino);
        helper.setSubject("Nuevo reporte generado: " + tituloReporte);
        helper.setText("Hola,\n\n" +
                "Adjunto encontrarás el reporte que solicitaste: " + tituloReporte + ".\n" +
                "Puedes acceder a más detalles dentro de la plataforma del Sistema de Riego Inteligente.\n\n" +
                "Atentamente,\nEl Equipo de SRI");

        // Adjuntar el PDF
        helper.addAttachment("Reporte_Sistema_Riego.pdf", new ByteArrayResource(pdfContent));

        emailSender.send(message);
    }
}
