package mag.ej05.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import mag.ej05.domain.Genero;
import mag.ej05.domain.Libro;
import mag.ej05.domain.LibroDTO;
import mag.ej05.repositories.GeneroRepository;
import mag.ej05.services.LibrosService;
import mag.ej05.services.UsuarioService;
import mag.ej05.services.ValoracionService;

@RestController
public class ApiController {

    @Autowired(required = true)
    LibrosService librosService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired(required = true)
    ValoracionService valoracionService;

    @Autowired
    GeneroRepository generoRepository;

    // MOSTRAR TODOS LOS LIBROS
    @GetMapping("/api/book/list")
    public List<LibroDTO> showList() {

        // Primero instanciamos la lista de libros
        return librosService.getAllDTO();
    }

    // MOSTRAR ATRIBUTOS DE UN LIBRO
    @GetMapping("/api/book/id/{id}")
    public ResponseEntity<?> showOne(@PathVariable Long id) {

        try {
            // Obtenemos el libro
            Libro libro = librosService.getOneById(id);

            return ResponseEntity.status(HttpStatus.OK).body(libro);

        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    //MOSTRAR PORTADA DE UN LIBRO
    @GetMapping("/api/book/img/{id}")
    public ResponseEntity<?> showBookImg(@PathVariable Long id) {

        try {
            // Obtenemos el libro y la imagen
            Libro libro = librosService.getOneById(id);
            String img = libro.getPortada();

            return ResponseEntity.status(HttpStatus.OK).body(img);

        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    //OBTENER TODOS LOS GÉNEROS
    @GetMapping("/api/genre/list")
    public List<Genero> showGenres() {

        //Obtener géneros
       return librosService.getAllGeneros();
    
    }

   //AÑADIR UN GÉNERO
   @PostMapping("/api/genre/")
   public Genero addGenre(@Valid @RequestBody Genero genero) {
    librosService.addGenero(genero);
      return genero;
   }

}
