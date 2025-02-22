package mag.ej05.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data 
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class LibroDTO {

    private Long id;
    private String titulo;
    private Genero genero; // Genero (enum)
    private String autor;
    private String sinopsis;

    private String portada;
    private Double mediaValoracion;

}