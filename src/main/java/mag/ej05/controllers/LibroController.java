package mag.ej05.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import mag.ej05.domain.Genero;
import mag.ej05.domain.Libro;
import mag.ej05.domain.Usuario;
import mag.ej05.domain.Valoracion;
import mag.ej05.services.FileStorageService;
import mag.ej05.services.LibrosService;
import mag.ej05.services.UsuarioService;
import mag.ej05.services.ValoracionService;

@Controller
@RequestMapping("/libros")
public class LibroController {

    // Variable para almacenar errores
    String txtErr = null;

    // Servicio de almacenamiento ficheros
    @Autowired
    private FileStorageService fileStorageService;

    @Autowired(required = true)
    LibrosService librosService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired(required = true)
    ValoracionService valoracionService;

    // CATÁLOGO LIBROS

    @GetMapping("/")
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
            return "redirect:/libros/";
        }
        return "libro/bookView";
    }

    // FORMULARIO AÑADIR LIBRO
    @GetMapping("/addBook")
    public String showNewBook(
            Model model) {

        model.addAttribute("txtErr", txtErr);
        model.addAttribute("generos", librosService.getAllGeneros());
        model.addAttribute("libro", new Libro());

        txtErr = null; // Reseteamos variable
        return "libro/bookNewFormView";
    }

    @PostMapping("/addBook/submit")
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

        return "redirect:/libros/";
    }

    // EDITAR LIBRO
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

        Libro libroAEditar = librosService.getOneById(id);
        model.addAttribute("generos", librosService.getAllGeneros());
        model.addAttribute("libroForm", libroAEditar);
        return "libro/bookEditFormView";
    }

    @PostMapping("/addGenero/submit")
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

        return "redirect:/libros/";
    }

    @PostMapping("/edit/submit")
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

        return "redirect:/libros/";
    }

    // BORRAR LIBRO
    @GetMapping("/delete/{id}")
    public String showDelete(@PathVariable Long id) {
        librosService.deleteById(id);
        return "redirect:/libros/";
    }

    // GÉNERO
    // FORMULARIO AÑADIR GÉNERO
    @GetMapping("/addGenero")
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

    // BUSCADORES
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
    // Actualizamos el GetMaping para obtener el id del usuario logeado
    @GetMapping("/votacion/{id}")
    public String getOneVoto(@PathVariable Long id,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            //Obtenemos el libro que estamos votando
            Libro libro = librosService.getOneById(id);

            // Crear el formulario de valoración
            Valoracion valoracion = new Valoracion();

            // Cargar el usuario logueado si existe
            if (userDetails != null) {
                Usuario usuario = usuarioService.findByNombre(userDetails.getUsername());
                valoracion.setUsuario(usuario);
            }else{
                txtErr = "Debes estar logeado para votar";
                return "redirect:/libros/";
            }

            // Cargar también el libro en la valoración
            valoracion.setLibro(libro);

            // Pasar ambos al modelo
            model.addAttribute("libro", libro);
            model.addAttribute("valoracionForm", valoracion);

        } catch (RuntimeException e) {
            // Si se lanza la excepción guardamos el mensaje en la variable txtErr para
            // mostrarla
            txtErr = e.getMessage();
            return "redirect:/libros/";
        }

        return "libro/bookVotoView";
    }

    @PostMapping("/votacion/submit")
    public String showNewVotoSubmit(
            @Valid Valoracion valoracionForm,
            BindingResult bindingResult,
            Model model) {
        System.out.println("Valoración recibida: " + valoracionForm);

        // Para los errores que llegan por el @Valid
        if (bindingResult.hasErrors()) {
            txtErr = "No has completado todos los campos";
            return "redirect:/libros/";
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
            return "redirect:/libros/";
        }

        return "redirect:/libros/";
    }

}
