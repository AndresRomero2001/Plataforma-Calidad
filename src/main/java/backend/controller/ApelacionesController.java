package backend.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;

import backend.model.ApFile;
import backend.model.Apelacion;
import backend.model.CustomFile;
import backend.model.Evidencia;
import backend.model.TramiteAudiencia;
import backend.model.dataBase.APDB;
import backend.model.dataBase.TADB;

@Controller
@RequestMapping("ap")
public class ApelacionesController {
    @Autowired
    private EntityManager em;

    private APDB apDB = new APDB();

	private static final Logger log = LogManager.getLogger(ApelacionesController.class);

    @GetMapping("/apelaciones")
    public String apelaciones(Model model, @RequestParam(required = false) String resolucion) {
        List<Apelacion> apList = apDB.getApelaciones(em);
        Collections.reverse(apList);

        if(resolucion != null) {
            apList = apDB.getApelacionesByResolucion(em, resolucion);
        }

        model.addAttribute("apelaciones", apList);

        return "apelaciones";
    }

    @GetMapping("/addApelacion")
    public String addApelacion(Model model) {
        
        return "addApelacion";
    }

    @GetMapping("/manageApelacion")
    public String manageApelacion(Model model, HttpSession session, @RequestParam(required = true) Long apId) {
        Apelacion ap = apDB.getApelacion(em, apId);
        model.addAttribute("ap", ap);

        log.info("@@@@");
        log.info("docs size: " + ap.getDocumentos().size());
        log.info("docs reg recibo size: " + ap.getDocumentosRegistroRecepcion().size());
        log.info("docs acuse recibo size: " + ap.getDocumentosAcuseRecibo().size());
        log.info("docs informe respuesta size: " + ap.getDocumentosInformeRespuesta().size());

        return "manageApelacion";
    }

    @PostMapping(path = "/addApelacion", produces = "application/json")
    @ResponseBody
    @Transactional
    public String addApelacion(@RequestBody JsonNode o){
        log.info("@@@@ inside addApelacion");
        String numeroExpediente = o.get("numeroExpediente").asText();
        String acronimo = o.get("acronimo").asText();
        String cliente = o.get("cliente").asText();

        String fechaCertificacionText = o.get("fechaCertificacion").asText();
        LocalDate fechaCertificacion = null;
        if(!fechaCertificacionText.equals("")){
            fechaCertificacion = LocalDate.parse(fechaCertificacionText);
        }

        String fechaRegistroRecepcionText = o.get("fechaRegistroRecepcion").asText();
        LocalDate fechaRegistroRecepcion = null;
        if(!fechaRegistroRecepcionText.equals("")){
            fechaRegistroRecepcion = LocalDate.parse(fechaRegistroRecepcionText);
        }
        String fechaAcuseReciboText = o.get("fechaAcuseRecibo").asText();
        LocalDate fechaAcuseRecibo = null;
        if(!fechaAcuseReciboText.equals("")){
            fechaAcuseRecibo = LocalDate.parse(fechaAcuseReciboText);
        }
        String fechaInformeRespuestaText = o.get("fechaInformeRespuesta").asText();
        LocalDate fechaInformeRespuesta = null;
        if(!fechaInformeRespuestaText.equals("")){
            fechaInformeRespuesta = LocalDate.parse(fechaInformeRespuestaText);
        }

        String plazoLimiteText = o.get("plazoLimite").asText();
        LocalDate plazoLimite = null;
        if(!plazoLimiteText.equals("")){
            plazoLimite = LocalDate.parse(plazoLimiteText);
        }

        Boolean revisionDocumental = o.get("revisionDocumental").asBoolean();
        Boolean solicitudRequerimientos = o.get("solicitudRequerimientos").asBoolean();
        Boolean imparcialidadExpertos = o.get("imparcialidadExpertos").asBoolean();
        Boolean revisionInforme4d = o.get("revisionInforme4d").asBoolean();
        Boolean verificacionCompetencias = o.get("verificacionCompetencias").asBoolean();
        Boolean revisionInformeTecnico = o.get("revisionInformeTecnico").asBoolean();
        Boolean revisionEvaluacionContable = o.get("revisionEvaluacionContable").asBoolean();
        Boolean documentoCertificacion = o.get("documentoCertificacion").asBoolean();
        
        String revisionDocumentalText = o.get("revisionDocumentalText").asText();
        String solicitudRequerimientosText = o.get("solicitudRequerimientosText").asText();
        String imparcialidadExpertosText = o.get("imparcialidadExpertosText").asText();
        String revisionInforme4dText = o.get("revisionInforme4dText").asText();
        String verificacionCompetenciasText = o.get("verificacionCompetenciasText").asText();
        String revisionInformeTecnicoText = o.get("revisionInformeTecnicoText").asText();
        String revisionEvaluacionContableText = o.get("revisionEvaluacionContableText").asText();
        String documentoCertificacionText = o.get("documentoCertificacionText").asText();

        String resolucion = o.get("resolucion").asText();

        List<Long> ncIds = new ArrayList<>();
        if (o.get("ncIds").isArray()) {
            for (final JsonNode objNode : o.get("ncIds")) {
                ncIds.add(objNode.asLong());
            }
        }

        String comentarios = o.get("comentarios").asText();

        long idAp = apDB.addApelacion(em, numeroExpediente, acronimo, cliente, fechaCertificacion, 
                                            fechaRegistroRecepcion, fechaAcuseRecibo, fechaInformeRespuesta, revisionDocumental, 
                                            revisionDocumentalText, solicitudRequerimientos, solicitudRequerimientosText, 
                                            revisionInforme4d, revisionInforme4dText, verificacionCompetencias, verificacionCompetenciasText,
                                            revisionInformeTecnico, revisionInformeTecnicoText, revisionEvaluacionContable, revisionEvaluacionContableText,
                                            imparcialidadExpertos, imparcialidadExpertosText,
                                            ncIds, resolucion, plazoLimite, documentoCertificacion, documentoCertificacionText, comentarios);
        
        return "{\"apId\": "+idAp+"}";
    }

