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

public class TramiteAudiencia {

    public TramiteAudiencia(Integer numeroTA, String numeroExpediente2, String acronimo2, Integer ejercicioFiscal2, String numeroIMV2,
                        String cliente2, String resolucion2, LocalDate llegadaNotificacion2, String consultora2, String experto4d2,
                        String expertoTecnico2, String motivo2, String resolucionDefinitiva, LocalDate fechaInterposicion2,
                        LocalDate fechaResolucionMinisterio2, LocalDate fechaEmisionInforme, List<Integer> codigosUnesco2, 
                        Boolean revisionDocumental2, String revisionDocumentalText2, Boolean solicitudRequerimientos2,
                        String solicitudRequerimientosText2, Boolean revisionInforme4d, String revisionInforme4dText, 
                        Boolean verificacionCompetencias2, String verificacionCompetenciasText2, Boolean revisionInformeTecnico2, 
                        String revisionInformeTecnicoText2, Boolean revisionEvaluacionContable2, String revisionEvaluacionContableText2, 
                        Boolean documentoCertificacion2, String documentoCertificacionText2, Boolean imparcialidadExpertos2, String imparcialidadExpertosText2, 
                        List<Long> ncIds2, String comentarios) {
                            
        this.numeroTA = numeroTA;
        this.numeroExpediente = numeroExpediente2;
        this.acronimo = acronimo2;
        this.ejercicioFiscal = ejercicioFiscal2;
        this.numeroIMV = numeroIMV2;
        this.cliente = cliente2;
        this.resolucion = resolucion2;
        this.llegadaNotificacion = llegadaNotificacion2;
        this.consultora = consultora2;
        this.experto4d = experto4d2;
        this.expertoTecnico = expertoTecnico2;
        this.motivo = motivo2;
        this.resolucionDefinitiva = resolucionDefinitiva;
        this.fechaInterposicion = fechaInterposicion2;
        this.fechaResolucionMinisterio = fechaResolucionMinisterio2;
        this.fechaEmision = fechaEmisionInforme;
        this.codigosUnesco = codigosUnesco2;

        this.revisionDocumental = revisionDocumental2;
        this.revisionDocumentalText = revisionDocumentalText2;
        this.solicitudRequerimientos = solicitudRequerimientos2;
        this.solicitudRequerimientosText = solicitudRequerimientosText2;
        this.revisionInforme4d = revisionInforme4d;
        this.revisionInforme4dText = revisionInforme4dText;
        this.verificacionCompetencias = verificacionCompetencias2;
        this.verificacionCompetenciasText = verificacionCompetenciasText2;
        this.revisionInformeTecnico = revisionInformeTecnico2;
        this.revisionInformeTecnicoText = revisionInformeTecnicoText2;
        this.revisionEvaluacionContable = revisionEvaluacionContable2;
        this.revisionEvaluacionContableText = revisionEvaluacionContableText2;
        this.documentoCertificacion = documentoCertificacion2;
        this.imparcialidadExpertos = imparcialidadExpertos2;
        this.imparcialidadExpertosText = imparcialidadExpertosText2;

        this.ncIds = ncIds2;
        this.comentarios = comentarios;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    Integer numeroTA;
    String numeroExpediente;
    String acronimo;
    Integer ejercicioFiscal;
    String numeroIMV;
    LocalDate llegadaNotificacion;
    String resolucion;
    String cliente;
    String consultora;
    String motivo;
    String experto4d;
    String expertoTecnico;
    LocalDate fechaEmision;
    LocalDate fechaInterposicion;
    LocalDate fechaResolucionMinisterio;
    String resolucionDefinitiva;

    @ElementCollection
    @CollectionTable(name = "codigosUnesco", joinColumns = @JoinColumn(name = "tramite_audiencia_id"))
    @Column(name = "codigoUnesco")
    List<Integer> codigosUnesco;

    @ElementCollection
    @CollectionTable(name = "ncIds_tramite_audiencia", joinColumns = @JoinColumn(name = "tramite_audiencia_id"))
    @Column(name = "ncId")
    List<Long> ncIds;

    @Lob
    String comentarios;

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
    

    
}
