package backend.model.dataBase;

import org.apache.logging.log4j.Logger;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;
import org.springframework.transaction.annotation.Transactional;

import backend.model.Accion;
import backend.model.Evidencia;
import backend.model.CustomFile;
import backend.model.NoConformidad;
import backend.model.TramiteAudiencia;
import backend.model.User;
import backend.model.Accion.AccionType;
import backend.model.User.Role;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import lombok.Data;

@Data
public class TADB {
    private static final Logger log = LogManager.getLogger(TADB.class);

    public long addTramiteAudiencia(EntityManager em, Integer numeroTA, String numeroExpediente, String acronimo, Integer ejercicioFiscal,
            String numeroIMV, String cliente, String resolucion, LocalDate llegadaNotificacion, String consultora,
            String experto4d, String expertoTecnico, String motivo, String resolucionDefinitiva,
            LocalDate fechaInterposicion, LocalDate fechaResolucionMinisterio, LocalDate fechaEmisionInforme,
            List<Integer> codigosUnesco, Boolean revisionDocumental, String revisionDocumentalText, Boolean solicitudRequerimientos, 
            String solicitudRequerimientosText, Boolean revisionInforme4d, String revisionInforme4dText, 
            Boolean verificacionCompetencias, String verificacionCompetenciasText, Boolean revisionInformeTecnico, 
            String revisionInformeTecnicoText, Boolean revisionEvaluacionContable, String revisionEvaluacionContableText, 
            Boolean documentoCertificacion, String documentoCertificacionText, Boolean imparcialidadExpertos, String imparcialidadExpertosText, 
            List<Long> ncIds, String comentarios) {

        TramiteAudiencia ta = new TramiteAudiencia(numeroTA, numeroExpediente, acronimo, ejercicioFiscal, numeroIMV, cliente,
                resolucion, llegadaNotificacion, consultora, experto4d, expertoTecnico, motivo, resolucionDefinitiva,
                fechaInterposicion, fechaResolucionMinisterio, fechaEmisionInforme, codigosUnesco, revisionDocumental, revisionDocumentalText, solicitudRequerimientos,
                solicitudRequerimientosText, revisionInforme4d, revisionInforme4dText, verificacionCompetencias, verificacionCompetenciasText,
                revisionInformeTecnico, revisionInformeTecnicoText, revisionEvaluacionContable, revisionEvaluacionContableText,
                documentoCertificacion, documentoCertificacionText, imparcialidadExpertos, imparcialidadExpertosText, ncIds, comentarios);

        em.persist(ta);
        em.flush();
        return ta.getId();
    }

    public List<TramiteAudiencia> getTramitesAudiencia(EntityManager em) {
        List<TramiteAudiencia> taList = new ArrayList<>();
        taList = em.createQuery("SELECT ta FROM TramiteAudiencia ta", TramiteAudiencia.class).getResultList();
        return taList;
    }

    public TramiteAudiencia getTramiteAudienciaById(EntityManager em, Long taId) {
        TramiteAudiencia ta = em.find(TramiteAudiencia.class, taId);
        return ta;
    }

    public void deleteTramiteAudiencia(EntityManager em, TramiteAudiencia ta) {
        em.remove(ta);
        em.flush();
    }

