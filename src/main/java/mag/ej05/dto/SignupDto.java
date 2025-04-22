package mag.ej05.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SignupDto {
  @NotBlank
  @Size(min = 3, max = 20)
  private String nombre;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  private String rol;

  @NotBlank
  @Size(min = 6, max = 40)
  private String password;

}
