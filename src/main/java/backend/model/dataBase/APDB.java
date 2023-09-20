package backend.model.dataBase;

import org.apache.logging.log4j.Logger;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;
import org.springframework.transaction.annotation.Transactional;

import backend.model.Accion;
import backend.model.ApFile;
import backend.model.Apelacion;
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
public class APDB {
    private static final Logger log = LogManager.getLogger(APDB.class);

    public long addApelacion(EntityManager em, String numeroExpediente, String acronimo, String cliente,
            LocalDate fechaCertificacion, LocalDate fechaRegistroRecepcion, LocalDate fechaAcuseRecibo,
            LocalDate fechaInformeRespuesta, Boolean revisionDocumental, String revisionDocumentalText,
            Boolean solicitudRequerimientos, String solicitudRequerimientosText, Boolean revisionInforme4d,
            String revisionInforme4dText, Boolean verificacionCompetencias, String verificacionCompetenciasText,
            Boolean revisionInformeTecnico, String revisionInformeTecnicoText, Boolean revisionEvaluacionContable,
            String revisionEvaluacionContableText, Boolean imparcialidadExpertos, String imparcialidadExpertosText,
            List<Long> ncIds, String resolucion, LocalDate plazoLimite, Boolean documentoCertificacion, String documentoCertificacionText, String comentarios) {

        Apelacion ap = new Apelacion(numeroExpediente, acronimo, fechaCertificacion, cliente, fechaRegistroRecepcion, 
                                fechaAcuseRecibo, fechaInformeRespuesta, revisionDocumental, revisionDocumentalText, solicitudRequerimientos, 
                                solicitudRequerimientosText, revisionInforme4d, revisionInforme4dText, verificacionCompetencias, 
                                verificacionCompetenciasText, revisionInformeTecnico, revisionInformeTecnicoText, revisionEvaluacionContable, 
                                revisionEvaluacionContableText, imparcialidadExpertos, imparcialidadExpertosText, ncIds, resolucion, plazoLimite,
                                documentoCertificacion, documentoCertificacionText, comentarios);
        em.persist(ap);
        em.flush();
        return ap.getId();
    }

    public List<Apelacion> getApelaciones(EntityManager em) {
        List<Apelacion> apList = new ArrayList<>();
        apList = em.createQuery("SELECT ap FROM Apelacion ap", Apelacion.class).getResultList();
        return apList;
    }

    public Apelacion getApelacion(EntityManager em, Long apId) {
        Apelacion ap = em.find(Apelacion.class, apId);
        return ap;
    }

    public void deleteApelacion(EntityManager em, Apelacion ap) {
        log.info("@@@@ inside deleteApelacion APDB");

        List<ApFile> documentos = ap.getDocumentos();
        for (ApFile doc : documentos) {
            CustomFile file = doc.getArchivo();
            em.remove(file);
            
            em.remove(doc);
        }

        em.remove(ap);
        em.flush();
    }

    public Apelacion getApelacionById(EntityManager em, Long apId) {
        Apelacion ap = em.find(Apelacion.class, apId);
        return ap;
    }