    public void updateTramiteAudiencia(EntityManager em, Integer numeroTA, String numeroExpediente, String acronimo,
            Integer ejercicioFiscal, String numeroIMV, String cliente, String resolucion, LocalDate llegadaNotificacion,
            String consultora, String experto4d, String expertoTecnico, String motivo, String resolucionDefinitiva,
            LocalDate fechaInterposicion, LocalDate fechaResolucionMinisterio, LocalDate fechaEmisionInforme,
            List<Integer> codigosUnesco, Boolean revisionDocumental, String revisionDocumentalText,
            Boolean solicitudRequerimientos, String solicitudRequerimientosText, Boolean revisionInforme4d,
            String revisionInforme4dText, Boolean verificacionCompetencias, String verificacionCompetenciasText,
            Boolean revisionInformeTecnico, String revisionInformeTecnicoText, Boolean revisionEvaluacionContable,
            String revisionEvaluacionContableText, Boolean documentoCertificacion, String documentoCertificacionText,
            Boolean imparcialidadExpertos, String imparcialidadExpertosText, List<Long> ncIds, String comentarios, Long taId) {

    TramiteAudiencia ta = em.find(TramiteAudiencia.class, taId); 

    if (ta != null) {
        ta.setNumeroTA(numeroTA);
        ta.setNumeroExpediente(numeroExpediente);
        ta.setAcronimo(acronimo);
        ta.setEjercicioFiscal(ejercicioFiscal);
        ta.setNumeroIMV(numeroIMV);
        ta.setCliente(cliente);
        ta.setResolucion(resolucion);
        ta.setLlegadaNotificacion(llegadaNotificacion);
        ta.setConsultora(consultora);
        ta.setExperto4d(experto4d);
        ta.setExpertoTecnico(expertoTecnico);
        ta.setMotivo(motivo);
        ta.setResolucionDefinitiva(resolucionDefinitiva);
        ta.setFechaInterposicion(fechaInterposicion);
        ta.setFechaResolucionMinisterio(fechaResolucionMinisterio);
        ta.setFechaEmision(fechaEmisionInforme);
        ta.getCodigosUnesco().clear();
        log.info("#### codigosUnescoSize: " + codigosUnesco.size());
        ta.setCodigosUnesco(codigosUnesco);
        ta.setRevisionDocumental(revisionDocumental);
        ta.setRevisionDocumentalText(revisionDocumentalText);
        ta.setSolicitudRequerimientos(solicitudRequerimientos);
        ta.setSolicitudRequerimientosText(solicitudRequerimientosText);
        ta.setRevisionInforme4d(revisionInforme4d);
        ta.setRevisionInforme4dText(revisionInforme4dText);
        ta.setVerificacionCompetencias(verificacionCompetencias);
        ta.setVerificacionCompetenciasText(verificacionCompetenciasText);
        ta.setRevisionInformeTecnico(revisionInformeTecnico);
        ta.setRevisionInformeTecnicoText(revisionInformeTecnicoText);
        ta.setRevisionEvaluacionContable(revisionEvaluacionContable);
        ta.setRevisionEvaluacionContableText(revisionEvaluacionContableText);
        ta.setDocumentoCertificacion(documentoCertificacion);
        ta.setDocumentoCertificacionText(documentoCertificacionText);
        ta.setImparcialidadExpertos(imparcialidadExpertos);
        ta.setImparcialidadExpertosText(imparcialidadExpertosText);
        ta.getNcIds().clear();
        ta.setNcIds(ncIds);
        ta.setComentarios(comentarios);
    }
    
}

    public void bindNC(EntityManager em, Long taId, Long ncId) {
        TramiteAudiencia ta = em.find(TramiteAudiencia.class, taId);
        NoConformidad nc = em.find(NoConformidad.class, ncId);
        if (ta != null && nc != null) {
            ta.getNcIds().add(ncId);
        }
    }

    public void unbindNC(EntityManager em, Long taId, Long ncId) {
        TramiteAudiencia ta = em.find(TramiteAudiencia.class, taId);
        NoConformidad nc = em.find(NoConformidad.class, ncId);
        if (ta != null && nc != null) {
            // por si el ID estaba duplicado
            ta.getNcIds().removeIf(id -> id.equals(ncId));
        }
    }

    public Boolean ncAlreadyBinded(EntityManager em, Long taId, Long ncId) {
        TramiteAudiencia ta = em.find(TramiteAudiencia.class, taId);

        return ta.getNcIds().contains(ncId);
    }

    public void unbindCodigoUnesco(EntityManager em, Long taId, Integer cu) {
        TramiteAudiencia ta = em.find(TramiteAudiencia.class, taId);
        if (ta != null) {
            ta.getCodigosUnesco().removeIf(id -> id.equals(cu));
        }
    }

