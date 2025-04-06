package mag.ej05.controllers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import mag.ej05.FormInfo;
import mag.ej05.domain.Usuario;
import mag.ej05.services.EmailService;
import mag.ej05.services.LibrosService;
import mag.ej05.services.UsuarioService;
import mag.ej05.services.ValoracionService;

@Controller
@RequestMapping("/public")
public class PrincipalController {

    // Variable para almacenar errores
    String txtErr = null;

    @Autowired
    private EmailService emailService;

    // Servicio de almacenamiento ficheros

    @Autowired(required = true)
    LibrosService librosService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired(required = true)
    ValoracionService valoracionService;

    @GetMapping("/")
    //Modificamos esta ruta para obtener el id del usuario logeado para ir
    //a editar su perfil específico,
    public String showHome(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("year", LocalDate.now().getYear());
        //Obtenemos el usuario logeado si lo hay
        if(userDetails != null){
        Usuario usuario = usuarioService.findByNombre(userDetails.getUsername());
        //pasamos el id del usuario a la vista
        model.addAttribute("usuarioLogueadoId", usuario.getId());
        }

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

       // RUTA ERROR
       @GetMapping("/accessError")
       public String showErr() {
   
           return "/usuario/errorView";
       }
   
       // LOGIN
       @GetMapping("/signin")
       public String showLogin() {
           return "/usuario/signinView";
       }
       //LOGOUT
       @GetMapping("/signout")
       public String showLogout() {
           return "/usuario/signOutView";
       }
    

}
