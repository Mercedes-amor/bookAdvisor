package mag.ej05.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    PasswordEncoder passwordEncoder;

    // Buscar por nombre
    public Usuario findByNombre(String nombre) {
        return usuarioRepository.findByNombre(nombre);
    }

    // Buscar por id
    public Usuario getOneById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // Obtener todos
    public List<Usuario> getAll() {
        return usuarioRepository.findAll();
    }

    // Método añadir
    public Usuario add(Usuario usuario) {
        // Primero encriptamos la contraseña
        String passCrypted = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passCrypted);
        try {
            // Si el nombre está duplicado fallaría el guardado, por eso lo metemos en
            // un try-catch
            return usuarioRepository.save(usuario);

        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Usuario editar(Usuario usuario) {
        // Obtenemos el usuario a editar
        Usuario userAEditar = usuarioRepository.findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrada"));
        // Encriptamos la contraseña
        String passCrypted = passwordEncoder.encode(usuario.getPassword());
        userAEditar.setPassword(passCrypted);
        return usuarioRepository.save(userAEditar);
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

}