package mag.ej05.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mag.ej05.domain.Genero;
import mag.ej05.domain.Libro;
import mag.ej05.domain.LibroDTO;
import mag.ej05.repositories.GeneroRepository;
import mag.ej05.repositories.LibroRepository;

@Service
public class LibrosServiceImp implements LibrosService {

    // Autoinyectamos el repositorio
    @Autowired
    LibroRepository libroRepository;

    @Autowired
    GeneroRepository generoRepository;

    public void add(Libro libro) {
        LocalDate fecha = LocalDate.now();

        libro.setFechaDeAlta(fecha);

        libro.setMediaValoracion(0d);

        libroRepository.save(libro);
    }

    // Método eliminar libro
    public void deleteById(Long id) {
        if (libroRepository.count() == 0) {
            throw new RuntimeException("La lista de libros está vacía");
        }

        Libro libro = getOneById(id);
        if (libro != null) {
            libroRepository.delete(libro);
        } else {
            throw new RuntimeException("No se ha encontrado el libro");
        }

    }

    // Método obtener un libro
    public Libro getOneById(Long id) {

         return libroRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Libro no encontrado"));
    }

    // Método obtener todos
    public List<Libro> getAll() {
        return libroRepository.findAll();
    }

    // Método actualizar libro
    public Libro editBook(Libro libro) {
        // Obtenemos el libro a modificar dentro del repositorio
        Libro libroAEditar = libroRepository.findById(libro.getId())
        .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        libro.setPortada(libroAEditar.getPortada());
                
            // Si el campo de portada viene vacío o nulo, mantenemos la portada anterior
            if (libro.getPortada() != null && !libro.getPortada().isEmpty()) {
                libroAEditar.setPortada(libro.getPortada());
            }
              
        return libroRepository.save(libro);
    }

    //Método convertir Libro a LibroDTO
    public LibroDTO convertirADTO(Libro libro) {
        if (libro == null) {
            throw new RuntimeException("No se ha encontrado el libro");
        }
        return new LibroDTO(
            libro.getId(),
            libro.getTitulo(),
            libro.getGenero(),
            libro.getAutor(),
            libro.getSinopsis(),
            libro.getPortada(),
            libro.getMediaValoracion()
        );
    }

        // Método obtener todos los DTO
        public List<LibroDTO> getAllDTO() {
            return libroRepository.findAll() //Obtenemos todos los libros
                .stream().map(this::convertirADTO) //Convierte cada libro en DTO usando el método anterior
                .collect(Collectors.toList());
        }

    // BUSCADOR
    public List<Libro> findByTitle(String busqueda) {
        // Convertimos todo a minúscula para que no no sea case sensitive
        busqueda = busqueda.toLowerCase();
        // Creamos un nuevo arrayList para almacenar los empleados encontrados
        List<Libro> encontrados = libroRepository.findByTituloContainingIgnoreCase(busqueda);
        
        return encontrados;
    }

     // BÚSQUEDA POR GÉNERO
     public List<Libro> findByGenero(Genero genero) {
        List<Libro> encontrados = libroRepository.findByGenero(genero);
        
        return encontrados;
    }


    //GENERO
    
    //Método añadir un genero
    public void addGenero (Genero genero) {

        generoRepository.save(genero);
    }
    // Método obtener un genero
    public Genero getOneGeneroById(Long id) {

        return generoRepository.findById(id)
       .orElseThrow(() -> new RuntimeException("Genero no encontrado"));
   }

   // Método obtener todos
   public List<Genero> getAllGeneros() {
       return generoRepository.findAll();
   }
}