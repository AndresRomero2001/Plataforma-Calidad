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
import backend.model.Reclamacion;
import backend.model.ReclamacionFile;
import backend.model.TramiteAudiencia;
import backend.model.dataBase.APDB;
import backend.model.dataBase.RCDB;
import backend.model.dataBase.TADB;

@Controller
@RequestMapping("rc")
public class ReclamacionesController {
    @Autowired
    private EntityManager em;

    private RCDB rcDB = new RCDB();

	private static final Logger log = LogManager.getLogger(ReclamacionesController.class);

    @GetMapping("/reclamaciones")
    public String reclamaciones(Model model, @RequestParam(required = false) String estado) {
        List<Reclamacion> rcList = rcDB.getReclamaciones(em);
        Collections.reverse(rcList);

        if(estado != null) {
            rcList = rcDB.getReclamacionesByEstado(em, estado);
        }

        model.addAttribute("reclamaciones", rcList);

        return "reclamaciones";
    }

    @GetMapping("/addReclamacion")
    public String addReclamacion(Model model) {
        
        return "addReclamacion";
    }

    @GetMapping("/manageReclamacion")
    public String manageReclamacion(Model model, HttpSession session, @RequestParam(required = true) Long rcId) {
        Reclamacion rc = rcDB.getReclamacionById(em, rcId);
        model.addAttribute("rc", rc);

        log.info("docs extra size: " + rc.getDocumentosExtra().size());
        log.info("docs respuesta extra size: " + rc.getDocumentosRespuestaExtra().size());

        return "manageReclamacion";
    }

    @PostMapping(path = "/addReclamacion", produces = "application/json")
    @ResponseBody
    @Transactional
    public String addReclamacion(@RequestBody JsonNode o){
        log.info("@@@@ inside addReclamacion");
        String numeroExpediente = o.get("numeroExpediente").asText();
        String acronimo = o.get("acronimo").asText();
        String consultora = o.get("consultora").asText();
        String estado = o.get("estado").asText();
        String comentarios = o.get("comentarios").asText();

        String fechaRecepcionText = o.get("fechaRecepcion").asText();
        LocalDate fechaRecepcion = null;
        if(!fechaRecepcionText.equals("")){
            fechaRecepcion = LocalDate.parse(fechaRecepcionText);
        }

        String fechaMaximaRespuestaText = o.get("fechaMaximaRespuesta").asText();
        LocalDate fechaMaximaRespuesta = null;
        if(!fechaMaximaRespuestaText.equals("")){
            fechaMaximaRespuesta = LocalDate.parse(fechaMaximaRespuestaText);
        }

        long rcId = rcDB.addReclamacion(em, numeroExpediente, acronimo, consultora, fechaRecepcion, fechaMaximaRespuesta, estado, comentarios);
        
        return "{\"rcId\": "+rcId+"}";
    }

    @PostMapping(path = "/updateReclamacion", produces = "application/json")
    @ResponseBody
    @Transactional
    public String updateReclamacion(@RequestBody JsonNode o){
        log.info("@@@@ inside updateReclamacion");
        String numeroExpediente = o.get("numeroExpediente").asText();
        String acronimo = o.get("acronimo").asText();
        String consultora = o.get("consultora").asText();
        String estado = o.get("estado").asText();
        Long rcId = o.get("rcId").asLong();
        String comentarios = o.get("comentarios").asText();

        String fechaRecepcionText = o.get("fechaRecepcion").asText();
        LocalDate fechaRecepcion = null;
        if(!fechaRecepcionText.equals("")){
            fechaRecepcion = LocalDate.parse(fechaRecepcionText);
        }

        String fechaMaximaRespuestaText = o.get("fechaMaximaRespuesta").asText();
        LocalDate fechaMaximaRespuesta = null;
        if(!fechaMaximaRespuestaText.equals("")){
            fechaMaximaRespuesta = LocalDate.parse(fechaMaximaRespuestaText);
        }

        rcDB.updateReclamacion(em, numeroExpediente, acronimo, consultora, fechaRecepcion, fechaMaximaRespuesta, estado, rcId, comentarios);
        
        return "{\"rcId\": "+rcId+"}";
    }

