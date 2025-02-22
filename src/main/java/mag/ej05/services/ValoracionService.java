package mag.ej05.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mag.ej05.domain.Libro;
import mag.ej05.domain.Usuario;
import mag.ej05.domain.Valoracion;
import mag.ej05.domain.ValoracionId;
import mag.ej05.repositories.LibroRepository;
import mag.ej05.repositories.UsuarioRepository;
import mag.ej05.repositories.ValoracionRepository;

@Service
public class ValoracionService {

    // Autoinyectamos los repositorios
    @Autowired
    LibroRepository libroRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    ValoracionRepository valoracionRepository;

    // Crear la clave compuesta
    public ValoracionId crearValoracionId(Long usuarioId, Long libroId) {

        ValoracionId valoracionId = new ValoracionId(usuarioId, libroId);
        return valoracionId;
    }

    // Añadir valoración
    public Valoracion addValoracion(Valoracion valoracion) {

        // Obtenemos el objeto Libro y Usuario
        Libro libro = valoracion.getLibro();
        Usuario usuario = valoracion.getUsuario();

        // Creamos la id compuesta
        ValoracionId id = new ValoracionId(usuario.getId(), libro.getId());

        if (valoracionRepository.existsById(id)) {
            throw new IllegalArgumentException("El usuario ya ha valorado este libro.");
        }
        // Asignamos la clave compuesta en la entidad
        valoracion.setUsuario(usuario);
        valoracion.setLibro(libro);
        System.out.println("Guardando valoración: " + valoracion);

        // Actualizamos la valoración media del libro
        libro.recalcularPuntuacion(valoracion.getPuntuacion());
        libroRepository.save(libro);

        return valoracionRepository.save(valoracion);
    }

    // Recuperar una valoración
    public Optional<Valoracion> getValoracion(Long usuarioId, Long libroId) {
        return valoracionRepository.findByUsuarioIdAndLibroId(usuarioId, libroId);
    }

    // Obtener todas las valoraciones
    public List<Valoracion> getAllValoraciones() {
        return valoracionRepository.findAll();
    }

    //Obtener las valoraciones de un libro
    public List<Valoracion> getValoracionesLibro(Long libroId){
       return valoracionRepository.findByLibroId(libroId);
    }

}
