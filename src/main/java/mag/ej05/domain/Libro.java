package mag.ej05.domain;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data //Genera los Getters y Setters
@EqualsAndHashCode(of ="id") //Overwrite del método Equals y HashCode, 
//Ahora interpreta que dos libros son iguales si sus id son iguales
@NoArgsConstructor
@AllArgsConstructor//Genera constructor con todos los parámetros
public class Libro {

        @NotNull
        private Long id;           // Identificador para el libro
        private String titulo;     
        private int year;          
        private Genero genero;     //Genero (enum)
        private String autor;      
        private Idioma idioma;     // Idioma (enum)
        private String sinopsis; 
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate fechaDeAlta;  

        private String portada;
    
    }