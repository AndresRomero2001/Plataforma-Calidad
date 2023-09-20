package backend.model.dataBase;

import org.apache.logging.log4j.Logger;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;
import org.springframework.transaction.annotation.Transactional;

import backend.model.Accion;
import backend.model.Evidencia;
import backend.model.CustomFile;
import backend.model.NoConformidad;
import backend.model.User;
import backend.model.Accion.AccionType;
import backend.model.User.Role;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
public class DB {
    private static final Logger log = LogManager.getLogger(DB.class);

    public List<NoConformidad> getNoConformidades(EntityManager em){
        List<NoConformidad> noConformidades = null;

        noConformidades = em.createNamedQuery("noConformidad.list", NoConformidad.class).getResultList();
        return noConformidades;
    }

    public long addNoConformidad(EntityManager em, String origen, LocalDate fecha, String alcance, String detectadaPor,
                                String descripcionDesviacion, String numeroExpediente, String acronimoExpediente, String apartadosNorma,
                                String consultora, List<String> analisisCausasList, Boolean analisisExtension, String asunto,
                                String explicacionCausas, String explicacionExtension, Boolean aplicaExpediente) {
/*         log.info("#### inside addNoConformidad DB"); */
        long idDevolver = -1;

        NoConformidad nc = new NoConformidad(origen, fecha, alcance, detectadaPor, descripcionDesviacion, numeroExpediente, acronimoExpediente,
                                                apartadosNorma, consultora, analisisCausasList, analisisExtension,
                                                asunto, explicacionCausas, explicacionExtension, aplicaExpediente);

        em.persist(nc);
        em.flush();
        idDevolver = nc.getId();

        return idDevolver;
    }

    public void updateNoConformidad(EntityManager em, String origen, LocalDate fecha, String alcance, String detectadaPor,
                                String descripcionDesviacion, String numeroExpediente, String acronimoExpediente, String apartadoNorma,
                                String consultora, List<String> analisisCausasList, Boolean analisisExtension, String asunto,
                                String explicacionCausas, String explicacionExtension, long idNC, Boolean aplicaExpediente) {
/*         log.info("#### inside updateNoConformidad DB"); */

        NoConformidad nc = getNoConformidadById(em, idNC);

        nc.setOrigen(origen);
        nc.setAcronimoExpediente(acronimoExpediente);
        nc.setFecha(fecha);
        nc.setAlcance(alcance);
        nc.setDetectadaPor(detectadaPor);
        nc.setDescripcionDesviacion(descripcionDesviacion);
        nc.setNumeroExpediente(numeroExpediente);
        nc.setAcronimoExpediente(acronimoExpediente);
        nc.setApartadoNorma(apartadoNorma);
        nc.setConsultora(consultora);
        nc.setCausas(analisisCausasList);
        nc.setAnalisisExtension(analisisExtension);
        nc.setAsunto(asunto);
        nc.setExplicacionCausas(explicacionCausas);
        nc.setExplicacionExtension(explicacionExtension);
        nc.setAplicaExpediente(aplicaExpediente);

        /* em.persist(nc);
        em.flush();
        idDevolver = nc.getId(); */
    }
 
    public NoConformidad getNoConformidadById(EntityManager em, Long idNC) {
        NoConformidad nc = em.find(NoConformidad.class, idNC);
        return nc;
    }

    public Long addAccion(EntityManager em, AccionType tipo, String explicacion, String responsable, LocalDate fechaInicial,
            String seguimiento, String estado, Long idNC, LocalDate plazoImplementacion, String identificador, LocalDate fechaCierre, LocalDate ultimoSeguimiento) {

        long idDevolver = -1;

        //NoConformidad nc = getNoConformidadById(em, idNC);
        NoConformidad nc = em.find(NoConformidad.class, idNC);

        Accion ac = new Accion(tipo, responsable, explicacion, fechaInicial, seguimiento, estado, nc, plazoImplementacion, identificador, fechaCierre, ultimoSeguimiento);

        em.persist(ac);
        em.flush();
        idDevolver = ac.getId();

        return idDevolver;
    }

    public Accion getAccionById(EntityManager em, Long idAC) {
        Accion ac = em.find(Accion.class, idAC);
        return ac;
    }

    public Evidencia getEvidenciaById(EntityManager em, Long idEv) {
        Evidencia ev = em.find(Evidencia.class, idEv);
        return ev;
    }

