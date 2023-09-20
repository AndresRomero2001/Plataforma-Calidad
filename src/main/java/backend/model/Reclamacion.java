package backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
public class Reclamacion {

    public Reclamacion(String numeroExpediente2, String acronimo2, String consultora2, LocalDate fechaRecepcion2,
            LocalDate fechaMaximaRespuesta2, String estado2, String comentarios2) {
                
        this.numeroExpediente = numeroExpediente2;
        this.acronimo = acronimo2;
        this.consultora = consultora2;
        this.fechaRecepcion = fechaRecepcion2;
        this.fechaMaximaRespuesta = fechaMaximaRespuesta2;
        this.estado = estado2;
        this.documentosExtra = new ArrayList<>();
        this.documentosRespuestaExtra = new ArrayList<>();
        this.comentarios = comentarios2;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    String numeroExpediente;
    String acronimo;
    String consultora;
    LocalDate fechaRecepcion;
    LocalDate fechaMaximaRespuesta;
    String estado;

    @Lob
    String comentarios;

    @OneToOne
    CustomFile registroFormulario;

    @OneToMany (mappedBy = "reclamacion", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    List<ReclamacionFile> documentosExtra;

    @OneToMany (mappedBy = "reclamacion", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    List<ReclamacionFile> documentosRespuestaExtra;

    public List<ReclamacionFile> getDocumentosExtra(){
        return this.documentosExtra.stream()
                     .filter(d -> d.getFileClass().equals("documentoExtra"))
                     .collect(Collectors.toList());
    }

    public List<ReclamacionFile> getDocumentosRespuestaExtra(){
        return this.documentosRespuestaExtra.stream()
                     .filter(d -> d.getFileClass().equals("documentoRespuestaExtra"))
                     .collect(Collectors.toList());
    }
    
}