    @PostMapping("/addReclamacionFile")
    @Transactional
    @ResponseBody 
    public ReclamacionFile addReclamacionFile(@RequestParam("archivo") MultipartFile archivo, @RequestParam("rcId") long rcId,
                                            @RequestParam("name") String name, @RequestParam("extension") String extension, @RequestParam("fileClass") String fileClass)
    {
        log.info("---- inside addReclamacionFile ----");

        long fileId = rcDB.createCustomFile(em, name, extension);
        long rcFileId = rcDB.createReclamacionFile(em, fileClass, rcId, fileId);
        
        rcDB.setRcFile(em, rcId, rcFileId);

        // a diferencia de apelaciones, aqui hay q crear otra carpeta pq hay 2 clases q generan ID, y podrían llegar a coincidir
        // (registroFormulario y documentosExtra) por lo q el path seria el mismo y daría problemas
        File doc = new File("media-files/documentosExtra", fileId + extension);

        log.info("rcId: " + rcId + " archivo: " + archivo + " fileClass: " + fileClass + " fileName: " + name + " fileExtension: " + extension + " fileId: " + fileId);

        if (archivo.isEmpty()) {
        log.info("failed to upload file: emtpy file?");
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

        ReclamacionFile rcFile = rcDB.getRcFileById(em, rcFileId);
        return rcFile;
    }

    @PostMapping("/addRegistroFormulario")
    @Transactional
    @ResponseBody 
    public CustomFile addRegistroFormulario(@RequestParam("archivo") MultipartFile archivo, @RequestParam("rcId") long rcId,
                                            @RequestParam("name") String name, @RequestParam("extension") String extension)
    {
        log.info("---- inside addRegistroFormulario ----");

        long fileId = rcDB.createCustomFile(em, name, extension);

        rcDB.setFormularioRegistro(em, rcId, fileId);

        File doc = new File("media-files/reclamaciones", fileId + extension);

        log.info("rcId: " + rcId + " archivo: " + archivo + " fileName: " + name + " fileExtension: " + extension + " fileId: " + fileId);

        if (archivo.isEmpty()) {
        log.info("failed to upload file: emtpy file?");
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

        CustomFile cf = rcDB.getCustomFileById(em, fileId);
        return cf;
    }

    @PostMapping(path = "/deleteRegistroFormulario", produces = "application/json")
    @ResponseBody
    @Transactional
    public String deleteRegistroFormulario(@RequestBody JsonNode o){
        log.info("@@@@ inside deleteRegistroFormulario");
        
        Long rcId = o.get("rcId").asLong();

        Reclamacion rc = rcDB.getReclamacionById(em, rcId);

        CustomFile file = rc.getRegistroFormulario(); 

        String filePath = "media-files/reclamaciones/" + file.getId() + file.getExtension();

        deleteFile(filePath);

        rcDB.deleteRegistroFormulario(em, rcId);
 
        return "{\"isok\": \"isok\"}";
    }

    @PostMapping(path = "/deleteDocumento", produces = "application/json")
    @ResponseBody
    @Transactional
    public String deleteDocumento(@RequestBody JsonNode o){
        log.info("@@@@ inside deleteDocumento");
        
        Long rcId = o.get("rcId").asLong();
        Long rcFileId = o.get("rcFileId").asLong();

        Reclamacion rc = rcDB.getReclamacionById(em, rcId);

        ReclamacionFile file = rcDB.getReclamacionFileById(em, rcFileId); 

        log.info("rcFileId:" + rcFileId);

        log.info(" name: " + file.getArchivo().getName());

        // a diferencia de apelaciones, aqui hay q crear otra carpeta pq hay 2 clases q generan ID, y podrían llegar a coincidir
        // (registroFormulario y documentosExtra) por lo q el path seria el mismo y daría problemas
        String filePath = "media-files/documentosExtra/" + file.getArchivo().getId() + file.getArchivo().getExtension();

        deleteFile(filePath);

        rcDB.deleteReclamacionFile(em, rcId, rcFileId);
 
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

    @PostMapping(path = "/deleteReclamacion", produces = "application/json")
    @ResponseBody
    @Transactional
    public String deleteReclamacion(@RequestBody JsonNode o){
        log.info("@@@@ inside deleteReclamacion");
        
        Long rcId = o.get("rcId").asLong();

        Reclamacion rc = rcDB.getReclamacionById(em, rcId);

        if(rc.getRegistroFormulario() != null) {
            String filePath = "media-files/reclamaciones/" + rc.getRegistroFormulario().getId() + rc.getRegistroFormulario().getExtension();
            deleteFile(filePath);
            rcDB.deleteRegistroFormulario(em, rcId);
        }

        for(ReclamacionFile rcFile : rc.getDocumentosExtra()){
            String filePath = "media-files/documentosExtra/" + rcFile.getArchivo().getId() + rcFile.getArchivo().getExtension();
            deleteFile(filePath);
            /* rcDB.deleteReclamacionFile(em, rcId, rcFile.getId()); */
        }

        for(ReclamacionFile rcFile : rc.getDocumentosRespuestaExtra()){
            String filePath = "media-files/documentosExtra/" + rcFile.getArchivo().getId() + rcFile.getArchivo().getExtension();
            deleteFile(filePath);
            /* rcDB.deleteReclamacionFile(em, rcId, rcFile.getId()); */
        }

        /* rcDB.deleteAllRcFiles(em, rcId); */

        rcDB.deleteReclamacion(em, rc);
        
        return "{\"isok\": \"isok\"}";
    }

    @PostMapping(path = "/searchReclamacion", produces = "application/json")
    @ResponseBody
    @Transactional
    public List<Reclamacion> searchReclamacion(@RequestBody JsonNode o){
        log.info("@@@@ inside searchReclamacion");
        
        String fechaRecepcionText = o.get("fechaRecepcion").asText();
        LocalDate fechaRecepcion = null;
        if(!fechaRecepcionText.equals("")){
            fechaRecepcion = LocalDate.parse(fechaRecepcionText);
        }
        
        Long id = o.get("id").asLong();
        String numeroExpediente = o.get("numeroExpediente").asText();
        String acronimo = o.get("acronimo").asText();
        String estado = o.get("estado").asText();
        String consultora = o.get("consultora").asText();


        List<Reclamacion> rcList = rcDB.searchReclamacion(em, fechaRecepcion, id, numeroExpediente, acronimo, estado, consultora);

        for(Reclamacion rc: rcList){
            log.info("dentro del for: " + rc.getId());
        }
 
        return rcList;
    }

    @PostMapping(path = "/sortCurrentReclamacionList", produces = "application/json")
    @ResponseBody
    @Transactional
    public List<Reclamacion> sortCurrentReclamacionList(@RequestBody JsonNode o){
        log.info("@@@@ inside sortCurrentReclamacionList");

        String fechaRecepcionText = o.get("fechaRecepcion").asText();
        LocalDate fechaRecepcion = null;
        if(!fechaRecepcionText.equals("")){
            fechaRecepcion = LocalDate.parse(fechaRecepcionText);
        }
        
        Long id = o.get("id").asLong();
        String numeroExpediente = o.get("numeroExpediente").asText();
        String acronimo = o.get("acronimo").asText();
        String estado = o.get("estado").asText();
        String consultora = o.get("consultora").asText();
        
        String sort = o.get("sort").asText();
        String reversedText = o.get("order").asText();
        Boolean reversed = null;
        if(!reversedText.equals("")) {
            reversed = Boolean.parseBoolean(reversedText);
        }

        List<Reclamacion> rcList = rcDB.searchReclamacion(em, fechaRecepcion, id, numeroExpediente, acronimo, estado, consultora); 

        if(sort.equals("byId")){
            if(reversed != null && reversed){
                Collections.sort(rcList, (rc1, rc2) -> Long.compare(rc2.getId(), rc1.getId())); // Descending
            } else if (reversed != null && !reversed){
                Collections.sort(rcList, (rc1, rc2) -> Long.compare(rc1.getId(), rc2.getId())); // Ascending
            }
    
        } else if (sort.equals("byFechaRecepcion")){
            if(reversed != null && reversed){
                // descending
                Collections.sort(rcList, (rc1, rc2) -> rc2.getFechaRecepcion().compareTo(rc1.getFechaRecepcion()));
            } else if (reversed != null && !reversed){
                // ascending
                Collections.sort(rcList, (rc1, rc2) -> rc1.getFechaRecepcion().compareTo(rc2.getFechaRecepcion()));
            }
        }
 
        return rcList;
    }
}
