package mag.ej05.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
// Evita duplicados en la clave compuesta
// Así se garantiza que un usuario solo puede realizar una valoración por libro
@IdClass(ValoracionId.class)
public class Valoracion {
  
    // Un usuario varias valoraciones
    // Cada valoración solo a un usuario
    @Id
    @ManyToOne
    @JoinColumn(name = "USUARIO_ID")
    private Usuario usuario;


    // Un libro varias valoraciones
    // Cada valoración solo 1 libro
    @Id
    @ManyToOne
    @JoinColumn(name = "LIBRO_ID")
    private Libro libro;

    private int puntuacion;

    private String comentarios;
}
