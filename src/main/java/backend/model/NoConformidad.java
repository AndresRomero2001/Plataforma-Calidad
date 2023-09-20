package backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@NamedQueries({
    @NamedQuery(name = "noConformidad.list", query = "select nc from NoConformidad nc")
})
public class NoConformidad {

    public NoConformidad(String origen, LocalDate fecha, String alcance, String detectadaPor,
            String descripcionDesviacion, String numeroExpediente, String acronimoExpediente, String apartadosNorma,
            String consultora, List<String> analisisCausasList, Boolean analisisExtension, String asunto, String explicacionCausas, String explicacionExtension, Boolean aplicaExpediente) {
        
        this.origen = origen;
        this.fecha = fecha; 
        this.alcance = alcance;
        this.detectadaPor = detectadaPor;
        this.descripcionDesviacion = descripcionDesviacion;
        this.numeroExpediente = numeroExpediente;
        this.acronimoExpediente = acronimoExpediente;
        this.apartadoNorma = apartadosNorma;
        this.consultora = consultora;
        this.causas = analisisCausasList;
        this.analisisExtension = analisisExtension;
        this.asunto = asunto;
        this.explicacionCausas = explicacionCausas;
        this.explicacionExtension = explicacionExtension;
        this.aplicaExpediente = aplicaExpediente;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //ALTER TABLE your_table_name AUTO_INCREMENT = 1024;
    //@SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    private String origen;
    private LocalDate fecha;
    private String alcance;
    private String detectadaPor;
    @Lob
    private String descripcionDesviacion;
    private String numeroExpediente;
    private String acronimoExpediente;
    private String consultora;
    private String apartadoNorma;
    @ElementCollection
    @CollectionTable(name = "causas", joinColumns = @JoinColumn(name = "no_conformidad_id"))
    @Column(name = "causa")
    private List<String> causas;
    @Lob
    private String explicacionCausas;
    private Boolean analisisExtension;
    @Lob
    private String explicacionExtension;
    private String asunto;
    private Boolean aplicaExpediente;
    
    @OneToOne
    private CustomFile documentoExtension;

    @OneToMany (mappedBy = "noConformidad", cascade = CascadeType.REMOVE)
    @JsonIgnore
    @ToString.Exclude
    List<Accion> acciones;

    @JsonProperty("estado")
    public String getEstado(){
        // prioridades Abierta > Pendiente > Cerrada
        // Si hay 1 de cada estado, la NC sera abierta
        // Si no hay abiertas pero si pendientes y cerradas, la NC sera pendiente
        // Si a una de las acciones de la NC le queda menos de 15 días para el plazo de implementacion
        // el estado dejara de ser pendiente o abierta y pasara a ser cerrada

        /* Boolean cerrada = false; */
        Boolean abierta = false;
        Boolean pendiente = false;
        Boolean urgente = false;

        if(acciones.size() == 0) return "Abierta";

        for(Accion a: acciones){
            // Calculate the difference between the current date and the plazoImplementacion date
            long daysBetween = ChronoUnit.DAYS.between(LocalDate.now(), a.getPlazoImplementacion());

            // Check if the Accion is urgent
            if(!a.getEstado().equals("Cerrada") && (daysBetween < 15)){
                urgente = true;
                break;  // no need to check other acciones, exit loop
            }
            // las acciones que no tienen estado asignado se consideran abiertas
            if(a.getEstado().equals("Abierta") || a.getEstado().equals("") || a.getEstado().equals(null)){
                abierta = true;
            }
            if(a.getEstado().equals("Pendiente")){
                pendiente = true;
            }
        }

        if(urgente) return "Urgente";

        if(abierta) return "Abierta";

        if(pendiente) return "Pendiente";

        return "Cerrada";
    }

}
