package mag.ej05;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class FormInfo {
    @NotNull(message = "Debe introducir un nombre")
    @NotEmpty(message = "Debe introducir un nombre")
    private String nombre;

    @NotNull(message = "Por favor introduzca un comentario")
    @NotEmpty(message = "Por favor introduzca un comentario")
    private String comentario;

    @NotNull(message = "Debe introducir un email")
    @NotEmpty(message = "Debe introducir un email")
    @Email(message = "El correo electrónico debe tener un formato válido")
    private String email;

    @NotNull(message = "Debe seleccionar una opcion")
    private TipoContacto tipoContacto;

    // Condición de validación para el booleano "Aceptar condiciones" debe estar
    // seleccionado(true)
    @AssertTrue(message = "Debe aceptar las condiciones para continuar.")
    private Boolean condiciones;

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getCondiciones() {
        return condiciones;
    }

    public void setCondiciones(Boolean condiciones) {
        this.condiciones = condiciones;
    }

    public TipoContacto getTipoContacto() {
        return tipoContacto;
    }

    public void setTipoContacto(TipoContacto tipoContacto) {
        this.tipoContacto = tipoContacto;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

}
