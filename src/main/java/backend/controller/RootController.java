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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;

import backend.model.Accion;
import backend.model.Apelacion;
import backend.model.CustomFile;
import backend.model.Evidencia;
import backend.model.NoConformidad;
import backend.model.Reclamacion;
import backend.model.TramiteAudiencia;
import backend.model.User;
import backend.model.Accion.AccionType;
import backend.model.dataBase.APDB;
import backend.model.dataBase.DB;
import backend.model.dataBase.RCDB;
import backend.model.dataBase.TADB;

/**
 *  Non-authenticated requests only.
 */
@Controller
public class RootController {

    @Autowired
    private EntityManager em;

    private DB db = new DB();
    private TADB taDB = new TADB();
    private APDB apDB = new APDB();
    private RCDB rcDB = new RCDB();

	private static final Logger log = LogManager.getLogger(RootController.class);

	@GetMapping("/login")
    public String login(Model model) {
        User u = new User();
        return "login";
    }

    @GetMapping("/error")
    public String error(Model model) {
        return "error";
    }

	@GetMapping("/")
    public String index(Model model) {
        int ncAbiertas = 0, ncCerradas = 0, ncPendientes = 0, ncUrgentes = 0;
        List<NoConformidad> noConformidades = db.getNoConformidades(em);

        for(NoConformidad nc: noConformidades){
            if(nc.getEstado().equals("Abierta")) ncAbiertas++;
            if(nc.getEstado().equals("Cerrada")) ncCerradas++;
            if(nc.getEstado().equals("Pendiente")) ncPendientes++;
            if(nc.getEstado().equals("Urgente")) ncUrgentes++;
        }

        model.addAttribute("ncAbiertas", ncAbiertas);
        model.addAttribute("ncCerradas", ncCerradas);
        model.addAttribute("ncPendientes", ncPendientes);
        model.addAttribute("ncUrgentes", ncUrgentes);

        int taFavorables = 0, taDesfavorables = 0, taFavorablesParciales = 0, taSinResolucionDefinitiva = 0;
        List<TramiteAudiencia> tramitesAudiencia = taDB.getTramitesAudiencia(em);

        for(TramiteAudiencia ta: tramitesAudiencia){
            if("Favorable".equals(ta.getResolucionDefinitiva())) taFavorables++;
            if("Desfavorable".equals(ta.getResolucionDefinitiva())) taDesfavorables++;
            if("Favorable parcial".equals(ta.getResolucionDefinitiva())) taFavorablesParciales++;
            if(ta.getResolucionDefinitiva() == null || "".equals(ta.getResolucionDefinitiva())) taSinResolucionDefinitiva++;

        }

        model.addAttribute("taFavorables", taFavorables);
        model.addAttribute("taDesfavorables", taDesfavorables);
        model.addAttribute("taFavorablesParciales", taFavorablesParciales);
        model.addAttribute("taSinResolucionDefinitiva", taSinResolucionDefinitiva);

        int apEstimadas = 0, apDesestimadas = 0, apSinResolucion = 0;
        List<Apelacion> apelaciones = apDB.getApelaciones(em);
        for(Apelacion ap: apelaciones){
            if("Estimada".equals(ap.getResolucion())) apEstimadas++;
            if("Desestimada".equals(ap.getResolucion())) apDesestimadas++;
            if(ap.getResolucion() == null || "".equals(ap.getResolucion())) apSinResolucion++;
        }

        model.addAttribute("apEstimadas", apEstimadas);
        model.addAttribute("apDesestimadas", apDesestimadas);
        model.addAttribute("apSinResolucion", apSinResolucion);

        int rcAbiertas = 0, rcCerradas = 0;
        List<Reclamacion> reclamaciones = rcDB.getReclamaciones(em);
        for(Reclamacion rc: reclamaciones){
            if("Abierta".equals(rc.getEstado())) rcAbiertas++;
            if("Cerrada".equals(rc.getEstado())) rcCerradas++;
        }

        model.addAttribute("rcAbiertas", rcAbiertas);
        model.addAttribute("rcCerradas", rcCerradas);

        return "index";
    }

