package mag.ej05.controllers.AuthController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import mag.ej05.domain.Rol;
import mag.ej05.domain.Usuario;
import mag.ej05.dto.JwtResponseDto;
import mag.ej05.dto.LoginDto;
import mag.ej05.dto.MessageResponse;
import mag.ej05.dto.SignupDto;
import mag.ej05.repositories.UsuarioRepository;
import mag.ej05.security.JwtUtils;
import mag.ej05.security.UserDetailsImpl;



@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @PostConstruct
public void init() {
    System.out.println("AuthController cargado correctamente");
}

  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UsuarioRepository usuarioRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDto loginDto) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginDto.getNombre(), loginDto.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    String rol = userDetails.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("ERROR");

    return ResponseEntity.ok(new JwtResponseDto(jwt, "Bearer",
        userDetails.getId(),
        userDetails.getUsername(),
        userDetails.getEmail(),
        rol));
  }

  @PostMapping("/signup")
  // @RequestMapping(value = "/signup", method = RequestMethod.POST)
  public ResponseEntity<?> registerUser(@RequestBody SignupDto signUpRequest) {
    System.out.println(">>>> ENTRANDO A SIGNUP");

    if (usuarioRepository.existsByNombre(signUpRequest.getNombre())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Ya existe un usuario con ese nombre"));
    }

    if (usuarioRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Ya existe un usuario con ese email"));
    }

    // Create new user's account
    Usuario user = new Usuario(null, signUpRequest.getNombre(),
        signUpRequest.getEmail(),
        encoder.encode(signUpRequest.getPassword()),
        Rol.valueOf(signUpRequest.getRol()));
    usuarioRepository.save(user);
    return ResponseEntity.ok(new MessageResponse("Usuario registrado correctamente"));
  }
}

