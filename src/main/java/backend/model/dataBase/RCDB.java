package backend.model.dataBase;

import org.apache.logging.log4j.Logger;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;
import org.springframework.transaction.annotation.Transactional;

import backend.model.Apelacion;
import backend.model.CustomFile;
import backend.model.Reclamacion;
import backend.model.ReclamacionFile;
import backend.model.User;
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
public class RCDB {
    private static final Logger log = LogManager.getLogger(RCDB.class);

    public List<Reclamacion> getReclamaciones(EntityManager em) {
        List<Reclamacion> rcList = new ArrayList<>();
        rcList = em.createQuery("SELECT rc FROM Reclamacion rc", Reclamacion.class).getResultList();
        return rcList;
    }

    public long addReclamacion(EntityManager em, String numeroExpediente, String acronimo, String consultora,
            LocalDate fechaRecepcion, LocalDate fechaMaximaRespuesta, String estado, String comentarios) {

        Reclamacion rc = new Reclamacion(numeroExpediente, acronimo, consultora, fechaRecepcion, 
        fechaMaximaRespuesta, estado, comentarios);

        em.persist(rc);
        return rc.getId();
    }

    public CustomFile getCustomFileById(EntityManager em, long fileId) {
        CustomFile cf = em.find(CustomFile.class, fileId);
        return cf;
    }

    public long createReclamacionFile(EntityManager em, String fileClass, long rcId, long fileId) {
        Reclamacion rc = em.find(Reclamacion.class, rcId);
        CustomFile cf = em.find(CustomFile.class, fileId);

        ReclamacionFile rcFile = new ReclamacionFile(rc, fileClass);

        rcFile.setArchivo(cf);

        em.persist(rcFile);
        em.persist(rc);
        em.flush();

        return rcFile.getId();
    }

    public void setRcFile(EntityManager em, long rcId, long rcFileId) {
        Reclamacion rc = em.find(Reclamacion.class, rcId);
        ReclamacionFile rcFile = em.find(ReclamacionFile.class, rcFileId);

        if(rcFile.getFileClass().equals("documentoExtra")) rc.getDocumentosExtra().add(0, rcFile);
        else if (rcFile.getFileClass().equals("documentoRespuestaExtra")) rc.getDocumentosRespuestaExtra().add(0, rcFile);

        em.persist(rc);
        em.flush();
    }

    public void deleteRegistroFormulario(EntityManager em, Long rcId) {
        Reclamacion rc = em.find(Reclamacion.class, rcId);
        CustomFile file = rc.getRegistroFormulario();
        rc.setRegistroFormulario(null);
        em.remove(file);
        em.flush();
    }

    public ReclamacionFile getRcFileById(EntityManager em, Long rcFileId) {
        ReclamacionFile rcFile = em.find(ReclamacionFile.class, rcFileId);
        return rcFile;
    }

    public long createCustomFile(EntityManager em, String name, String extension) {
        CustomFile cf = new CustomFile(name, extension);
        em.persist(cf);
        em.flush();
        return cf.getId();
    }

    public void setFormularioRegistro(EntityManager em, long rcId, long fileId) {
        Reclamacion rc = em.find(Reclamacion.class, rcId);
        CustomFile cf = em.find(CustomFile.class, fileId);
        rc.setRegistroFormulario(cf);
        em.persist(rc);
        em.flush();
    }

    public Reclamacion getReclamacionById(EntityManager em, Long rcId) {
        Reclamacion rc = em.find(Reclamacion.class, rcId);
        return rc;
    }

    public ReclamacionFile getReclamacionFileById(EntityManager em, Long rcFileId) {
        ReclamacionFile rcFile = em.find(ReclamacionFile.class, rcFileId);
        return rcFile;
    }

