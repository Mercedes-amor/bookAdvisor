package mag.ej05.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import mag.ej05.FormInfo;
import mag.ej05.domain.Genero;
import mag.ej05.domain.Libro;
import mag.ej05.services.EmailService;
import mag.ej05.services.LibrosService;

@Controller
public class PrincipalController {

    // Variable para almacenar errores
    String txtErr = null;

    @Autowired
    private EmailService emailService;

    @Autowired(required = true)
    LibrosService librosService;

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

    // CATÁLOGO LIBROS

    @GetMapping("/libros")
    public String showLibros(Model model) {

        model.addAttribute("listaLibros", librosService.getAll());
        return "bookListView";

    }

    // VISTA DETALLE LIBRO
    @GetMapping("/listone/{id}")
    public String getOne(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("libro", librosService.getOneById(id));

        } catch (RuntimeException e) {
            // Si se lanza la excepción guardamos el mensaje en la variable txtErr para
            // mostrarla
            txtErr = e.getMessage();
            return "redirect:/libros";
        }
        return "bookView";
    }

    // FORMULARIO AÑADIR LIBRO
    @GetMapping("/libros/addBook")
    public String showNewBook(
            Model model) {

        model.addAttribute("txtErr", txtErr);
        model.addAttribute("libro", new Libro());
        
        txtErr = null; //Reseteamos variable
        return "bookNewFormView";
    }

    @PostMapping("/libros/addBook/submit")
    public String showNewBookSubmit(
            @Valid Libro libro,
            BindingResult bindingResult,
            Model model) {
        // Para los errores que llegan por el @Valid
        if (bindingResult.hasErrors()) {
            txtErr = "No has completado todos los campos";
            return "redirect:/libros/addBook";
        }
        try {
            librosService.add(libro);
        } catch (RuntimeException e) {
            // Capturamos las excepciones que llegan del service
            txtErr = e.getMessage();
            return "redirect:/libros/addBook";
        }

        return "redirect:/libros";
    }

    // EDITAR LIBRO
    @GetMapping("/libros/edit/{id}")
    public String getEdit(
            @PathVariable long id,
            @RequestParam(required = false) Integer err,
            Model model) {

        // Para los errores genéricos que llegan por el @Valid
        // Como el formato del email o los campos vacíos.
        if (err != null) {
            model.addAttribute("text2Err", "Hubo un error en los datos actualizados");
        }

        Libro libroAEditar = librosService.getOneById(id);
        model.addAttribute("libroForm", libroAEditar);
        return "bookEditFormView";
    }

    @PostMapping("/libros/edit/submit")
    public String getEditSubmit(
            @Valid Libro libroForm,
            BindingResult bindingResult) {
        // Para los errores que llegan por el @Valid
        if (bindingResult.hasErrors()) {
            return "redirect:/edit/submit?err=1";
        }
        try {
            librosService.editBook(libroForm);
        } catch (RuntimeException e) {
            // Capturamos las excepciones que llegan del service
            txtErr = e.getMessage();
            return "redirect:/libros/edit/submit";
        }

        return "redirect:/libros";
    }

    // BORRAR LIBRO
    @GetMapping("/libros/delete/{id}")
    public String showDelete(@PathVariable Long id) {
        librosService.deleteById(id);
        return "redirect:/libros";
    }

    // BUSCADOR

    @GetMapping("/findByTitle")
    public String showFindByTitle() {
        return "bookListView";
    }

    @PostMapping("/findByTitle")
    public String showFindByNameSubmit(
            @RequestParam("busqueda") String busqueda,
            Model model) {
        // Creamos el arrayList con los empleados encontrados
        List<Libro> librosEncontrados = librosService.findByTitle(busqueda);

        // Pasamos los empleados encontrados a la vista
        model.addAttribute("listaLibros", librosEncontrados);

        // Mantenemos el valor de 'busqueda' ingresado para mostrarlo en el campo
        // del formulario
        model.addAttribute("busqueda", busqueda);

        return "bookListView";
    }

    // BÚSQUEDA POR GÉNERO
    @GetMapping("/findByGenero/{genero}")
    public String showFindByGenero(
            @PathVariable Genero genero,
            Model model) {
        // Llamamos a la función buscarPorGénero y mostramos el resultado
        model.addAttribute("listaLibros", librosService.findByGenero(genero));
        // Pasamos a la vista la opción seleccionada
        model.addAttribute("generoSeleccionado", genero);
        return "bookListView";
    }

}
