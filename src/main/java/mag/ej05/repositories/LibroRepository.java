package mag.ej05.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mag.ej05.domain.Genero;
import mag.ej05.domain.Libro;


public interface LibroRepository extends JpaRepository<Libro, Long>{
    List<Libro> findByTituloContainingIgnoreCase(String titulo);
    List<Libro> findByGenero(Genero genero);
}