    public List<User> getUsersList(EntityManager em) {
        List<User> lu = null;

        lu = em.createNamedQuery("getUsersList", User.class).getResultList();

        return lu;
    }

    public User modifyUser(EntityManager em, String username, String encodedPassword, String firstName,
            String lastName, Long userId) {

        User u = em.find(User.class, userId);
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setPassword(encodedPassword);
        u.setUsername(username);

        return u;
    }

    public User getUser(EntityManager em, Long userId) {
        User u = em.find(User.class, userId);
        return u;
    }

    public Boolean existsUserByUsername(EntityManager em, String username)
    {
        Long size = em.createNamedQuery("User.hasUsername", Long.class).setParameter("username", username).getSingleResult();
        if(size == 0) return false;
        else return true;
    }

    public void deleteUser(EntityManager em, Long userId) {
        User u = getUser(em, userId);
        u.setEnabled(false);
    }

    public long createUser(EntityManager em, String username, String password, String firstName, String lastName) {

        long idDevolver = -1;

        User u = new User(username, password, firstName, lastName, true, "USER");

        em.persist(u);
        em.flush();
        idDevolver = u.getId();

        return idDevolver;
    }

    public long setDocumentoExtension(EntityManager em, long idNC, String name, String extension) {
        NoConformidad nc = getNoConformidadById(em, idNC);
        long idDevolver = -1;

        CustomFile documentoExtension = new CustomFile(name, extension);

        em.persist(documentoExtension);
        em.flush();
        idDevolver = documentoExtension.getId();

        nc.setDocumentoExtension(documentoExtension);

        return idDevolver;
    }

    public long createEvidencia(EntityManager em, long idAc, String descripcion) {
        long idDevolver = -1;

        Accion ac = getAccionById(em, idAc);

        Evidencia e = new Evidencia(ac, descripcion);

        em.persist(e);
        em.flush();
        idDevolver = e.getId();

        return idDevolver;
    }

    public Evidencia getEvidenciaById(EntityManager em, long evidenciaId){
        Evidencia e = em.find(Evidencia.class, evidenciaId);
        return e;
    }

    public long setEvidencia(EntityManager em, long evidenciaId, String name, String extension) {
        Evidencia e = getEvidenciaById(em, evidenciaId);
        long idDevolver = -1;

        CustomFile evidencia = new CustomFile(name, extension);

        em.persist(evidencia);
        em.flush();
        idDevolver = evidencia.getId();

        e.setArchivo(evidencia);

        return idDevolver;
    }

    public void deleteAccion(EntityManager em, Long accionId) {
        Accion acc = getAccionById(em, accionId);
        em.remove(acc);
        em.flush();
    }

    /* public void deleteEvidencia(EntityManager em, Long accionId, Long evidenciaId) {
        Accion acc = getAccionById(em, accionId);
        List<Evidencia> el = acc.getEvidencias();
        Evidencia ev = getEvidenciaById(em, evidenciaId);
        el.remove(ev);

        acc.setEvidencias(el);

        em.flush();
    } */

    public void deleteEvidencia(EntityManager em, Long evidenciaId) {

        Evidencia ev = getEvidenciaById(em, evidenciaId);

        em.remove(ev);
        em.flush();
    }

    public void deleteNoConformidadFile(EntityManager em, Long documentoExtensionId, Long ncId) {
        NoConformidad nc = getNoConformidadById(em, ncId);
        nc.setDocumentoExtension(null);

        CustomFile file = em.find(CustomFile.class, documentoExtensionId);
        em.remove(file);
        em.flush();
    }

    public void deleteEvidenciaFile(EntityManager em, Long archivoId, Long evId) {
        Evidencia ev = getEvidenciaById(em, evId);
        ev.setArchivo(null);

        CustomFile file = em.find(CustomFile.class, archivoId);
        em.remove(file);
        em.flush();
    }

    public CustomFile getCustomFileById(EntityManager em, Long documentoExtensionId) {
        CustomFile file = em.find(CustomFile.class, documentoExtensionId);
        return file;
    }

