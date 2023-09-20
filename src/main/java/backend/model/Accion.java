package backend.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.tomcat.jni.Local;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Accion {

    public Accion(AccionType tipo, String responsable, String explicacion, LocalDate fechaInicial, String seguimiento,
            String estado, NoConformidad nc, LocalDate plazoImplementacion, String identificador, LocalDate fechaCierre, LocalDate ultimoSeguimiento) {
        this.tipo = tipo;
        this.responsable = responsable;
        this.explicacion = explicacion;
        this.fechaInicial = fechaInicial;
        this.seguimiento = seguimiento;
        this.estado = estado;
        this.noConformidad = nc;
        this.plazoImplementacion = plazoImplementacion;
        this.identificador = identificador;
        this.fechaCierre = fechaCierre;
        this.ultimoSeguimiento = ultimoSeguimiento;
    }

    public enum AccionType {
        CORRECTIVA,
        REPARADORA,
        CONTENCION 
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private AccionType tipo;
    @Lob
    private String explicacion;
    private String responsable;
    private LocalDate fechaInicial;
    private LocalDate plazoImplementacion;
    private String seguimiento;
    private String estado;
    private LocalDate fechaCierre;
    private String identificador;
    private LocalDate ultimoSeguimiento;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    private NoConformidad noConformidad;

    @OneToMany (mappedBy = "accion", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    List<Evidencia> evidencias;
}
