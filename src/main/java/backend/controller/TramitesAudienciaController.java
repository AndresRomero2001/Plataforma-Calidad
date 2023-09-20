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

import backend.model.Accion;
import backend.model.CustomFile;
import backend.model.Evidencia;
import backend.model.NoConformidad;
import backend.model.TramiteAudiencia;
import backend.model.dataBase.DB;
import backend.model.dataBase.TADB;

/**
 *  Non-authenticated requests only.
 */
@Controller
@RequestMapping("ta")
public class TramitesAudienciaController {

    @Autowired
    private EntityManager em;

    private TADB taDB = new TADB();

	private static final Logger log = LogManager.getLogger(TramitesAudienciaController.class);

    @GetMapping("/tramitesAudiencia")
    public String tramitesAudiencia(Model model, @RequestParam(required = false) String resolucionDefinitiva) {
        List<TramiteAudiencia> taList = taDB.getTramitesAudiencia(em);
    
        if(resolucionDefinitiva != null) {
            if(resolucionDefinitiva.equals("FavorableParcial")) resolucionDefinitiva = "Favorable parcial";
            taList = taDB.getTramitesAudienciaByResolucionDefinitiva(em, resolucionDefinitiva);
        }

        Collections.sort(taList, Comparator.comparing(TramiteAudiencia::getNumeroTA));
        Collections.reverse(taList);

        model.addAttribute("taList", taList);

        return "tramitesAudiencia";
    }

    @GetMapping("/addTramiteAudiencia")
    public String addTramiteAudiencia(Model model) {
        
        return "addTramiteAudiencia";
    }

    @GetMapping("/manageTramiteAudiencia")
    public String manageTramiteAudiencia(Model model, HttpSession session, @RequestParam(required = true) Long taId) {
        TramiteAudiencia ta = em.find(TramiteAudiencia.class, taId);
        model.addAttribute("ta", ta);
        return "manageTramiteAudiencia";
    }