    public void updateApelacion(EntityManager em, String numeroExpediente, String acronimo, String cliente,
            LocalDate fechaCertificacion, LocalDate fechaRegistroRecepcion, LocalDate fechaAcuseRecibo,
            LocalDate fechaInformeRespuesta, Boolean revisionDocumental, String revisionDocumentalText,
            Boolean solicitudRequerimientos, String solicitudRequerimientosText, Boolean revisionInforme4d,
            String revisionInforme4dText, Boolean verificacionCompetencias, String verificacionCompetenciasText,
            Boolean revisionInformeTecnico, String revisionInformeTecnicoText, Boolean revisionEvaluacionContable,
            String revisionEvaluacionContableText, Boolean imparcialidadExpertos, String imparcialidadExpertosText,
            List<Long> ncIds, String resolucion, Long apId, LocalDate plazoLimite, Boolean documentoCertificacion, 
            String documentoCertificacionText, String comentarios) {

        Apelacion ap = getApelacionById(em, apId);

        ap.setNumeroExpediente(numeroExpediente);
        ap.setAcronimo(acronimo);
        ap.setCliente(cliente);
        ap.setFechaCertificacion(fechaCertificacion);
        ap.setFechaRegistroRecepcion(fechaRegistroRecepcion);
        ap.setFechaAcuseRecibo(fechaAcuseRecibo);
        ap.setFechaInformeRespuesta(fechaInformeRespuesta);
        ap.setPlazoLimite(plazoLimite);  // Note: the parameter name might be misleading compared to the field name
        ap.setRevisionDocumental(revisionDocumental);
        ap.setRevisionDocumentalText(revisionDocumentalText);
        ap.setSolicitudRequerimientos(solicitudRequerimientos);
        ap.setSolicitudRequerimientosText(solicitudRequerimientosText);
        ap.setImparcialidadExpertos(imparcialidadExpertos);
        ap.setImparcialidadExpertosText(imparcialidadExpertosText);
        ap.setRevisionInforme4d(revisionInforme4d);
        ap.setRevisionInforme4dText(revisionInforme4dText);
        ap.setVerificacionCompetencias(verificacionCompetencias);
        ap.setVerificacionCompetenciasText(verificacionCompetenciasText);
        ap.setRevisionInformeTecnico(revisionInformeTecnico);
        ap.setRevisionInformeTecnicoText(revisionInformeTecnicoText);
        ap.setRevisionEvaluacionContable(revisionEvaluacionContable);
        ap.setRevisionEvaluacionContableText(revisionEvaluacionContableText);
        ap.setDocumentoCertificacion(documentoCertificacion);
        ap.setDocumentoCertificacionText(documentoCertificacionText);
        ap.setResolucion(resolucion);
        ap.setNcIds(ncIds);
        ap.setComentarios(comentarios);
    }

    public void bindNC(EntityManager em, Long apId, Long ncId) {
        Apelacion ap = em.find(Apelacion.class, apId);
        NoConformidad nc = em.find(NoConformidad.class, ncId);
        if (ap != null && nc != null) {
            ap.getNcIds().add(ncId);
        }
    }

    public void unbindNC(EntityManager em, Long apId, Long ncId) {
        Apelacion ap = em.find(Apelacion.class, apId);
        NoConformidad nc = em.find(NoConformidad.class, ncId);
        if (ap != null && nc != null) {
            // por si el ID estaba duplicado
            ap.getNcIds().removeIf(id -> id.equals(ncId));
        }
    }

    public Boolean ncAlreadyBinded(EntityManager em, Long apId, Long ncId) {
        Apelacion ap = em.find(Apelacion.class, apId);

        return ap.getNcIds().contains(ncId);
    }

    public List<Apelacion> searchApelacion(EntityManager em, LocalDate fechaRecepcion, Long id,
                                        String numeroExpediente, String acronimo, String resolucion) {
        log.info("@@@@ inside searchApelacion DB");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Apelacion> cq = cb.createQuery(Apelacion.class);

        Root<Apelacion> apelacion = cq.from(Apelacion.class);
        List<Predicate> predicates = new ArrayList<>();

        if (fechaRecepcion != null) {
            log.info("#### inside fechaRecepcion: " + fechaRecepcion);
            predicates.add(cb.equal(apelacion.get("fechaRegistroRecepcion"), fechaRecepcion));
        }
        if (id != null && id != 0) {
            log.info("#### inside id: " + id);
            predicates.add(cb.equal(apelacion.get("id"), id));
        }
        if (numeroExpediente != null && !numeroExpediente.isEmpty()) {
            log.info("#### inside numeroExpediente: " + numeroExpediente);
            predicates.add(cb.like(apelacion.get("numeroExpediente"), numeroExpediente + "%"));
        }
        if (acronimo != null && !acronimo.isEmpty()) {
            log.info("#### inside acronimo: " + acronimo);
            predicates.add(cb.like(apelacion.get("acronimo"), acronimo + "%"));
        }
        if (resolucion != null && !resolucion.isEmpty()) {
            log.info("#### inside resolucion: " + resolucion);
            if (resolucion.equals("SinResolucion")) {
                Predicate notEstimada = cb.notEqual(apelacion.get("resolucion"), "Estimada");
                Predicate notDesestimada = cb.notEqual(apelacion.get("resolucion"), "Desestimada");
                Predicate isNull = cb.isNull(apelacion.get("resolucion"));
                predicates.add(cb.or(isNull, cb.and(notEstimada, notDesestimada)));
            } else {
                predicates.add(cb.equal(apelacion.get("resolucion"), resolucion));
            }
        }

        cq.where(predicates.toArray(new Predicate[0]));

        List<Apelacion> result = em.createQuery(cq).getResultList();

        log.info("resultSize after filtering: " + result.size());
        Collections.reverse(result);

        return result;
    }