    @GetMapping("/noConformidades")
    public String noConformidades(Model model, @RequestParam(required = false) String estado) {
        List<NoConformidad> noConformidades = db.getNoConformidades(em);
        Collections.sort(noConformidades, Comparator.comparing(NoConformidad::getId));
        Collections.reverse(noConformidades);
        
        if(estado != null){
            log.info("#### " + estado);
            noConformidades = db.getNoConformidadesByEstado(em, estado);
        }

        model.addAttribute("noConformidades", noConformidades);

        return "noConformidades";
    }

    /* @GetMapping("/showNoConformidad")
    public String showNoConformidad(Model model, @RequestParam(required = true) Long ncId) {
        NoConformidad nc = db.getNoConformidadById(em, ncId);
        model.addAttribute("nc", nc);

        return "showNoConformidad";
    } */

    @GetMapping("/addNoConformidad")
    public String addNoConformidad(Model model) {
        return "addNoConformidad";
    }

    @GetMapping("/manageNoConformidad")
    public String manageNoConformidad(Model model, HttpSession session, @RequestParam(required = true) Long ncId) {
        NoConformidad nc = db.getNoConformidadById(em, ncId);
        
        List<Accion> sortedAcciones = nc.getAcciones().stream()
            .sorted(Comparator.comparing(a -> a.getTipo().name()))
            .collect(Collectors.toList());
            
        nc.setAcciones(sortedAcciones);

        boolean otraCausa = false;
        String otraCausaValue = "";
        for (String causa : nc.getCausas()) {
            switch (causa) {
                case "procedimientos":
                case "registro":
                case "revision":
                case "experto4d":
                case "expertoTecnico":
                case "expertoContable":
                case "gestor":
                case "consultora":
                    break;
                default:
                    otraCausa = true;
                    otraCausaValue = causa;
                    break;
            }
            if (otraCausa) {
                break;
            }
        }
        model.addAttribute("otraCausa", otraCausa);
        model.addAttribute("otraCausaValue", otraCausaValue);

        model.addAttribute("nc", nc);
        
        return "manageNoConformidad";
    }


    @PostMapping(path = "/addNoConformidad", produces = "application/json")
    @ResponseBody
    @Transactional
    public String addNoConformidad(@RequestBody JsonNode o){
        log.info("@@@@ inside addNoConformidad");
        String origen = o.get("origen").asText();
        String fechaText = o.get("fecha").asText();
        LocalDate fecha = null;
        if(!fechaText.equals("")){
            fecha = LocalDate.parse(fechaText);
        }
        String alcance = o.get("alcance").asText();
        String detectadaPor = o.get("detectadaPor").asText();
        String descripcionDesviacion = o.get("descripcionDesviacion").asText();
        String numeroExpediente = o.get("numeroExpediente").asText();
        String acronimoExpediente = o.get("acronimoExpediente").asText();
        String apartadosNorma = o.get("apartadosNorma").asText();
        String consultora = o.get("consultora").asText();
        /* String analisisCausas = o.get("analisisCausas").asText(); */
        Boolean analisisExtension = o.get("analisisExtension").asBoolean();
        String asunto = o.get("asunto").asText();
        String explicacionCausas = o.get("explicacionCausas").asText();
        String explicacionExtension = o.get("explicacionExtension").asText();
        Boolean aplicaExpediente = o.get("aplicaExpediente").asBoolean();

        List<String> analisisCausasList = new ArrayList<>();
        if (o.get("analisisCausas").isArray()) {
            for (final JsonNode objNode : o.get("analisisCausas")) {
                analisisCausasList.add(objNode.asText());
            }
        }

        long idNC = db.addNoConformidad(em, origen, fecha, alcance, detectadaPor, descripcionDesviacion, numeroExpediente, 
                            acronimoExpediente, apartadosNorma, consultora, analisisCausasList, analisisExtension,
                            asunto, explicacionCausas, explicacionExtension, aplicaExpediente);

        /* NoConformidad nc = db.getNoConformidadById(em, idNC);
        log.info("#### nc.analisisExtension" + nc.getAnalisisExtension()); */
        
        return "{\"idNC\": "+idNC+"}";
    }

