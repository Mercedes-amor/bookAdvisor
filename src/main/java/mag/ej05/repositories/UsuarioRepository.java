package mag.ej05.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import mag.ej05.domain.Usuario;


public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    Usuario findByNombre(String nombre); //Nos devuelve un nombre porque la columna nombre no acepta duplicados

}