    @PostMapping(path = "/addTramiteAudiencia", produces = "application/json")
    @ResponseBody
    @Transactional
    public String addTramiteAudiencia(@RequestBody JsonNode o){
        log.info("@@@@ inside addTramiteAudiencia");
        Integer numeroTA = o.get("numeroTA").asInt();
        String numeroExpediente = o.get("numeroExpediente").asText();
        String acronimo = o.get("acronimo").asText();
        Integer ejercicioFiscal = o.get("ejercicioFiscal").asInt();
        String numeroIMV = o.get("numeroIMV").asText();
        String cliente = o.get("cliente").asText();
        String resolucion = o.get("resolucion").asText();
        String llegadaNotificacionText = o.get("llegadaNotificacion").asText();
        LocalDate llegadaNotificacion = null;
        if(!llegadaNotificacionText.equals("")){
            llegadaNotificacion = LocalDate.parse(llegadaNotificacionText);
        }
        String consultora = o.get("consultora").asText();
        String experto4d = o.get("experto4d").asText();
        String expertoTecnico = o.get("expertoTecnico").asText();
        String motivo = o.get("motivo").asText();
        String resolucionDefinitiva = o.get("resolucionDefinitiva").asText();

        String fechaInterposicionText = o.get("fechaInterposicion").asText();
        LocalDate fechaInterposicion = null;
        if(!fechaInterposicionText.equals("")){
            fechaInterposicion = LocalDate.parse(fechaInterposicionText);
        }
        String fechaResolucionMinisterioText = o.get("fechaResolucionMinisterio").asText();
        LocalDate fechaResolucionMinisterio = null;
        if(!fechaResolucionMinisterioText.equals("")){
            fechaResolucionMinisterio = LocalDate.parse(fechaResolucionMinisterioText);
        }
        String fechaEmisionInformeText = o.get("fechaEmisionInforme").asText();
        LocalDate fechaEmisionInforme = null;
        if(!fechaEmisionInformeText.equals("")){
            fechaEmisionInforme = LocalDate.parse(fechaEmisionInformeText);
        }

        List<Integer> codigosUnesco = new ArrayList<>();
        if (o.get("codigosUnesco").isArray()) {
            for (final JsonNode objNode : o.get("codigosUnesco")) {
                codigosUnesco.add(objNode.asInt());
            }
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

        List<Long> ncIds = new ArrayList<>();
        if (o.get("ncIds").isArray()) {
            for (final JsonNode objNode : o.get("ncIds")) {
                ncIds.add(objNode.asLong());
            }
        }

        String comentarios = o.get("comentarios").asText();

        long taId = taDB.addTramiteAudiencia(em, numeroTA, numeroExpediente, acronimo, ejercicioFiscal, numeroIMV, cliente, resolucion, 
                                            llegadaNotificacion, consultora, experto4d, expertoTecnico, motivo, 
                                            resolucionDefinitiva, fechaInterposicion, fechaResolucionMinisterio, fechaEmisionInforme,
                                            codigosUnesco, revisionDocumental, revisionDocumentalText, solicitudRequerimientos, solicitudRequerimientosText, 
                                            revisionInforme4d, revisionInforme4dText, verificacionCompetencias, verificacionCompetenciasText,
                                            revisionInformeTecnico, revisionInformeTecnicoText, revisionEvaluacionContable, revisionEvaluacionContableText,
                                            documentoCertificacion, documentoCertificacionText, imparcialidadExpertos, imparcialidadExpertosText,
                                            ncIds, comentarios);
        
        return "{\"taId\": "+taId+"}";
    }

    @PostMapping(path = "/updateTramiteAudiencia", produces = "application/json")
    @ResponseBody
    @Transactional
    public String updateTramiteAudiencia(@RequestBody JsonNode o){
        log.info("@@@@ inside updateTramiteAudiencia");
        Integer numeroTA = o.get("numeroTA").asInt();
        String numeroExpediente = o.get("numeroExpediente").asText();
        String acronimo = o.get("acronimo").asText();
        Integer ejercicioFiscal = o.get("ejercicioFiscal").asInt();
        String numeroIMV = o.get("numeroIMV").asText();
        String cliente = o.get("cliente").asText();
        String resolucion = o.get("resolucion").asText();
        String llegadaNotificacionText = o.get("llegadaNotificacion").asText();
        LocalDate llegadaNotificacion = null;
        if(!llegadaNotificacionText.equals("")){
            llegadaNotificacion = LocalDate.parse(llegadaNotificacionText);
        }
        String consultora = o.get("consultora").asText();
        String experto4d = o.get("experto4d").asText();
        String expertoTecnico = o.get("expertoTecnico").asText();
        String motivo = o.get("motivo").asText();
        String resolucionDefinitiva = o.get("resolucionDefinitiva").asText();

        String fechaInterposicionText = o.get("fechaInterposicion").asText();
        LocalDate fechaInterposicion = null;
        if(!fechaInterposicionText.equals("")){
            fechaInterposicion = LocalDate.parse(fechaInterposicionText);
        }
        String fechaResolucionMinisterioText = o.get("fechaResolucionMinisterio").asText();
        LocalDate fechaResolucionMinisterio = null;
        if(!fechaResolucionMinisterioText.equals("")){
            fechaResolucionMinisterio = LocalDate.parse(fechaResolucionMinisterioText);
        }
        String fechaEmisionInformeText = o.get("fechaEmisionInforme").asText();
        LocalDate fechaEmisionInforme = null;
        if(!fechaEmisionInformeText.equals("")){
            fechaEmisionInforme = LocalDate.parse(fechaEmisionInformeText);
        }

        List<Integer> codigosUnesco = new ArrayList<>();
        if (o.get("codigosUnesco").isArray()) {
            for (final JsonNode objNode : o.get("codigosUnesco")) {
                codigosUnesco.add(objNode.asInt());
            }
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

        List<Long> ncIds = new ArrayList<>();
        if (o.get("ncIds").isArray()) {
            for (final JsonNode objNode : o.get("ncIds")) {
                ncIds.add(objNode.asLong());
            }
        }

        String comentarios = o.get("comentarios").asText();
        Long taId = o.get("taId").asLong();

        taDB.updateTramiteAudiencia(em, numeroTA, numeroExpediente, acronimo, ejercicioFiscal, numeroIMV, cliente, resolucion, 
                                        llegadaNotificacion, consultora, experto4d, expertoTecnico, motivo, 
                                        resolucionDefinitiva, fechaInterposicion, fechaResolucionMinisterio, fechaEmisionInforme,
                                        codigosUnesco, revisionDocumental, revisionDocumentalText, solicitudRequerimientos, solicitudRequerimientosText, 
                                        revisionInforme4d, revisionInforme4dText, verificacionCompetencias, verificacionCompetenciasText,
                                        revisionInformeTecnico, revisionInformeTecnicoText, revisionEvaluacionContable, revisionEvaluacionContableText,
                                        documentoCertificacion, documentoCertificacionText, imparcialidadExpertos, imparcialidadExpertosText,
                                        ncIds, comentarios, taId);
        
        return "{\"idTA\": "+0+"}";
    }

    @PostMapping(path = "/deleteTramiteAudiencia", produces = "application/json")
    @ResponseBody
    @Transactional
    public String deleteTramiteAudiencia(@RequestBody JsonNode o){
        log.info("@@@@ inside deleteTramiteAudiencia");
        
        Long taId = o.get("taId").asLong();

        TramiteAudiencia ta = taDB.getTramiteAudienciaById(em, taId);

        taDB.deleteTramiteAudiencia(em, ta);
        
        return "{\"isok\": \"isok\"}";
    }

    @PostMapping(path = "/bindNC", produces = "application/json")
    @ResponseBody
    @Transactional
    public String bindNC(@RequestBody JsonNode o){
        log.info("@@@@ inside bindNC");
        
        Long taId = o.get("taId").asLong();
        Long ncId = o.get("ncId").asLong();

        taDB.bindNC(em, taId, ncId);
        
        return "{\"isok\": \"isok\"}";
    }

    @PostMapping(path = "/unbindNC", produces = "application/json")
    @ResponseBody
    @Transactional
    public String unbindNC(@RequestBody JsonNode o){
        log.info("@@@@ inside unbindNC");
        
        Long taId = o.get("taId").asLong();
        Long ncId = o.get("ncId").asLong();

        taDB.unbindNC(em, taId, ncId);
        
        return "{\"isok\": \"isok\"}";
    }

    @PostMapping(path = "/unbindCodigoUnesco", produces = "application/json")
    @ResponseBody
    @Transactional
    public String unbindCodigoUnesco(@RequestBody JsonNode o){
        log.info("@@@@ inside unbindCodigoUnesco");
        
        Long taId = o.get("taId").asLong();
        Integer cuId = o.get("cuId").asInt();

        taDB.unbindCodigoUnesco(em, taId, cuId);
        
        return "{\"isok\": \"isok\"}";
    }

    @PostMapping(path = "/ncAlreadyBinded", produces = "application/json")
    @ResponseBody
    @Transactional
    public Boolean ncAlreadyBinded(@RequestBody JsonNode o){
        log.info("@@@@ inside ncAlreadyBinded");
        
        Long taId = o.get("taId").asLong();
        Long ncId = o.get("ncId").asLong();
        
        return taDB.ncAlreadyBinded(em, taId, ncId);
    }

    @PostMapping(path = "/searchTramiteAudiencia", produces = "application/json")
    @ResponseBody
    @Transactional
    public List<TramiteAudiencia> searchTramiteAudiencia(@RequestBody JsonNode o){
        log.info("@@@@ inside searchTramiteAudiencia");
        
        Integer numeroTA = o.get("numeroTA").asInt();
        String resolucionDefinitiva = o.get("resolucionDefinitiva").asText();
        Integer ejercicioFiscal = o.get("ejercicioFiscal").asInt();
        String consultora = o.get("consultora").asText();
        
        Long id = o.get("id").asLong();
        String numeroExpediente = o.get("numeroExpediente").asText();
        String acronimo = o.get("acronimo").asText();

        String numeroIMV = o.get("numeroIMV").asText();
        String cliente = o.get("cliente").asText();
        String motivo = o.get("motivo").asText();
        String experto4d = o.get("experto4d").asText();
        String expertoTecnico = o.get("expertoTecnico").asText();
        String resolucion = o.get("resolucion").asText();
        Integer codigoUnesco = o.get("codigoUnesco").asInt();

        String llegadaNotificacionText = o.get("llegadaNotificacion").asText();
        LocalDate llegadaNotificacion = null;
        if(!llegadaNotificacionText.equals("")){
            llegadaNotificacion = LocalDate.parse(llegadaNotificacionText);
        }

        resolucionDefinitiva = resolucionDefinitiva.replace("'", "");
        resolucion = resolucion.replace("'", "");


        List<TramiteAudiencia> taList = taDB.searchTramiteAudiencia(em, numeroTA, resolucionDefinitiva, ejercicioFiscal, consultora, id, numeroExpediente, acronimo,
                                                numeroIMV, cliente, motivo, experto4d, expertoTecnico, resolucion, llegadaNotificacion, codigoUnesco);

        for(TramiteAudiencia ta: taList){
            log.info("dentro del for: " + ta.getId());
        }
        
 
        return taList;
    }

    @PostMapping(path = "/sortCurrentTramiteAudienciaList", produces = "application/json")
    @ResponseBody
    @Transactional
    public List<TramiteAudiencia> sortCurrentTramiteAudienciaList(@RequestBody JsonNode o){
        log.info("@@@@ inside sortCurrentTramiteAudienciaList");

        Integer numeroTA = o.get("numeroTA").asInt();
        String resolucionDefinitiva = o.get("resolucionDefinitiva").asText();
        Integer ejercicioFiscal = o.get("ejercicioFiscal").asInt();
        String consultora = o.get("consultora").asText();
        Long id = o.get("id").asLong();
        String numeroExpediente = o.get("numeroExpediente").asText();
        String acronimo = o.get("acronimo").asText();
        resolucionDefinitiva = resolucionDefinitiva.replace("'", "");

        String numeroIMV = o.get("numeroIMV").asText();
        String cliente = o.get("cliente").asText();
        String motivo = o.get("motivo").asText();
        String experto4d = o.get("experto4d").asText();
        String expertoTecnico = o.get("expertoTecnico").asText();
        String resolucion = o.get("resolucion").asText();
        resolucion = resolucion.replace("'", "");
        Integer codigoUnesco = o.get("codigoUnesco").asInt();

        String llegadaNotificacionText = o.get("llegadaNotificacion").asText();
        LocalDate llegadaNotificacion = null;
        if(!llegadaNotificacionText.equals("")){
            llegadaNotificacion = LocalDate.parse(llegadaNotificacionText);
        }
        
        String sort = o.get("sort").asText();
        String reversedText = o.get("order").asText();
        Boolean reversed = null;
        if(!reversedText.equals("")) {
            reversed = Boolean.parseBoolean(reversedText);
        }

        List<TramiteAudiencia> taList = taDB.searchTramiteAudiencia(em, numeroTA, resolucionDefinitiva, ejercicioFiscal, consultora, id, numeroExpediente, acronimo, 
                                                numeroIMV, cliente, motivo, experto4d, expertoTecnico, resolucion, llegadaNotificacion, codigoUnesco);   

        if(sort.equals("byNumeroTA")){
            if(reversed != null && reversed){
                Collections.sort(taList, (ta1, ta2) -> Long.compare(ta2.getNumeroTA(), ta1.getNumeroTA())); // Descending
            } else if (reversed != null && !reversed){
                Collections.sort(taList, (ta1, ta2) -> Long.compare(ta1.getNumeroTA(), ta2.getNumeroTA())); // Ascending
            }
    
        } else if (sort.equals("byEjercicioFiscal")){
            if(reversed != null && reversed){
                // descending
                Collections.sort(taList, (ta1, ta2) -> Integer.compare(ta2.getEjercicioFiscal(), ta1.getEjercicioFiscal()));
            } else if (reversed != null && !reversed){
                // ascending
                Collections.sort(taList, (ta1, ta2) -> Integer.compare(ta1.getEjercicioFiscal(), ta2.getEjercicioFiscal()));
            }
        }
 
        return taList;
    }
}
