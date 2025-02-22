package mag.ej05.domain;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data // Genera los Getters y Setters
@EqualsAndHashCode(of = "id") // Overwrite del método Equals y HashCode,
// Ahora interpreta que dos libros son iguales si sus id son iguales
@NoArgsConstructor
@AllArgsConstructor // Genera constructor con todos los parámetros
@Entity
public class Libro {

    @Id // Identificador para el libro
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    private String titulo;
    private int annio;
    
    @ManyToOne
    @JoinColumn(name = "genero_id") // Ya no es una enum, es una FK
    private Genero genero;

    private String autor;
    private Idioma idioma; // Idioma (enum)
    private String sinopsis;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaDeAlta;

    private String portada;

    // CAMPOS PARA VALORACIÓN
    //Iniciamos los valores en 0
    private double mediaValoracion = 0.0; 
    private int cantidadValoraciones = 0;
    private int sumaValoraciones = 0;

    // Método para actualizar la puntuación media
    public void recalcularPuntuacion(int nuevaPuntuacion) {
        this.sumaValoraciones += nuevaPuntuacion; //Sumamos la nueva valoración
        this.cantidadValoraciones++; //Aumentamos en 1 el contador de votos
        //Recalculamos la media
        this.mediaValoracion = Math.round(((double) this.sumaValoraciones / this.cantidadValoraciones)*100/100); 
    }

}