    public void deleteReclamacionFile(EntityManager em, Long rcId, Long rcFileId) {
        Reclamacion rc = em.find(Reclamacion.class, rcId);
        ReclamacionFile rcFile = em.find(ReclamacionFile.class, rcFileId);
        CustomFile cf = rcFile.getArchivo();

        if(rcFile.getFileClass().equals("documentoExtra")) rc.getDocumentosExtra().remove(rcFile);
        else if (rcFile.getFileClass().equals("documentoRespuestaExtra")) rc.getDocumentosRespuestaExtra().remove(rcFile);

        
        rcFile.setArchivo(null);
        em.remove(cf);

        em.remove(rcFile);
        em.flush();
    }

    public void updateReclamacion(EntityManager em, String numeroExpediente, String acronimo, String consultora,
            LocalDate fechaRecepcion, LocalDate fechaMaximaRespuesta, String estado, Long rcId, String comentarios) {

        Reclamacion rc = em.find(Reclamacion.class, rcId);
        rc.setNumeroExpediente(numeroExpediente);
        rc.setAcronimo(acronimo);
        rc.setConsultora(consultora);
        rc.setFechaRecepcion(fechaRecepcion);
        rc.setFechaMaximaRespuesta(fechaMaximaRespuesta);
        rc.setEstado(estado);
        rc.setComentarios(comentarios);
    }

    public void deleteReclamacion(EntityManager em, Reclamacion rc) {
        for(ReclamacionFile rcFile : rc.getDocumentosExtra()) {
            CustomFile cf = rcFile.getArchivo();
            rcFile.setArchivo(null);
            em.remove(cf);
        }
        for(ReclamacionFile rcFile : rc.getDocumentosRespuestaExtra()) {
            CustomFile cf = rcFile.getArchivo();
            rcFile.setArchivo(null);
            em.remove(cf);
        }
        em.remove(rc);
        em.flush();
    }

    public void deleteAllRcFiles(EntityManager em, Long rcId) {
        Query query = em.createQuery("DELETE FROM ReclamacionFile r WHERE r.reclamacion.id = :rcId");
        query.setParameter("rcId", rcId);
        query.executeUpdate();
    }
    
    public List<Reclamacion> searchReclamacion(EntityManager em, LocalDate fechaRecepcion, Long id,
                                        String numeroExpediente, String acronimo, String estado, String consultora) {
        log.info("@@@@ inside searchReclamacion DB");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Reclamacion> cq = cb.createQuery(Reclamacion.class);

        Root<Reclamacion> reclamacion = cq.from(Reclamacion.class);
        List<Predicate> predicates = new ArrayList<>();

        if (fechaRecepcion != null) {
            log.info("#### inside fechaRecepcion: " + fechaRecepcion);
            predicates.add(cb.equal(reclamacion.get("fechaRecepcion"), fechaRecepcion));
        }
        if (id != null && id != 0) {
            log.info("#### inside id: " + id);
            predicates.add(cb.equal(reclamacion.get("id"), id));
        }
        if (numeroExpediente != null && !numeroExpediente.isEmpty()) {
            log.info("#### inside numeroExpediente: " + numeroExpediente);
            predicates.add(cb.like(reclamacion.get("numeroExpediente"), numeroExpediente + "%"));
        }
        if (acronimo != null && !acronimo.isEmpty()) {
            log.info("#### inside acronimo: " + acronimo);
            predicates.add(cb.like(reclamacion.get("acronimo"), acronimo + "%"));
        }
        if (estado != null && !estado.isEmpty()) {
            log.info("#### inside estado: " + estado);
            predicates.add(cb.equal(reclamacion.get("estado"), estado));
        }
        if (consultora != null && !consultora.isEmpty()) {
            log.info("#### inside consultora: " + consultora);
            predicates.add(cb.like(reclamacion.get("consultora"), consultora + "%"));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        List<Reclamacion> result = em.createQuery(cq).getResultList();

        log.info("resultSize after filtering: " + result.size());
        Collections.reverse(result);

        return result;
    }
    
    public List<Reclamacion> getReclamacionesByEstado(EntityManager em, String estado) {
        List<Reclamacion> rcList = new ArrayList<>();
        rcList = em.createQuery("SELECT rc FROM Reclamacion rc WHERE rc.estado = :estado", Reclamacion.class)
                .setParameter("estado", estado).getResultList();

        return rcList;
    }
}