    public void updateAccion(EntityManager em, AccionType tipo, String explicacion, String responsable,
            LocalDate fechaInicial, String seguimiento, String estado, Long idAc, LocalDate plazoImplementacion,
            String identificador, LocalDate fechaCierre, LocalDate ultimoSeguimiento) {

            Accion ac = em.find(Accion.class, idAc);

            ac.setEstado(estado);
            ac.setExplicacion(explicacion);
            ac.setFechaInicial(fechaInicial);
            ac.setIdentificador(identificador);
            ac.setTipo(tipo);
            ac.setResponsable(responsable);
            ac.setSeguimiento(seguimiento);
            ac.setFechaCierre(fechaCierre);
            ac.setUltimoSeguimiento(ultimoSeguimiento);
            ac.setPlazoImplementacion(plazoImplementacion);

    }

    public void deleteNoConformidad(EntityManager em, Long ncId) {
        NoConformidad nc = getNoConformidadById(em, ncId);

        for(Accion ac : nc.getAcciones()) {
            for(Evidencia ev : ac.getEvidencias()) {
                em.remove(ev.getArchivo());
            }
            em.remove(ac);
        }

        em.remove(nc);
        em.flush();
    }

    public void updateDescripcionEvidencia(EntityManager em, Long evidenciaId, String descripcion) {
        Evidencia ev = getEvidenciaById(em, evidenciaId);

        ev.setDescripcion(descripcion);
    }

    public List<NoConformidad> searchNoConformidad(EntityManager em, String asunto, LocalDate fecha, String estado, Long id, String origen) {
        log.info("@@@@ inside searchNoConformidad DB");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<NoConformidad> cq = cb.createQuery(NoConformidad.class);
    
        Root<NoConformidad> noConformidad = cq.from(NoConformidad.class);
        List<Predicate> predicates = new ArrayList<>();
    
        if (asunto != null && asunto != "") {
            log.info("#### inside asunto: " +  asunto);
            predicates.add(cb.like(noConformidad.get("asunto"), asunto + "%"));
        }
        if (fecha != null) {
            predicates.add(cb.greaterThanOrEqualTo(noConformidad.get("fecha"), fecha));
            log.info("#### inside fecha: " + fecha);
        }
        if (origen != null && !origen.isEmpty()) {
            if ("otro".equalsIgnoreCase(origen)) {
                List<String> excludedOrigins = Arrays.asList("Auditoría interna", "ENAC", "Reclamaciones", "Apelaciones", "Trámites de Audiencia", "Revisión por dirección", "Sistema de gestión");
                predicates.add(cb.not(noConformidad.get("origen").in(excludedOrigins)));
                log.info("#### inside origen otro: excluding specified origins");
            } else {
                predicates.add(cb.equal(noConformidad.get("origen"), origen));
                log.info("#### inside origen: " + origen);
            }
        }
        if (id != null && id != 0) { //si el input ID no se relleno, se envia al servidor id="", y java lo traduce como id=0
            predicates.add(cb.equal(noConformidad.get("id"), id));
            log.info("#### inside id: " + id);
        }
    
        cq.where(predicates.toArray(new Predicate[0]));
    
        List<NoConformidad> result = em.createQuery(cq).getResultList();

        if (estado != null && estado != "") {
            result = result.stream()
                .filter(nc -> nc.getEstado().equals(estado))
                .collect(Collectors.toList());
        }

        log.info("resultSize after filtering: " + result.size());

        return result;
    }

    public List<NoConformidad> getNoConformidadesByEstado(EntityManager em, String estado) {
        List<NoConformidad> ncList = getNoConformidades(em);

        if (estado != null && estado != "") {
            ncList = ncList.stream()
                .filter(nc -> nc.getEstado().equals(estado))
                .collect(Collectors.toList());
        }

        // Filter based on estado
        /* if (estado != null && estado != "" && !estado.equals("Urgente")) {
            ncList = ncList.stream()
                .filter(nc -> nc.getEstado().equals(estado))
                .collect(Collectors.toList());
        } else if (estado != null && estado.equals("Urgente")){
            LocalDate fifteenDaysAgo = LocalDate.now().minusDays(15);

            ncList = ncList.stream()
            .filter(nc -> {
                log.info("fecha: " + nc.getFecha());
                si la NC está cerrada, no es Urgente pq ya esta cerrada
                return !nc.getFecha().isBefore(fifteenDaysAgo) && !nc.getEstado().equals("Cerrada");
            })
            .collect(Collectors.toList());
        } */

        return ncList;
    }

    public Boolean checkExistsNC(EntityManager em, Long id) {
        NoConformidad nc = getNoConformidadById(em, id);

        if(nc != null) return true;
        else return false;
    }
}
