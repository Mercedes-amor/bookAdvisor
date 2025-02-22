package mag.ej05.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mag.ej05.domain.Valoracion;
import mag.ej05.domain.ValoracionId;


public interface ValoracionRepository extends JpaRepository<Valoracion, ValoracionId>{


    //Buscar la valoración de un usuario sobre un libro
    Optional<Valoracion> findByUsuarioIdAndLibroId(Long usuarioId, Long libroId);
    List<Valoracion> findByLibroId(Long libroId);


}
