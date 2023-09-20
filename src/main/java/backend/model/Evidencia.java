package backend.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evidencia {

    public Evidencia(Accion accion, String descripcion) {
        this.accion = accion;
        this.descripcion = descripcion;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;


    private String descripcion;

    @OneToOne
    private CustomFile archivo;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    private Accion accion;
}
