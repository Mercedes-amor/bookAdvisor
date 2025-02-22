package mag.ej05.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mag.ej05.domain.Usuario;
import mag.ej05.repositories.LibroRepository;
import mag.ej05.repositories.UsuarioRepository;

@Service
public class UsuarioService {

    // Autoinyectamos el repositorio
    @Autowired
    LibroRepository libroRepository;

    @Autowired
    UsuarioRepository usuarioRepository;


    //Método añadir
    public void add(Usuario usuario) {

        usuarioRepository.save(usuario);
    }

    // Método eliminar
    public void deleteById(Long id) {
        if (usuarioRepository.count() == 0) {
            throw new RuntimeException("La lista de usuarios está vacía");
        }

        Usuario usuario = getOneById(id);
        if (usuario != null) {
            usuarioRepository.delete(usuario);
        } else {
            throw new RuntimeException("No se ha encontrado el usuario");
        }

    }

    // Método obtener un usuario
    public Usuario getOneById(Long id) {

         return usuarioRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // Método obtener todos
    public List<Usuario> getAll() {
        return usuarioRepository.findAll();
    }

    // Método actualizar usuario
    public Usuario editUser(Usuario usuario) {
        // Obtenemos el libro a modificar dentro del repositorio
        Usuario usuarioAEditar = usuarioRepository.findById(usuario.getId())
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

             
        return usuarioRepository.save(usuarioAEditar);
    }

    
}