    public List<Apelacion> getApelacionesByResolucion(EntityManager em, String resolucion) {
        List<Apelacion> apList = new ArrayList<>();
        apList = em.createQuery("SELECT ap FROM Apelacion ap WHERE ap.resolucion = :resolucion", Apelacion.class)
                .setParameter("resolucion", resolucion).getResultList();
        if(!resolucion.equals("SinResolucion")){
            apList = em.createQuery(
                "SELECT ap FROM Apelacion ap WHERE ap.resolucion = :resolucion",
                Apelacion.class).setParameter("resolucion", resolucion).getResultList();
        } else {
            apList = em.createQuery(
                "SELECT ap FROM Apelacion ap WHERE ap.resolucion IS NULL OR ap.resolucion = ''",
                Apelacion.class).getResultList();
        } 
        return apList;
    }

    /* public long addCustomFile(EntityManager em, long apId, String name, String extension, String fileClass) {
        Apelacion ap = em.find(Apelacion.class, apId);
        CustomFile cf = new CustomFile(name, extension);
        ApFile apFile = new ApFile(ap, fileClass);
        cf.setApelacion(ap);
        if(fileClass.equals("documentoRegistroRecepcion")) {
            log.info("#### inside registro recibo");
            ap.getDocumentosRegistroRecepcion().add(cf);
        }
        if(fileClass.equals("documentoAcuseRecibo")) {
            log.info("#### inside acuse recibo");
            ap.getDocumentosAcuseRecibo().add(cf);
        }
        if(fileClass.equals("documentoInformeRespuesta")) {
            log.info("#### inside informe respuesta");
            ap.getDocumentosInformeRespuesta().add(cf);
        }

        em.persist(cf);
        em.flush();
        em.clear();

        return cf.getId();
    } */

    public CustomFile getCustomFileById(EntityManager em, long fileId) {
        CustomFile cf = em.find(CustomFile.class, fileId);
        return cf;
    }

    public long createApFile(EntityManager em, String fileClass, long apId) {
        Apelacion ap = em.find(Apelacion.class, apId);
        ApFile apFile = new ApFile(ap, fileClass);

        apFile.setApelacion(ap);

        ap.getDocumentos().add(apFile);

        em.persist(apFile);
        em.persist(ap);
        em.flush();

        return apFile.getId();
    }

    public long setApFile(EntityManager em, long apFileId, String name, String extension) {
        ApFile apFile = em.find(ApFile.class, apFileId);
        
        CustomFile cf = new CustomFile(name, extension);
        em.persist(cf);
        em.flush();

        apFile.setArchivo(cf);
        
        return cf.getId();
    }

    public void deleteCustomFile(EntityManager em, Long fileId, Long apFileId) {
        ApFile apFile = getApFileById(em, apFileId);
        apFile.setArchivo(null);

        CustomFile file = em.find(CustomFile.class, fileId);
        em.remove(file);
        em.flush();
    }

    public void deleteApFile(EntityManager em, Long apFileId) {
        /* Apelacion ap = getApelacionById(em, apId); */
        ApFile apFile = getApFileById(em, apFileId);
        /* ap.getDocumentos().remove(apFile); */
        em.remove(apFile);
        em.flush();
    }

    public ApFile getApFileById(EntityManager em, Long apFileId) {
        ApFile apFile = em.find(ApFile.class, apFileId);
        return apFile;
    }
    
}