    @PostMapping(path = "/deleteNoConformidad", produces = "application/json")
    @ResponseBody
    @Transactional
    public String deleteNoConformidad(@RequestBody JsonNode o){
        log.info("@@@@ inside deleteNoConformidad");
        
        Long ncId = o.get("ncId").asLong();

        NoConformidad nc = db.getNoConformidadById(em, ncId);

        if(nc.getDocumentoExtension() != null){
            Long fileId = nc.getDocumentoExtension().getId();
            CustomFile file = db.getCustomFileById(em, fileId); 

            String filePath = "src/main/resources/static/media/documentosExtension/" + file.getId() + file.getExtension();
            deleteFile(filePath);
        }

        for(Accion a: nc.getAcciones()){
            for(Evidencia e: a.getEvidencias()){
                CustomFile file = db.getCustomFileById(em, e.getArchivo().getId());
                String filePath = "src/main/resources/static/media/evidencias/" + file.getId() + file.getExtension();
                deleteFile(filePath);
            }
        }

        db.deleteNoConformidad(em, ncId);
        
        return "{\"isok\": \"isok\"}";
    }

    @PostMapping(path = "/updateNoConformidad", produces = "application/json")
    @ResponseBody
    @Transactional
    public String updateNoConformidad(@RequestBody JsonNode o){
        log.info("@@@@ inside updateNoConformidad");
        String origen = o.get("origen").asText();
        String fechaText = o.get("fecha").asText();
        LocalDate fecha = null;
        if(!fechaText.equals("")){
            fecha = LocalDate.parse(fechaText);
        }
        String alcance = o.get("alcance").asText();
        String detectadaPor = o.get("detectadaPor").asText();
        String descripcionDesviacion = o.get("descripcionDesviacion").asText();
        String numeroExpediente = o.get("numeroExpediente").asText();
        String acronimoExpediente = o.get("acronimoExpediente").asText();
        String apartadoNorma = o.get("apartadoNorma").asText();
        String consultora = o.get("consultora").asText();
        /* String analisisCausas = o.get("analisisCausas").asText(); */
        Boolean analisisExtension = o.get("analisisExtension").asBoolean();
        String asunto = o.get("asunto").asText();
        String explicacionCausas = o.get("explicacionCausas").asText();
        String explicacionExtension = o.get("explicacionExtension").asText();
        Long idNC = o.get("idNC").asLong();
        Boolean aplicaExpediente = o.get("aplicaExpediente").asBoolean();

        List<String> analisisCausasList = new ArrayList<>();
        if (o.get("analisisCausas").isArray()) {
            for (final JsonNode objNode : o.get("analisisCausas")) {
                analisisCausasList.add(objNode.asText());
            }
        }

        db.updateNoConformidad(em, origen, fecha, alcance, detectadaPor, descripcionDesviacion, numeroExpediente, 
                            acronimoExpediente, apartadoNorma, consultora, analisisCausasList, analisisExtension,
                            asunto, explicacionCausas, explicacionExtension, idNC, aplicaExpediente);

        /* NoConformidad nc = db.getNoConformidadById(em, idNC);
        log.info("#### nc.analisisExtension" + nc.getAnalisisExtension()); */
        
        
        return "{\"idNC\": "+idNC+"}";
    }

    @PostMapping(path = "/checkExistsNC", produces = "application/json")
    @ResponseBody
    @Transactional
    public Boolean checkExistsNC(@RequestBody JsonNode o){
        log.info("@@@@ inside checkExistsNC");
        
        Long id = o.get("ncId").asLong();

        Boolean exists = db.checkExistsNC(em, id);
 
        return exists;
    }