    public List<TramiteAudiencia> searchTramiteAudiencia(EntityManager em, Integer numeroTA, String resolucionDefinitiva,
                                                    Integer ejercicioFiscal, String consultora, Long id, String numeroExpediente, 
                                                    String acronimo, String numeroIMV, String cliente, String motivo, String experto4d, 
                                                    String expertoTecnico, String resolucion, LocalDate llegadaNotificacion, Integer codigoUnesco) {
        log.info("@@@@ inside searchTramiteAudiencia DB");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TramiteAudiencia> cq = cb.createQuery(TramiteAudiencia.class);

        Root<TramiteAudiencia> tramiteAudiencia = cq.from(TramiteAudiencia.class);
        List<Predicate> predicates = new ArrayList<>();

        if (resolucionDefinitiva != null && !resolucionDefinitiva.isEmpty()) {
            log.info("#### inside resolucionDefinitiva: " + resolucionDefinitiva);
            /* predicates.add(cb.equal(tramiteAudiencia.get("resolucionDefinitiva"), resolucionDefinitiva)); */
            if (resolucionDefinitiva.equals("SinResolucionDefinitiva")) {
                Predicate notFavorable = cb.notEqual(tramiteAudiencia.get("resolucionDefinitiva"), "Favorable");
                Predicate notFavorableParcial = cb.notEqual(tramiteAudiencia.get("resolucionDefinitiva"), "Favorable parcial");
                Predicate notDesfavorable = cb.notEqual(tramiteAudiencia.get("resolucionDefinitiva"), "Desfavorable");
                predicates.add(cb.and(notFavorable, notFavorableParcial, notDesfavorable));
            } else {
                predicates.add(cb.equal(tramiteAudiencia.get("resolucionDefinitiva"), resolucionDefinitiva));
            }
        }
        if (numeroTA != null && numeroTA != 0) {
            predicates.add(cb.equal(tramiteAudiencia.get("numeroTA"), numeroTA));
            log.info("#### inside numeroTA: " + numeroTA);
        }
        if (codigoUnesco != null && codigoUnesco != 0) {
            predicates.add(cb.isMember(codigoUnesco, tramiteAudiencia.get("codigosUnesco")));
            log.info("#### inside codigoUnesco: " + codigoUnesco);
        }        
        if (ejercicioFiscal != null && ejercicioFiscal != 0) {
            predicates.add(cb.equal(tramiteAudiencia.get("ejercicioFiscal"), ejercicioFiscal));
            log.info("#### inside ejercicioFiscal: " + ejercicioFiscal);
        }
        if (consultora != null && !consultora.isEmpty()) {
            predicates.add(cb.like(tramiteAudiencia.get("consultora"), consultora + "%"));
            log.info("#### inside consultora: " + consultora);
        }
        if (id != null && id != 0) {
            predicates.add(cb.equal(tramiteAudiencia.get("id"), id));
            log.info("#### inside id: " + id);
        }
        if (numeroExpediente != null && !numeroExpediente.isEmpty()) {
            predicates.add(cb.like(tramiteAudiencia.get("numeroExpediente"), numeroExpediente + "%"));
            log.info("#### inside numeroExpediente: " + numeroExpediente);
        }
        if (acronimo != null && !acronimo.isEmpty()) {
            predicates.add(cb.like(tramiteAudiencia.get("acronimo"), acronimo + "%"));
            log.info("#### inside acronimo: " + acronimo);
        }

        if (numeroIMV != null && !numeroIMV.isEmpty()) {
            predicates.add(cb.like(tramiteAudiencia.get("numeroIMV"), numeroIMV + "%"));
            log.info("#### inside numeroIMV: " + numeroIMV);
        }
        if (cliente != null && !cliente.isEmpty()) {
            predicates.add(cb.like(tramiteAudiencia.get("cliente"), cliente + "%"));
            log.info("#### inside cliente: " + cliente);
        }
        if (motivo != null && !motivo.isEmpty()) {
            log.info("#### inside motivo: " + motivo);
            if ("Similitud".equals(motivo) || "Novedad".equals(motivo)) {
                predicates.add(cb.equal(tramiteAudiencia.get("motivo"), motivo));
            } else if ("otro".equals(motivo)) {
                // Exclude records with motivo being "Similitud", "Novedad", null or empty
                Predicate notSimilitud = cb.notEqual(tramiteAudiencia.get("motivo"), "Similitud");
                Predicate notNovedad = cb.notEqual(tramiteAudiencia.get("motivo"), "Novedad");
                Predicate notEmpty = cb.and(cb.isNotNull(tramiteAudiencia.get("motivo")), cb.notEqual(tramiteAudiencia.get("motivo"), ""));
                predicates.add(cb.and(notSimilitud, notNovedad, notEmpty));
            } else if ("SinMotivo".equals(motivo)) {
                // For records where motivo is null or empty
                Predicate isNull = cb.isNull(tramiteAudiencia.get("motivo"));
                Predicate isEmpty = cb.equal(tramiteAudiencia.get("motivo"), "");
                predicates.add(cb.or(isNull, isEmpty));
            } else {
                predicates.add(cb.like(tramiteAudiencia.get("motivo"), motivo + "%"));
            }
        }            
        if (experto4d != null && !experto4d.isEmpty()) {
            predicates.add(cb.like(tramiteAudiencia.get("experto4d"), experto4d + "%"));
            log.info("#### inside experto4d: " + experto4d);
        }
        if (expertoTecnico != null && !expertoTecnico.isEmpty()) {
            predicates.add(cb.like(tramiteAudiencia.get("expertoTecnico"), expertoTecnico + "%"));
            log.info("#### inside expertoTecnico: " + expertoTecnico);
        }
        if (resolucion != null && !resolucion.isEmpty()) {
            log.info("#### inside resolucion: " + resolucion);
            if ("Desfavorable".equals(resolucion)) {
                predicates.add(cb.equal(tramiteAudiencia.get("resolucion"), "Desfavorable"));
            } else if ("Favorable parcial".equals(resolucion)) {
                predicates.add(cb.equal(tramiteAudiencia.get("resolucion"), "Favorable parcial"));
            } else if ("SinResolucion".equals(resolucion)) {
                Predicate notDesfavorable = cb.notEqual(tramiteAudiencia.get("resolucion"), "Desfavorable");
                Predicate notFavorableParcial = cb.notEqual(tramiteAudiencia.get("resolucion"), "Favorable parcial");
                predicates.add(cb.and(notDesfavorable, notFavorableParcial));
            } else {
                predicates.add(cb.equal(tramiteAudiencia.get("resolucion"), resolucion));
            }
        }              
        if (llegadaNotificacion != null) {
            predicates.add(cb.greaterThanOrEqualTo(tramiteAudiencia.get("llegadaNotificacion"), llegadaNotificacion));
            log.info("#### inside llegadaNotificacion: " + llegadaNotificacion);
        }

        cq.where(predicates.toArray(new Predicate[0]));

        List<TramiteAudiencia> result = em.createQuery(cq).getResultList();

        log.info("resultSize after filtering: " + result.size());
        Collections.reverse(result);

        return result;
    }

    public List<TramiteAudiencia> getTramitesAudienciaByResolucionDefinitiva(EntityManager em,
            String resolucionDefinitiva) {

        List<TramiteAudiencia> taList = new ArrayList<>();
        if(!resolucionDefinitiva.equals("SinResolucionDefinitiva")){
            taList = em.createQuery(
                "SELECT ta FROM TramiteAudiencia ta WHERE ta.resolucionDefinitiva = :resolucionDefinitiva",
                TramiteAudiencia.class).setParameter("resolucionDefinitiva", resolucionDefinitiva).getResultList();
        } else {
            taList = em.createQuery(
                "SELECT ta FROM TramiteAudiencia ta WHERE ta.resolucionDefinitiva IS NULL OR ta.resolucionDefinitiva = ''",
                TramiteAudiencia.class).getResultList();
        } 

        return taList;
    }



}