    @PostMapping(path = "/deleteApelacion", produces = "application/json")
    @ResponseBody
    @Transactional
    public String deleteApelacion(@RequestBody JsonNode o){
        log.info("@@@@ inside deleteApelacion");
        
        Long apId = o.get("apId").asLong();

        Apelacion ap = apDB.getApelacionById(em, apId);

        for(ApFile apFile: ap.getDocumentos()) {
            CustomFile file = apDB.getCustomFileById(em, apFile.getArchivo().getId());
            String filePath = "src/main/resources/static/media/apelaciones/" + file.getId() + file.getExtension();
            deleteFile(filePath);
        }

        apDB.deleteApelacion(em, ap);
        
        return "{\"isok\": \"isok\"}";
    }
    
    @PostMapping(path = "/updateApelacion", produces = "application/json")
    @ResponseBody
    @Transactional
    public String updateApelacion(@RequestBody JsonNode o){
        log.info("@@@@ inside updateApelacion");

        Long apId = o.get("apId").asLong();
        String numeroExpediente = o.get("numeroExpediente").asText();
        String acronimo = o.get("acronimo").asText();
        String cliente = o.get("cliente").asText();

        String fechaCertificacionText = o.get("fechaCertificacion").asText();
        LocalDate fechaCertificacion = null;
        if(!fechaCertificacionText.equals("")){
            fechaCertificacion = LocalDate.parse(fechaCertificacionText);
        }

        String fechaRegistroRecepcionText = o.get("fechaRegistroRecepcion").asText();
        LocalDate fechaRegistroRecepcion = null;
        if(!fechaRegistroRecepcionText.equals("")){
            fechaRegistroRecepcion = LocalDate.parse(fechaRegistroRecepcionText);
        }
        String fechaAcuseReciboText = o.get("fechaAcuseRecibo").asText();
        LocalDate fechaAcuseRecibo = null;
        if(!fechaAcuseReciboText.equals("")){
            fechaAcuseRecibo = LocalDate.parse(fechaAcuseReciboText);
        }
        String fechaInformeRespuestaText = o.get("fechaInformeRespuesta").asText();
        LocalDate fechaInformeRespuesta = null;
        if(!fechaInformeRespuestaText.equals("")){
            fechaInformeRespuesta = LocalDate.parse(fechaInformeRespuestaText);
        }

        String plazoLimiteText = o.get("plazoLimite").asText();
        LocalDate plazoLimite = null;
        if(!plazoLimiteText.equals("")){
            plazoLimite = LocalDate.parse(plazoLimiteText);
        }

        Boolean revisionDocumental = o.get("revisionDocumental").asBoolean();
        Boolean solicitudRequerimientos = o.get("solicitudRequerimientos").asBoolean();
        Boolean imparcialidadExpertos = o.get("imparcialidadExpertos").asBoolean();
        Boolean revisionInforme4d = o.get("revisionInforme4d").asBoolean();
        Boolean verificacionCompetencias = o.get("verificacionCompetencias").asBoolean();
        Boolean revisionInformeTecnico = o.get("revisionInformeTecnico").asBoolean();
        Boolean revisionEvaluacionContable = o.get("revisionEvaluacionContable").asBoolean();
        Boolean documentoCertificacion = o.get("documentoCertificacion").asBoolean();
        
        String revisionDocumentalText = o.get("revisionDocumentalText").asText();
        String solicitudRequerimientosText = o.get("solicitudRequerimientosText").asText();
        String imparcialidadExpertosText = o.get("imparcialidadExpertosText").asText();
        String revisionInforme4dText = o.get("revisionInforme4dText").asText();
        String verificacionCompetenciasText = o.get("verificacionCompetenciasText").asText();
        String revisionInformeTecnicoText = o.get("revisionInformeTecnicoText").asText();
        String revisionEvaluacionContableText = o.get("revisionEvaluacionContableText").asText();
        String documentoCertificacionText = o.get("documentoCertificacionText").asText();

        String resolucion = o.get("resolucion").asText();

        List<Long> ncIds = new ArrayList<>();
        if (o.get("ncIds").isArray()) {
            for (final JsonNode objNode : o.get("ncIds")) {
                ncIds.add(objNode.asLong());
            }
        }

        String comentarios = o.get("comentarios").asText();

        apDB.updateApelacion(em, numeroExpediente, acronimo, cliente, fechaCertificacion, 
                                fechaRegistroRecepcion, fechaAcuseRecibo, fechaInformeRespuesta, revisionDocumental, 
                                revisionDocumentalText, solicitudRequerimientos, solicitudRequerimientosText, 
                                revisionInforme4d, revisionInforme4dText, verificacionCompetencias, verificacionCompetenciasText,
                                revisionInformeTecnico, revisionInformeTecnicoText, revisionEvaluacionContable, revisionEvaluacionContableText,
                                imparcialidadExpertos, imparcialidadExpertosText,
                                ncIds, resolucion, apId, plazoLimite, documentoCertificacion, documentoCertificacionText, comentarios);
        
        return "{\"isok\": \"isok\"}";
    }