    @PostMapping(path = "/addAccion", produces = "application/json")
    @ResponseBody
    @Transactional
    public Accion addAccion(@RequestBody JsonNode o){
        log.info("@@@@ inside addAccion");
        
        String explicacion = o.get("explicacion").asText();
        String responsable = o.get("responsable").asText();
        String fechaInicialText = o.get("fechaInicial").asText();
        LocalDate fechaInicial = null;
        if(!fechaInicialText.equals("")){
            fechaInicial = LocalDate.parse(fechaInicialText);
        }
        String seguimiento = o.get("seguimiento").asText();        
        String estado = o.get("estado").asText();        
        Long idNC = o.get("idNC").asLong();
        LocalDate plazoImplementacion = LocalDate.parse(o.get("plazoImplementacion").asText());
        /* LocalDate ultimoSeguimiento = LocalDate.parse(o.get("ultimoSeguimiento").asText()); */
        AccionType tipo = AccionType.valueOf(o.get("tipo").asText().toUpperCase());
        String identificador = o.get("identificador").asText();

        String fechaCierreText = o.get("fechaCierre").asText();
        LocalDate fechaCierre = null;
        if(fechaCierre != null || !fechaCierreText.equals("") ){
            fechaCierre = LocalDate.parse(fechaCierreText);
        }

        String ultimoSeguimientoText = o.get("ultimoSeguimiento").asText();
        LocalDate ultimoSeguimiento = null;
        if(!ultimoSeguimientoText.equals("")){
            ultimoSeguimiento = LocalDate.parse(ultimoSeguimientoText);
        }

        Long idAc = db.addAccion(em, tipo, explicacion, responsable, fechaInicial, seguimiento, estado, idNC, plazoImplementacion, identificador, fechaCierre, ultimoSeguimiento);
        Accion acc = db.getAccionById(em, idAc);
 
        return acc;
    }

    @PostMapping(path = "/getEvidenciasAccion", produces = "application/json")
    @ResponseBody
    @Transactional
    public List<Evidencia> getEvidenciasAccion(@RequestBody JsonNode o){
        log.info("@@@@ inside getEvidenciasAccion");

        Long idAc = o.get("idAc").asLong();
        Accion acc = db.getAccionById(em, idAc);

        log.info("####");
        for(Evidencia e: acc.getEvidencias()){
            log.info("e.id: " + e.getId());
        }
 
        return acc.getEvidencias();
    }

    @PostMapping(path = "/updateAccion", produces = "application/json")
    @ResponseBody
    @Transactional
    public String updateAccion(@RequestBody JsonNode o){
        log.info("@@@@ inside updateAccion");
        
        String explicacion = o.get("explicacion").asText();
        String responsable = o.get("responsable").asText();
        String fechaInicialText = o.get("fechaInicial").asText();
        LocalDate fechaInicial = null;
        if(!fechaInicialText.equals("")){
            fechaInicial = LocalDate.parse(fechaInicialText);
        }
        String seguimiento = o.get("seguimiento").asText();        
        String estado = o.get("estado").asText();        
        Long idAc = o.get("idAc").asLong();
        LocalDate plazoImplementacion = LocalDate.parse(o.get("plazoImplementacion").asText());
        /* LocalDate ultimoSeguimiento = LocalDate.parse(o.get("ultimoSeguimiento").asText()); */
        AccionType tipo = AccionType.valueOf(o.get("tipo").asText().toUpperCase());
        String identificador = o.get("identificador").asText();

        String fechaCierreText = o.get("fechaCierre").asText();
        LocalDate fechaCierre = null;
        if(!fechaCierreText.equals("")){
            fechaCierre = LocalDate.parse(fechaCierreText);
        }

        String ultimoSeguimientoText = o.get("ultimoSeguimiento").asText();
        LocalDate ultimoSeguimiento = null;
        if(!ultimoSeguimientoText.equals("")){
            ultimoSeguimiento = LocalDate.parse(ultimoSeguimientoText);
        }

        log.info("#### plazoImplementacion: " + plazoImplementacion);

        db.updateAccion(em, tipo, explicacion, responsable, fechaInicial, seguimiento, estado, idAc, plazoImplementacion, identificador, fechaCierre, ultimoSeguimiento);
 
        return "{\"idAc\": "+idAc+"}";
    }

