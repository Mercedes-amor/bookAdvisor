package mag.ej05.controllers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import mag.ej05.FormInfo;
import mag.ej05.services.EmailService;

@Controller
public class PrincipalController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/")
    public String showHome(Model model) {
        model.addAttribute("year", LocalDate.now().getYear());

        return "indexView";
    }

    @GetMapping("/quienesSomos")
    public String showQuienesSomos(Model model) {
        model.addAttribute("year", LocalDate.now().getYear());

        return "quienesSomosView";
    }

    // Rutas del formulario
    @GetMapping("/contacto")
    public String showContacto(Model model) {

        model.addAttribute("formInfo", new FormInfo());

        return "contactoView";
    }

    @PostMapping("/contacto")

    public String showFormInfo(

            @Valid @ModelAttribute("formInfo") FormInfo formInfo,
            BindingResult bindingResult,
            Model model) {

        // Si hay errores en el formulario mostramos nuevamente la vista
        // con los mensajes de error
        if (bindingResult.hasErrors()) {
            return "contactoView";
        }

        try {

            String cuerpoMensaje = String.format(
                    "Se ha recibido un nuevo formulario:\n\nNombre: %s\nEmail: %s\nComentario: %s\nTipo Solicitud: %s",
                    formInfo.getNombre(), formInfo.getEmail(), formInfo.getComentario(), formInfo.getTipoContacto());

            // Enviar el correo
            emailService.sendEmail(formInfo, "gerenteprueba2024@gmail.com", cuerpoMensaje);

        } catch (Exception e) {
            model.addAttribute("mensaje", "Error al procesar el formulario: " + e.getMessage());
        }

        // Mensaje de formulario enviado correctamente
        model.addAttribute("formularioEnviado", "Formulario enviado correctamente");
        System.out.println("Formulario enviado: ");

        return "contactoView";
    }

}
