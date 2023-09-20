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
public class Apelacion {
    
    public Apelacion(String numeroExpediente2, String acronimo2, LocalDate fechaCertificacion2, String cliente2,
            LocalDate fechaRegistroRecepcion2, LocalDate fechaAcuseRecibo2, LocalDate fechaInformeRespuesta,
            Boolean revisionDocumental2, String revisionDocumentalText2, Boolean solicitudRequerimientos2,
            String solicitudRequerimientosText2, Boolean revisionInforme4d2, String revisionInforme4dText2,
            Boolean verificacionCompetencias2, String verificacionCompetenciasText2, Boolean revisionInformeTecnico2,
            String revisionInformeTecnicoText2, Boolean revisionEvaluacionContable2,
            String revisionEvaluacionContableText2, Boolean imparcialidadExpertos2, String imparcialidadExpertosText2,
            List<Long> ncIds2, String resolucion2, LocalDate plazoLimite2, Boolean documentoCertificacion2, 
            String documentoCertificacionText2, String comentarios2) {

        this.numeroExpediente = numeroExpediente2;
        this.acronimo = acronimo2;
        this.fechaCertificacion = fechaCertificacion2;
        this.cliente = cliente2;
        this.fechaRegistroRecepcion = fechaRegistroRecepcion2;
        this.fechaAcuseRecibo = fechaAcuseRecibo2;
        this.fechaInformeRespuesta = fechaInformeRespuesta;
        this.plazoLimite = plazoLimite2;
        this.revisionDocumental = revisionDocumental2;
        this.revisionDocumentalText = revisionDocumentalText2;
        this.solicitudRequerimientos = solicitudRequerimientos2;
        this.solicitudRequerimientosText = solicitudRequerimientosText2;
        this.revisionInforme4d = revisionInforme4d2;
        this.revisionInforme4dText = revisionInforme4dText2;
        this.verificacionCompetencias = verificacionCompetencias2;
        this.verificacionCompetenciasText = verificacionCompetenciasText2;
        this.revisionInformeTecnico = revisionInformeTecnico2;
        this.revisionInformeTecnicoText = revisionInformeTecnicoText2;
        this.revisionEvaluacionContable = revisionEvaluacionContable2;
        this.revisionEvaluacionContableText = revisionEvaluacionContableText2;
        this.imparcialidadExpertos = imparcialidadExpertos2;
        this.imparcialidadExpertosText = imparcialidadExpertosText2;
        this.ncIds = ncIds2;
        this.resolucion = resolucion2;
        this.documentos = new ArrayList<>();
        this.documentoCertificacion = documentoCertificacion2;
        this.documentoCertificacionText = documentoCertificacionText2;
        this.comentarios = comentarios2;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    String numeroExpediente;
    String acronimo;
    LocalDate fechaCertificacion;
    String cliente;

    LocalDate fechaRegistroRecepcion;
    /* @OneToMany (mappedBy = "apelacion", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    List<ApFile> documentosRegistroRecepcion; */
    LocalDate fechaAcuseRecibo;
    /* @OneToMany (mappedBy = "apelacion", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    List<ApFile> documentosAcuseRecibo; */
    LocalDate fechaInformeRespuesta;

    LocalDate plazoLimite;
    /* @OneToMany (mappedBy = "apelacion", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    List<ApFile> documentosInformeRespuesta; */

    @OneToMany (mappedBy = "apelacion", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    List<ApFile> documentos;

    Boolean revisionDocumental;
    String revisionDocumentalText;
    Boolean solicitudRequerimientos;
    String solicitudRequerimientosText;
    Boolean imparcialidadExpertos;
    String imparcialidadExpertosText;
    Boolean revisionInforme4d;
    String revisionInforme4dText;
    Boolean verificacionCompetencias;
    String verificacionCompetenciasText;
    Boolean revisionInformeTecnico;
    String revisionInformeTecnicoText;
    Boolean revisionEvaluacionContable;
    String revisionEvaluacionContableText;
    Boolean documentoCertificacion;
    String documentoCertificacionText;

    String resolucion;

    @ElementCollection
    @CollectionTable(name = "ncIds_apelacion", joinColumns = @JoinColumn(name = "apelacion_id"))
    @Column(name = "ncId")
    List<Long> ncIds;

    @Lob
    String comentarios;

    public List<ApFile> getDocumentosRegistroRecepcion(){
    return this.documentos.stream()
                 .filter(d -> d.getFileClass().equals("documentoRegistroRecepcion"))
                 .collect(Collectors.toList());
    }

    public List<ApFile> getDocumentosAcuseRecibo(){
    return this.documentos.stream()
                 .filter(d -> d.getFileClass().equals("documentoAcuseRecibo"))
                 .collect(Collectors.toList());
    }

    public List<ApFile> getDocumentosInformeRespuesta(){
    return this.documentos.stream()
                 .filter(d -> d.getFileClass().equals("documentoInformeRespuesta"))
                 .collect(Collectors.toList());
    }
    
}