    @PostMapping(path = "/deleteAccion", produces = "application/json")
    @ResponseBody
    @Transactional
    public String deleteAccion(@RequestBody JsonNode o){
        log.info("@@@@ inside deleteAccion");
        
        Long accionId = o.get("accionId").asLong();


        db.deleteAccion(em, accionId);
 
        return "{\"isok\": \"isok\"}";
    }

    @PostMapping(path = "/updateDescripcionEvidencia", produces = "application/json")
    @ResponseBody
    @Transactional
    public String updateDescripcionEvidencia(@RequestBody JsonNode o){
        log.info("@@@@ inside updateDescripcionEvidencia");
        
        /* Long accionId = o.get("accionId").asLong(); */
        Long evidenciaId = o.get("evidenciaId").asLong();
        String descripcion = o.get("descripcion").asText();


        db.updateDescripcionEvidencia(em, evidenciaId, descripcion);
 
        return "{\"isok\": \"isok\"}";
    }

    @PostMapping(path = "/deleteEvidencia", produces = "application/json")
    @ResponseBody
    @Transactional
    public String deleteEvidencia(@RequestBody JsonNode o){
        log.info("@@@@ inside deleteEvidencia");
        
        Long evidenciaId = o.get("evidenciaId").asLong();
        Long archivoId = o.get("archivoId").asLong();

        CustomFile file = db.getCustomFileById(em, archivoId); 

        String filePath = "src/main/resources/static/media/evidencias/" + file.getId() + file.getExtension();

        deleteFile(filePath);

        db.deleteEvidenciaFile(em, archivoId, evidenciaId);  

        db.deleteEvidencia(em, evidenciaId);
 
        return "{\"isok\": \"isok\"}";
    }