    @PostMapping(path = "/bindNC", produces = "application/json")
    @ResponseBody
    @Transactional
    public String bindNC(@RequestBody JsonNode o){
        log.info("@@@@ inside bindNC");
        
        Long apId = o.get("apId").asLong();
        Long ncId = o.get("ncId").asLong();

        apDB.bindNC(em, apId, ncId);
        
        return "{\"isok\": \"isok\"}";
    }

    @PostMapping(path = "/unbindNC", produces = "application/json")
    @ResponseBody
    @Transactional
    public String unbindNC(@RequestBody JsonNode o){
        log.info("@@@@ inside unbindNC");
        
        Long apId = o.get("apId").asLong();
        Long ncId = o.get("ncId").asLong();

        apDB.unbindNC(em, apId, ncId);
        
        return "{\"isok\": \"isok\"}";
    }

    @PostMapping(path = "/ncAlreadyBinded", produces = "application/json")
    @ResponseBody
    @Transactional
    public Boolean ncAlreadyBinded(@RequestBody JsonNode o){
        log.info("@@@@ inside ncAlreadyBinded");
        
        Long apId = o.get("apId").asLong();
        Long ncId = o.get("ncId").asLong();
        
        return apDB.ncAlreadyBinded(em, apId, ncId);
    }

    @PostMapping(path = "/searchApelacion", produces = "application/json")
    @ResponseBody
    @Transactional
    public List<Apelacion> searchApelacion(@RequestBody JsonNode o){
        log.info("@@@@ inside searchApelacion");
        
        String fechaRecepcionText = o.get("fechaRecepcion").asText();
        LocalDate fechaRecepcion = null;
        if(!fechaRecepcionText.equals("")){
            fechaRecepcion = LocalDate.parse(fechaRecepcionText);
        }
        
        Long id = o.get("id").asLong();
        String numeroExpediente = o.get("numeroExpediente").asText();
        String acronimo = o.get("acronimo").asText();
        String resolucion = o.get("resolucion").asText();


        List<Apelacion> apList = apDB.searchApelacion(em, fechaRecepcion, id, numeroExpediente, acronimo, resolucion);

        for(Apelacion ap: apList){
            log.info("dentro del for: " + ap.getId());
        }
        
 
        return apList;
    }

