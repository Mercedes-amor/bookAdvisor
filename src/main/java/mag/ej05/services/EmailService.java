package mag.ej05.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import mag.ej05.FormInfo;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public EmailService(JavaMailSender mailSender) {
        this.javaMailSender = mailSender;
    }

    public boolean sendEmail(FormInfo formInfo, String destination,
            String cuerpoMensaje) {

        try {
            MimeMessage mensaje = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);
            helper.setTo(destination);
            helper.setSubject("Nuevo formulario enviado de " + formInfo.getNombre());
            helper.setText(cuerpoMensaje);
            helper.setFrom(destination);

            // Adjuntar el archivo
            // if (!formInfo.getFichero().isEmpty()) {
            //     helper.addAttachment(formInfo.getFichero().getOriginalFilename(), formInfo.getFichero());
            // }

            javaMailSender.send(mensaje);
            System.out.println("email enviado correctamente");

            return true;
        }

        catch (MessagingException e) {
            System.out.println("Error al enviar el correo: " + e.getMessage());

            e.printStackTrace();
            return false;
        }
    }
}