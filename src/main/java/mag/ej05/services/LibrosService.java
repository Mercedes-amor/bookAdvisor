package mag.ej05.services;

import java.util.List;

import mag.ej05.domain.Genero;
import mag.ej05.domain.Libro;
import mag.ej05.domain.LibroDTO;

public interface LibrosService {
    

    void add(Libro libro);
    void deleteById(Long id);
    Libro getOneById(Long id);
    List <Libro> getAll();
    public List<LibroDTO> getAllDTO();
    Libro editBook(Libro libro);
    List<Libro> findByTitle(String busqueda);
    List<Libro> findByGenero(Genero genero);

    //Género
    public void addGenero (Genero genero);
    public Genero getOneGeneroById(Long id);
    public List<Genero> getAllGeneros();
}