    @PostMapping(path = "/sortCurrentApelacionList", produces = "application/json")
    @ResponseBody
    @Transactional
    public List<Apelacion> sortCurrentApelacionList(@RequestBody JsonNode o){
        log.info("@@@@ inside sortCurrentApelacionList");

        String fechaRecepcionText = o.get("fechaRecepcion").asText();
        LocalDate fechaRecepcion = null;
        if(!fechaRecepcionText.equals("")){
            fechaRecepcion = LocalDate.parse(fechaRecepcionText);
        }
        
        Long id = o.get("id").asLong();
        String numeroExpediente = o.get("numeroExpediente").asText();
        String acronimo = o.get("acronimo").asText();
        
        String sort = o.get("sort").asText();
        String reversedText = o.get("order").asText();
        Boolean reversed = null;
        if(!reversedText.equals("")) {
            reversed = Boolean.parseBoolean(reversedText);
        }
        String resolucion = o.get("resolucion").asText();

        List<Apelacion> apList = apDB.searchApelacion(em, fechaRecepcion, id, numeroExpediente, acronimo, resolucion); 

        if(sort.equals("byId")){
            if(reversed != null && reversed){
                Collections.sort(apList, (ap1, ap2) -> Long.compare(ap2.getId(), ap1.getId())); // Descending
            } else if (reversed != null && !reversed){
                Collections.sort(apList, (ap1, ap2) -> Long.compare(ap1.getId(), ap2.getId())); // Ascending
            }
    
        } else if (sort.equals("byFechaRecepcion")){
            if(reversed != null && reversed){
                // descending
                Collections.sort(apList, (ap1, ap2) -> ap2.getFechaRegistroRecepcion().compareTo(ap1.getFechaRegistroRecepcion()));
            } else if (reversed != null && !reversed){
                // ascending
                Collections.sort(apList, (ap1, ap2) -> ap1.getFechaRegistroRecepcion().compareTo(ap2.getFechaRegistroRecepcion()));
            }
        }
 
        return apList;
    }

    @PostMapping("/addApFile")
    @Transactional
    @ResponseBody 
    public ApFile addApFile(@RequestParam("archivo") MultipartFile archivo, @RequestParam("apId") long apId,
                                            @RequestParam("name") String name, @RequestParam("extension") String extension, @RequestParam("fileClass") String fileClass)
    {
        log.info("---- inside addApFile ----");

        long apFileId = apDB.createApFile(em, fileClass, apId);

        long fileId = apDB.setApFile(em, apFileId, name, extension);

        File doc = new File("media-files/apelaciones", fileId + extension);

        log.info("apId: " + apId + " archivo: " + archivo + " fileClass: " + fileClass + " fileName: " + name + " fileExtension: " + extension + " fileId: " + fileId);

        if (archivo.isEmpty()) {
        log.info("failed to upload video: emtpy file?");
        return null;
        } else {
            try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(doc))) {
                byte[] bytes = archivo.getBytes();
                stream.write(bytes);
                /* log.info("la ruta es: " + doc.getAbsolutePath()); */
            } catch (Exception e) {
                log.error("Failed to save the file: " + name + ". Error: " + e.getMessage(), e);
                return null;
            }
        }

        ApFile apFile = apDB.getApFileById(em, apFileId);
        return apFile;
    }

    @PostMapping(path = "/deleteDocumento", produces = "application/json")
    @ResponseBody
    @Transactional
    public String deleteDocumento(@RequestBody JsonNode o){
        log.info("@@@@ inside deleteDocumento");
        
        Long apId = o.get("apId").asLong();
        Long fileId = o.get("fileId").asLong();
        Long apFileId = o.get("apFileId").asLong();

        CustomFile file = apDB.getCustomFileById(em, fileId); 

        String filePath = "src/main/resources/static/media/apelaciones/" + file.getId() + file.getExtension();

        deleteFile(filePath);

        log.info("#### antes de los deletes");

        apDB.deleteCustomFile(em, fileId, apFileId);  

        log.info("#### despues primer delete");

        apDB.deleteApFile(em, apFileId);
 
        return "{\"isok\": \"isok\"}";
    }

    public void deleteFile(String filePath) {
        try {
            /* log.info("#### " + Paths.get(filePath)); */
            Files.delete(Paths.get(filePath));
        } catch (IOException e) {
            // handle exception or re-throw
        }
    }

}


