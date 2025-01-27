package mag.ej05.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import mag.ej05.domain.Genero;
import mag.ej05.domain.Idioma;
import mag.ej05.domain.Libro;

@Service
public class LibrosServiceImp implements LibrosService {

    /// Inicializamos el ArrayList de libros
    List<Libro> librosRepository = new ArrayList<>();

    public LibrosServiceImp() {
        // Agregamos algunos libros iniciales para probar
        librosRepository.add(new Libro(1L, "El Señor de los Anillos", 1954, Genero.FANTASIA, "J.R.R. Tolkien",
                Idioma.ESPAÑOL, "Una épica aventura en la Tierra Media", LocalDate.now()));
        librosRepository.add(new Libro(2L, "Cien Años de Soledad", 1967, Genero.DRAMA, "Gabriel García Márquez",
                Idioma.ESPAÑOL, "La historia de la familia Buendía", LocalDate.now()));
        librosRepository.add(new Libro(3L, "1984", 1949, Genero.CIENCIA_FICCION, "George Orwell", Idioma.INGLES,
                "Una crítica distópica de la sociedad", LocalDate.now()));
    }

    public void add(Libro libro) {

        librosRepository.add(libro);
    }

    // Método eliminar libro
    public void deleteById(Long id) {
        if (librosRepository.isEmpty()) {
            throw new RuntimeException("La lista de libros está vacía");
        }

        Libro libro = getOneById(id);
        if (libro != null) {
            librosRepository.remove(libro);
        } else {
            throw new RuntimeException("No se ha encontrado el libro");
        }

    }

    // Método obtener un libro
    public Libro getOneById(Long id) {
        for (Libro libro : librosRepository) {
            if (libro.getId().equals(id)) {
                return libro;
            }
        }
        // Lanzamos una excepción al no encontrar el empleado buscado por id
        throw new RuntimeException("libro no encontrado");
    }

    // Método obtener todos
    public List<Libro> getAll() {
        return librosRepository;
    }

    // Método actualizar libro
    public Libro editBook(Libro libro) {

        // Obtenemos la posición del libro a modificar dentro
        // del array librosRepository
        int pos = librosRepository.indexOf(libro);
        if (pos == -1) {
            throw new RuntimeException("Libro no encontrado");
        } else {
            librosRepository.set(pos, libro);
        }

        return libro;

    }

    // BUSCADOR
    public List<Libro> findByTitle(String busqueda) {
        // Convertimos todo a minúscula para que no no sea case sensitive
        busqueda = busqueda.toLowerCase();
        // Creamos un nuevo arrayList para almacenar los empleados encontrados
        List<Libro> encontrados = new ArrayList<>();
        // Comparacion con .contains para comprobar si el título contiene el texto
        // buscado
        for (Libro libro : librosRepository)
            if (libro.getTitulo().toLowerCase().contains(busqueda))
                // Si lo encuentra lo añade al arrayList
                encontrados.add(libro);
        return encontrados;
    }

     // BÚSQUEDA POR GÉNERO
     public List<Libro> findByGenero(Genero genero) {
        List<Libro> encontrados = new ArrayList<>();
        for (Libro libro : librosRepository)
            if (libro.getGenero() == genero)
                encontrados.add(libro);
        return encontrados;
    }
}