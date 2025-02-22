package mag.ej05.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import mag.ej05.FormInfo;
import mag.ej05.domain.Genero;
import mag.ej05.domain.Libro;
import mag.ej05.domain.Usuario;
import mag.ej05.domain.Valoracion;
import mag.ej05.services.EmailService;
import mag.ej05.services.FileStorageService;
import mag.ej05.services.LibrosService;
import mag.ej05.services.UsuarioService;
import mag.ej05.services.ValoracionService;

@Controller
public class PrincipalController {

    // Variable para almacenar errores
    String txtErr = null;

    @Autowired
    private EmailService emailService;

    // Servicio de almacenamiento ficheros
    @Autowired
    private FileStorageService fileStorageService;

    @Autowired(required = true)
    LibrosService librosService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired(required = true)
    ValoracionService valoracionService;

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

        model.addAttribute("listaLibros", librosService.getAllDTO());
        return "libro/bookListView";

    }

    // VISTA DETALLE LIBRO
    @GetMapping("/listone/{id}")
    public String getOne(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("libro", librosService.getOneById(id));
            model.addAttribute("listaValoraciones", valoracionService.getValoracionesLibro(id));

        } catch (RuntimeException e) {
            // Si se lanza la excepción guardamos el mensaje en la variable txtErr para
            // mostrarla
            txtErr = e.getMessage();
            return "redirect:/libros";
        }
        return "libro/bookView";
    }

    // FORMULARIO AÑADIR LIBRO
    @GetMapping("/libros/addBook")
    public String showNewBook(
            Model model) {

        model.addAttribute("txtErr", txtErr);
        model.addAttribute("generos", librosService.getAllGeneros());
        model.addAttribute("libro", new Libro());

        txtErr = null; // Reseteamos variable
        return "libro/bookNewFormView";
    }

    @PostMapping("/libros/addBook/submit")
    public String showNewBookSubmit(
            @Valid Libro libro,
            BindingResult bindingResult,
            @RequestParam MultipartFile file,
            Model model) {
        // Para los errores que llegan por el @Valid
        if (bindingResult.hasErrors()) {
            txtErr = "No has completado todos los campos";
            return "redirect:/libros/addBook";
        }

        try {
            if (!file.isEmpty()) {
                String newFileName = fileStorageService.store(file);
                // devuelve el nombre definitivo con el que se ha guardado.
                // lo podríamos guardar en algún objeto
                libro.setPortada(newFileName);
                System.out.println("Fichero almacenado:" + newFileName);
            }

        } catch (Exception e) {
            txtErr = e.getMessage();
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
        model.addAttribute("generos", librosService.getAllGeneros());
        model.addAttribute("libroForm", libroAEditar);
        return "libro/bookEditFormView";
    }

    @PostMapping("/libros/addGenero/submit")
    public String showNewGeneroSubmit(
            @Valid Genero genero,
            BindingResult bindingResult,
            Model model) {
        // Para los errores que llegan por el @Valid
        if (bindingResult.hasErrors()) {
            txtErr = "No has completado todos los campos";
            return "redirect:/libros/addGenero";
        }

        try {
            librosService.addGenero(genero);
        } catch (RuntimeException e) {
            // Capturamos las excepciones que llegan del service
            txtErr = e.getMessage();
            return "redirect:/libros/addGenero";
        }

        return "redirect:/libros";
    }

    @PostMapping("/libros/edit/submit")
    public String getEditSubmit(
            @Valid Libro libroForm,
            BindingResult bindingResult,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Model model) {

        // Para los errores que llegan por el @Valid
        if (bindingResult.hasErrors()) {
            return "redirect:/edit/submit?err=1";
        }

        try {
            if (!file.isEmpty()) {
                // Guardamos la imagen y obtenemos el nombre del archivo
                String newFileName = fileStorageService.store(file);
                libroForm.setPortada(newFileName); // Actualizamos la portada del libro con el nuevo archivo
            } else {
                // Si no se sube un nuevo archivo, mantenemos la portada original
                Libro libroExistente = librosService.getOneById(libroForm.getId());
                libroForm.setPortada(libroExistente.getPortada());
            }

            // Actualizamos el libro en la base de datos
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

    // GÉNERO
    // FORMULARIO AÑADIR GÉNERO
    @GetMapping("/libros/addGenero")
    public String showNewGenero(
            Model model) {

        model.addAttribute("txtErr", txtErr);
        model.addAttribute("genero", new Genero());

        txtErr = null; // Reseteamos variable
        return "libro/generoNewFormView";
    }

    // MÉTODO PASAR IMÁGENES
    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = fileStorageService.loadAsResource(filename);
        return ResponseEntity.ok().body(file);
    }

    // BUSCADOR

    @GetMapping("/findByTitle")
    public String showFindByTitle() {
        return "libro/bookListView";
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

        return "libro/bookListView";
    }

    // BÚSQUEDA POR GÉNERO
    @GetMapping("/findByGenero/{genero}")
    public String showFindByGenero(
            @PathVariable Genero genero,
            Model model) {
        // Llamamos a la función buscarPorGénero y mostramos el resultado
        model.addAttribute("listaLibros", librosService.findByGenero(genero));
        model.addAttribute("generos", librosService.getAllGeneros());
        // Pasamos a la vista la opción seleccionada
        model.addAttribute("generoSeleccionado", genero);
        return "libro/bookListView";
    }

    // VOTACIÓN

    @GetMapping("/libros/votacion/{id}")
    public String getOneVoto(@PathVariable Long id, Model model) {
        try {
            Libro libro = librosService.getOneById(id);

            // Obtenemos el libro al que vamos a votar
            model.addAttribute("libro", libro);
            model.addAttribute("valoracionForm", new Valoracion());
            model.addAttribute("listaUsuarios", usuarioService.getAll());

        } catch (RuntimeException e) {
            // Si se lanza la excepción guardamos el mensaje en la variable txtErr para
            // mostrarla
            txtErr = e.getMessage();
            return "redirect:/libros";
        }

        return "libro/bookVotoView";
    }

    @PostMapping("/libros/votacion/submit")
    public String showNewVotoSubmit(
            @Valid Valoracion valoracionForm,
            BindingResult bindingResult,
            Model model) {
        System.out.println("Valoración recibida: " + valoracionForm);

        // Para los errores que llegan por el @Valid
        if (bindingResult.hasErrors()) {
            txtErr = "No has completado todos los campos";
            return "redirect:/libros";
        }

        try {
            // Recuperamos los objectos completos de Libro y Usuario, ya que a través
            // del formulario solo nos llegan los id.
            Libro libro = librosService.getOneById(valoracionForm.getLibro().getId());
            Usuario usuario = usuarioService.getOneById(valoracionForm.getUsuario().getId());
            System.out.println("libroid es: " + libro.getId());
            // Asignamos los objetos completos a la valoración
            valoracionForm.setLibro(libro);
            valoracionForm.setUsuario(usuario);
            
            // Guardamos la valoración
            valoracionService.addValoracion(valoracionForm);
        } catch (RuntimeException e) {
            // Capturamos las excepciones que llegan del service
            txtErr = e.getMessage();
            return "redirect:/libros";
        }

        return "redirect:/libros";
    }

}