    @PostMapping("/uploadDocumentoExtension")
    @Transactional
    @ResponseBody 
    public CustomFile uploadDocumentoExtension(@RequestParam("documentoExtension") MultipartFile documentoExtension, @RequestParam("idNC") long idNC,
                                            @RequestParam("name") String name, @RequestParam("extension") String extension)
    {
        log.info("---- inside uploadDocumentoExtension ----");

        long fileId = db.setDocumentoExtension(em, idNC, name, extension);

        File doc = new File("media-files/documentosExtension", fileId + extension);

        if (documentoExtension.isEmpty()) {
        log.info("failed to upload file: emtpy file?");
        return null;
        } else {
            BufferedOutputStream stream = null;
            try {
                stream = new BufferedOutputStream(new FileOutputStream(doc));
                byte[] bytes = documentoExtension.getBytes();
                stream.write(bytes);
                stream.flush(); // ensure all data is written out
                log.info("archivo copiado en le servidor");
            } catch (Exception e) {
                log.info(e.getMessage());
                return null;
            } finally {
                if (stream != null) {
                    try {
                        stream.close(); // ensure the stream is closed
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        CustomFile cf = db.getCustomFileById(em, fileId);

        return cf;
    }

    @PostMapping(path = "/deleteDocumentoExtension", produces = "application/json")
    @ResponseBody
    @Transactional
    public String deleteDocumentoExtension(@RequestBody JsonNode o){
        log.info("@@@@ inside deleteDocumentoExtension");
        
        Long documentoExtensionId = o.get("documentoExtensionId").asLong();
        Long ncId = o.get("ncId").asLong();

        CustomFile file = db.getCustomFileById(em, documentoExtensionId);  // Replace db.getFileById with the correct method to fetch a file by its id from your database.

        String filePath = "src/main/resources/static/media/documentosExtension/" + file.getId() + file.getExtension();

        deleteFile(filePath);

        db.deleteNoConformidadFile(em, documentoExtensionId, ncId);  // Replace db.deleteFile with the correct method to delete a file by its id from your database.
    
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

    @PostMapping("/addEvidencia")
    @Transactional
    @ResponseBody 
    public Evidencia addEvidencia(@RequestParam("evidencia") MultipartFile evidencia, @RequestParam("idAc") long idAc,
                                            @RequestParam("name") String name, @RequestParam("extension") String extension, @RequestParam("descripcion") String descripcion)
    {
        log.info("---- inside addEvidencia ----");

        long evidenciaId = db.createEvidencia(em, idAc, descripcion);

        long fileId = db.setEvidencia(em, evidenciaId, name, extension);

        File doc = new File("media-files/evidencias", fileId + extension);

        if (evidencia.isEmpty()) {
        log.info("failed to upload video: emtpy file?");
        return null;
        } else {
            try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(doc))) {
                byte[] bytes = evidencia.getBytes();
                stream.write(bytes);
                /* log.info("la ruta es: " + doc.getAbsolutePath()); */
            } catch (Exception e) {
                return null;
            }
        }

        Evidencia ev = db.getEvidenciaById(em, evidenciaId);
        return ev;
    }

    @PostMapping(path = "/searchNoConformidad", produces = "application/json")
    @ResponseBody
    @Transactional
    public List<NoConformidad> searchNoConformidad(@RequestBody JsonNode o){
        log.info("@@@@ inside searchNoConformidad");
        
        String asunto = o.get("asunto").asText();
        String estado = o.get("estado").asText();
        Long id = o.get("id").asLong();
        String origen = o.get("origen").asText();
        origen = origen.replace("'", "");

        String fechaText = o.get("fecha").asText();
        LocalDate fecha = null;
        if(!fechaText.equals("")){
            fecha = LocalDate.parse(fechaText);
        }

        List<NoConformidad> ncList = db.searchNoConformidad(em, asunto, fecha, estado, id, origen);

        for(NoConformidad nc: ncList){
            log.info("dentro del for: " + nc.getId());
        }
        
 
        return ncList;
    }

    @PostMapping(path = "/sortCurrentNoConformidadList", produces = "application/json")
    @ResponseBody
    @Transactional
    public List<NoConformidad> sortCurrentNoConformidadList(@RequestBody JsonNode o){
        log.info("@@@@ inside sortCurrentNoConformidadList");
        
        String asunto = o.get("asunto").asText();
        String estado = o.get("estado").asText();
        Long id = o.get("id").asLong();
        String sort = o.get("sort").asText();
        String reversedText = o.get("order").asText();
        Boolean reversed = null;
        if(!reversedText.equals("")) {
            reversed = Boolean.parseBoolean(reversedText);
        }
        String origen = o.get("origen").asText();
        origen = origen.replace("'", "");

        String fechaText = o.get("fecha").asText();
        LocalDate fecha = null;
        if(!fechaText.equals("")){
            fecha = LocalDate.parse(fechaText);
        }

        List<NoConformidad> ncList = db.searchNoConformidad(em, asunto, fecha, estado, id, origen);   

        if(sort.equals("byId")){
            if(reversed != null && reversed){
                Collections.sort(ncList, (nc1, nc2) -> Long.compare(nc2.getId(), nc1.getId())); // Descending
            } else if (reversed != null && !reversed){
                Collections.sort(ncList, (nc1, nc2) -> Long.compare(nc1.getId(), nc2.getId())); // Ascending
            }
    
        } else if (sort.equals("byFecha")){
            if(reversed != null && reversed){
                // descending
                Collections.sort(ncList, (nc1, nc2) -> nc2.getFecha().compareTo(nc1.getFecha()));
            } else if (reversed != null && !reversed){
                // ascending
                Collections.sort(ncList, (nc1, nc2) -> nc1.getFecha().compareTo(nc2.getFecha()));
            }
        }
 
        return ncList;
    }
}
