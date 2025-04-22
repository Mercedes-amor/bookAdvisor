package mag.ej05.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;
import mag.ej05.domain.Usuario;
import mag.ej05.repositories.UsuarioRepository;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
@Autowired
UsuarioRepository usuarioRepository;
@Override
@Transactional
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
Usuario usuario = usuarioRepository.findByNombre(username);
if (usuario == null) throw (new UsernameNotFoundException("Usuario no encontrado!"));
return User //org.springframework.security.core.userdetails.User
.withUsername(username)
.roles(usuario.getRol().toString())
.password(usuario.getPassword())
.build();

}
// @Override
//   @Transactional
//   public UserDetails loadUserByUsername(String nombre) throws UsernameNotFoundException {
//     Usuario usuario = usuarioRepository.findByNombre(nombre);
//     if (usuario == null) throw new UsernameNotFoundException("Usuario no encontrado: " + nombre);
//     return UserDetailsImpl.build(usuario);
//   }
}
