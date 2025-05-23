package mag.ej05.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import mag.ej05.domain.Rol;
import mag.ej05.domain.Usuario;
import mag.ej05.services.LibrosService;
import mag.ej05.services.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    // Variable para almacenar errores
    String txtErr = null;

    @Autowired(required = true)
    UsuarioService usuarioService;

    @Autowired(required = true)
    LibrosService librosService;

    // USUARIOS

    @GetMapping("/")
    public String showLibros(Model model) {

        model.addAttribute("listaUsuarios", usuarioService.getAll());
        return "usuario/usuarioListView";

    }

    // FORMULARIO AÑADIR USUARIO (POR ADMIN)
    @GetMapping("/addUser")
    public String showNewUser(
            Model model) {

        model.addAttribute("txtErr", txtErr);
        model.addAttribute("usuario", new Usuario());

        txtErr = null; // Reseteamos variable
        return "usuario/usuarioNewFormView";
    }

    @PostMapping("/addUser/submit")
    public String showNewUserSubmit(
            @Valid Usuario usuario,
            BindingResult bindingResult,
            Model model) {
        // Para los errores que llegan por el @Valid
        if (bindingResult.hasErrors()) {
            txtErr = "No has completado todos los campos";
            return "redirect:/usuarios/addUser";
        }

        try {
            usuarioService.add(usuario);
        } catch (RuntimeException e) {
            // Capturamos las excepciones que llegan del service
            txtErr = e.getMessage();
            return "redirect:/usuarios/addUser";
        }

        return "redirect:/usuarios/";
    }

    //REGISTRARSE, USUARIOS GENÉRICOS
        @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario/registroFormView";
    }

    @PostMapping("/registro/submit")
    public String procesarFormularioRegistro(@ModelAttribute("usuario") Usuario usuario) {

        // Asignamos el rol USER y lo activamos
        usuario.setRol(Rol.USER);

        usuarioService.add(usuario);

        return "redirect:/public/signin/";
    }

    // EDITAR USUARIO
    @GetMapping("/edit/{id}")
    public String getEdit(
            @PathVariable long id,
            @RequestParam(required = false) Integer err,
            Model model) {

        // Para los errores genéricos que llegan por el @Valid
        // Como el formato del email o los campos vacíos.
        if (err != null) {
            model.addAttribute("text2Err", "Hubo un error en los datos actualizados");
        }

        Usuario usuarioAEditar = usuarioService.getOneById(id);
        model.addAttribute("usuarioForm", usuarioAEditar);
        return "usuario/usuarioEditFormView";
    }
    
    @PostMapping("/edit/submit")
    public String getEditSubmit(
            @Valid Usuario usuarioForm,
            BindingResult bindingResult,

            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("text2Err", "Hubo un error en los datos actualizados");
            return "usuario/usuarioEditFormView";
        }
        usuarioService.editar(usuarioForm);
        return "redirect:/usuarios/";
    }

    
    // EDITAR USUARIO PASSWORD
    @GetMapping("/edit/pass/{id}")
    public String getEditPass(
            @PathVariable long id,
            @RequestParam(required = false) Integer err,
            Model model) {

        // Para los errores genéricos que llegan por el @Valid
        // Como el formato del email o los campos vacíos.
        if (err != null) {
            model.addAttribute("text2Err", "Hubo un error en los datos actualizados");
        }

        Usuario usuarioAEditar = usuarioService.getOneById(id);
        model.addAttribute("usuarioForm", usuarioAEditar);
        return "usuario/usuarioEditPassView";
    }
    
    @PostMapping("/edit/pass/submit")
    public String getEditPassSubmit(
            @Valid Usuario usuarioForm,
            BindingResult bindingResult,

            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("text2Err", "Hubo un error en los datos actualizados");
            return "usuario/usuarioEditFormView";
        }
        usuarioService.editar(usuarioForm);
        return "redirect:/usuarios/";
    }

    // BORRAR USUARIO
    @GetMapping("/delete/{id}")
    public String showDelete(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return "redirect:/usuarios/";
    }



}
