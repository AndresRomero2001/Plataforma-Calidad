const eliminarModal = new bootstrap.Modal(document.querySelector('#eliminarModal'));
const eliminarEvidenciaModal = new bootstrap.Modal(document.querySelector('#eliminarEvidenciaModal'));
const eliminarDocumentoExtensionModal = new bootstrap.Modal(document.querySelector('#eliminarDocumentoExtensionModal'));
const modifiedToast = new bootstrap.Toast(document.querySelector('#modifiedToast'));
const accionAddedToast = new bootstrap.Toast(document.querySelector('#accionAddedToast'));

//ver si hay inputs con valor=="otro" y precargar la informacion correspondiente
window.onload = function() {
    
    const causes = $('#analisisCausasSelect').filterMultiSelect();

    let selectedOption = document.getElementById("origenSelect")
    if(selectedOption.value === "otro"){
        document.getElementById("origenSelectOtroDiv").style.display = "block"
        document.getElementById("origenSelectOtro").value = document.getElementById("origenText").value
    }

    let selectedOptionAlcance = document.getElementById("alcanceSelect");
    if (selectedOptionAlcance.value === "otro") {
        document.getElementById("alcanceSelectOtroDiv").style.display = "block";
        document.getElementById("alcanceSelectOtro").value = document.getElementById("alcanceText").value
    }

    let selectedOptionDetectada = document.getElementById("detectadaPorSelect");
    if (selectedOptionDetectada.value === "otro") {
        document.getElementById("detectadaPorSelectOtroDiv").style.display = "block";
        document.getElementById("detectadaPorSelectOtro").value = document.getElementById("detectadaPorText").value;
    }

    let selectedOptionApartadoNorma = document.getElementById("apartadoNormaSelect");
    if (selectedOptionApartadoNorma.value === "otro") {
        document.getElementById("apartadoNormaSelectOtroDiv").style.display = "block";
        document.getElementById("apartadoNormaSelectOtro").value = document.getElementById("apartadoNormaText").value;
    }

    /* let selectedOptionAnalisisCausas = document.getElementById("analisisCausasSelect");
    if (selectedOptionAnalisisCausas.value === "otro") {
        document.getElementById("analisisCausasSelectOtroDiv").style.display = "block";
        document.getElementById("analisisCausasSelectOtro").value = document.getElementById("analisisCausasText").value
    } */
    

    let accionDivList = document.querySelectorAll(".accionDiv");
    accionDivList.forEach((div) => {
        selectedOptionResponsable = div.querySelector("#responsableSelect")
        if (selectedOptionResponsable.value === "otro") {
            div.querySelector("#responsableSelectOtroDiv").style.display = "block";
            div.querySelector("#responsableSelectOtro").value = div.querySelector("#responsableText").value;
        }

        selectedOptionEstado = div.querySelector("#estadoSelect")
        if (selectedOptionEstado.value === "Cerrada") {
            div.querySelector("#estadoSelectCerradaDiv").style.display = "block";
            div.querySelector("#fechaCierre").value = div.querySelector("#fechaCierreText").value;
        }
    });

    // si no, las fechas se mostrarian YYYY-MM-DD
    let accordionHeaderList = document.querySelectorAll(".accordion-header");
    accordionHeaderList.forEach((div) => {
        plazoImplementacion = div.querySelector("#showPlazoImplementacion").innerHTML;
        const formattedDate = reverseDate(plazoImplementacion);
        div.querySelector("#showPlazoImplementacion").innerHTML = formattedDate;
    });
}

document.addEventListener("DOMContentLoaded", () => {
    const targetNode = document.getElementById('analisisCausasSelect').parentNode; // observe the parent

    const observerOptions = {
        childList: true, // Report added/removed nodes
        subtree: true, // Also observe child nodes
    };

    const observer = new MutationObserver(() => {
        if (otraCausaIsSelected()) {
            document.getElementById("analisisCausasSelectOtroDiv").style.display = "block";
            document.getElementById("analisisCausasSelectOtro").value = document.getElementById("analisisCausasText").value;
        }
    });

    observer.observe(targetNode, observerOptions);

    setTimeout(()=>{
        console.log("desconcectando observer")
        observer.disconnect()
    }, 1000)

    if(getAccionesCount() <= 0){
        let html = `
            <div id="noAccionesMsg">
                <h3>No hay acciones asignadas</h3>
            </div>
        `
        document.getElementById("accionesTitle").insertAdjacentHTML("afterend", html)
        document.getElementById("fakeAccordionItem").style.display = "none"
    }
});


// para poder crear varias acciones a la vez y que no se solapen los IDs, que se necesitan para poder seleccionarlos 
// cuando se creen y poder precargar la fecha inicial
var countNewAcciones = 0;
function addAccionForm(){
    let accordion = document.getElementById("accionesAccordion")

    let html=`
        <div class="accordion-item">
            <h2 class="accordion-header" >
                <button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#newAccion`+countNewAcciones+`" aria-expanded="true" aria-controls="newAccion`+countNewAcciones+`">
                    <span>Nueva acción</span>
                </button>
            </h2>
            <div id="newAccion`+countNewAcciones+`" class="accordion-collapse collapse show">
                <div class="row accordion-body">
                    <div class="accionDiv">
                        <form role="form" id="addAccionForm" onsubmit="event.preventDefault(); addAccion(event);">
                            <div class="row formRow">
                                <div class="col col-md-6">
                                    <label for="identificador" class="inputLabel">Identificador*</label>
                                    <input type="text" class="form-control" id="identificador" maxlength="12" onchange="checkCreateAccion(event)" onkeyup="checkCreateAccion(event)" required>
                                </div>
                            </div>
                            <div class="row formRow">
                                <div class="col">
                                    <label for="tipoSelect" class="inputLabel">Tipo de acción*</label>
                                    <select class="form-select" id="tipoSelect" onchange="checkCreateAccion(event)" required>
                                        <option selected value="">Seleccionar...</option>
                                        <option value="correctiva">Correctiva</option>
                                        <option value="reparadora">Reparadora</option>
                                        <option value="contencion">Contención</option>
                                    </select>
                                </div>
                                <div class="col">
                                    <label for="responsableSelect" class="inputLabel">Responsable*</label>
                                    <select class="form-select" id="responsableSelect" onchange="manageAccionSelect(event); checkCreateAccion(event)" required>
                                        <option selected value="">Seleccionar...</option>
                                        <option value="direccionGeneral">Dirección general</option>
                                        <option value="directorTecnico">Director técnico</option>
                                        <option value="directorCalidad">Director de calidad</option>
                                        <option value="otro">Otro</option>
                                    </select>
                                    <div class="row otroDiv" id="responsableSelectOtroDiv">
                                        <div class="col col-md-6">
                                            <label for="responsableSelectOtro" class="inputLabel">Otro responsable</label>
                                            <input class="form-control" id="responsableSelectOtro"></input>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="row formRow">
                                <div class="col">
                                    <label for="fechaInicial" class="inputLabel">Fecha inicial*</label>
                                    <input type="date" class="form-control" id="fechaInicial" onchange="checkCreateAccion(event)" required>
                                </div>
                                <div class="col">
                                    <label for="plazoImplementacion" class="inputLabel">Plazo de implementación*</label>
                                    <input type="date" class="form-control" id="plazoImplementacion" onchange="checkCreateAccion(event)" required></input>
                                </div>
                            </div>

                            <div class="row formRow">
                                <div class="col col-md-6"></div>
                                <div class="col ">
                                    <label for="ultimoSeguimiento" class="inputLabel">Último seguimiento</label>
                                    <input type="date" class="form-control" id="ultimoSeguimiento">
                                </div>
                            </div>
            
                            <div class="row formRow">
                                <div class="col col-md-6">
                                    <label for="explicacionAccion" class="inputLabel">Explicación de la acción*</label>
                                    <textarea class="form-control" id="explicacionAccion" onchange="checkCreateAccion(event)" onkeyup="checkCreateAccion(event)" required></textarea>
                                </div>
                                <div class="col">
                                    <label for="seguimientoAccion" class="inputLabel">Seguimiento de la acción</label>
                                    <textarea class="form-control" id="seguimientoAccion"></textarea>
                                </div>
                            </div>
            
                            <div class="row evidenciaDiv formRow">
                                <div class="col">
                                    <label for="descripcionEvidencia" class="inputLabel">Descripción de la evidencia</label>
                                    <input type="text" class="form-control" id="descripcionEvidencia">
                                </div>
                                <div class="col">
                                    <label for="evidencia" class="inputLabel">Evidencia</label>
                                    <input type="file" class="form-control" id="evidencia">
                                </div>
                            </div>
                            
                            <div class="row formRow">
                                <div class="col">
                                    <button type="button" class="btn btn-primary" id="addEvidenciaButton" onclick="addEvidenciaWithoutCheckCreateHTML(event)">Nueva evidencia</button>
                                </div>
                            </div>
            
                            <div class="row formRow">
                                <div class="col col-md-6">
                                    <label for="estadoSelect" class="inputLabel">Estado</label>
                                    <select class="form-select" id="estadoSelect" onchange="manageSelectCerrada(event)">
                                        <option selected value="">Seleccionar...</option>
                                        <option value="Abierta">Abierta</option>
                                        <option value="Cerrada">Cerrada</option>
                                        <option value="Pendiente">Pendiente</option>
                                    </select>
                                    
                                </div>
                                <div class="col otroDiv" id="estadoSelectCerradaDiv">
                                    <label for="fechaCierre">Fecha cierre</label>
                                    <input type="date" class="form-control" id="fechaCierre">
                                </div>
                            </div>
                            <div class="row d-flex justify-content-center formRow">
                                <button type="submit" class="btn btn-primary addAccionButton" id="addAccionButton" disabled>Añadir acción</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    `

    countNewAcciones++;
    accordion.insertAdjacentHTML("beforeend", html)

    // to preload always the fechaInicial of the last added accion
    let accionDivs = document.querySelectorAll(".accionDiv");
    let lastAccionDiv = accionDivs[accionDivs.length - 1];
    let fechaInicial = lastAccionDiv.querySelector("#fechaInicial")
    preloadCurrentDate(fechaInicial)
}

function checkCreateAccion(e){
    let accion = e.target.closest(".accordion-item")

    let tipo = accion.querySelector("#tipoSelect").value
    let fechaInicial = accion.querySelector("#fechaInicial").value
    let responsable = accion.querySelector("#responsableSelect").value
    let plazoImplementacion = accion.querySelector("#plazoImplementacion").value
    let explicacionAccion = accion.querySelector("#explicacionAccion").value
    let identificador = accion.querySelector("#identificador").value

    let activateCreateButton = true;

    if(tipo == "") activateCreateButton = false;
    if(fechaInicial == "" || fechaInicial == null) activateCreateButton = false;
    if(responsable == "") activateCreateButton = false;
    if(plazoImplementacion == "") activateCreateButton = false;
    if(explicacionAccion == "") activateCreateButton = false;
    if(identificador == "" || identificador == null) activateCreateButton = false;

    console.log(activateCreateButton);
    if(activateCreateButton) accion.querySelector("#addAccionButton").disabled = false
    else accion.querySelector("#addAccionButton").disabled = true
}

function addEvidenciaHTML(e){
    
    let html = `
        <div class="row evidenciaDiv formRow">
            <div class="col">
                <label for="descripcionEvidencia" class="inputLabel">Descripción de la evidencia</label>
                <input type="text" class="form-control" id="descripcionEvidencia" onchange="checkCreateEvidencia(event)">
            </div>
            <div class="col">
                <label for="evidencia" class="inputLabel">Archivo de la evidencia</label>
                <input type="file" class="form-control" id="evidencia" onchange="checkCreateEvidencia(event)">
            </div>
        </div>
    `
    e.target.insertAdjacentHTML("beforebegin", html)
}

function addEvidenciaWithoutCheckCreateHTML(e){
    
    let html = `
        <div class="row evidenciaDiv formRow">
            <div class="col">
                <label for="descripcionEvidencia" class="inputLabel">Descripción de la evidencia</label>
                <input type="text" class="form-control" id="descripcionEvidencia">
            </div>
            <div class="col">
                <label for="evidencia" class="inputLabel">Archivo de la evidencia</label>
                <input type="file" class="form-control" id="evidencia">
            </div>
        </div>
    `
    e.target.insertAdjacentHTML("beforebegin", html)
}

function checkCreateEvidencia(e){
    let accion = e.target.closest(".accordion-item")
    let idAc = accion.id
    let evidenciaDiv = e.target.closest(".evidenciaDiv")
    let descripcion = evidenciaDiv.querySelector("#descripcionEvidencia").value
    let archivoInput = evidenciaDiv.querySelector("#evidencia")
    if(descripcion != "" && archivoInput.files[0]){
        addEvidencia(evidenciaDiv, idAc).then(ev => {

            console.log("/media/evidencias/" + ev.archivo.id + ev.archivo.extension );

            let html = `
                <div class="row formRow evidenciaDiv" id="${ev.id}">
                    <div class="col">
                        <label for="descripcionEvidencia" class="inputLabel">Descripción de la evidencia</label>
                        <input type="text" class="form-control" id="descripcionEvidencia" value="${ev.descripcion}" onchange="updateDescripcionEvidencia(event)">
                    </div>
                    <div class="col ">
                        <label for="evidencia" class="inputLabel">Archivo de la evidencia</label>
                        <input type="text" class="form-control evidenciaArchivo" id="evidencia" value="${ev.archivo.name}" disabled>
                        <a class="iconLinkButton" href="/media/evidencias/${ev.archivo.id}${ev.archivo.extension}?downloadName=${ev.archivo.name}">
                            <button type="button" class="btn btn-primary downloadEvidenciaButton">
                                <i class="bi bi-file-earmark-arrow-down"></i>
                            </button>
                        </a>
                        <input type="hidden" id="archivoId" value="${ev.archivo.id}">
                        <button type="button" class="btn btn-danger" id="deleteEvidenciaButton" data-bs-toggle="modal" data-bs-target="#eliminarEvidenciaModal"  onclick="updateEvidenciaToDelete(event)">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </div>
            `

            evidenciaDiv.insertAdjacentHTML("beforebegin", html)
            evidenciaDiv.remove()
            modifiedToast.show()
        })
    }
}

function addEvidencia(div, idAc){
    console.log("--- inside addEvidencia ---");
    let evidencia = div.querySelector("#evidencia");
    console.log(evidencia);
    let descripcion = div.querySelector("#descripcionEvidencia").value

    console.log("evidencia desc: " + descripcion);

    let formData = new FormData();
    formData.append("idAc", idAc)
    formData.append("descripcion", descripcion)
    formData.append("evidencia", evidencia.files[0])

    let fileName = evidencia.files[0].name;
    let fileExtension = fileName.split('.').pop();
    fileExtension = "." + fileExtension

    formData.append("name", fileName)
    formData.append("extension", fileExtension)

    console.log("evidencia name: " + fileName);

    // hay que devolver una promesa para el array de promesas de addAccion
    return go("/addEvidencia", "POST", formData, {}).then(ev => {
        console.log("dentro de then");
        return ev
    }).catch((e) =>{ 
        console.log("Error en catch addEvidencia");
    });
}

function responsableWellWritten(responsable){
    switch (responsable) {
        case "directorTecnico":
            return "Director técnico";
        case "direccionGeneral":
            return "Dirección general"
        case "directorCalidad":
            return "Director de calidad"
        default:
            return responsable
    }
}

function addAccion(e){
    let div = e.target.closest(".accordion-item")
    let idNC = document.getElementById("idNC").value
    let responsable = div.querySelector("#responsableSelect").value
    let fechaInicial = div.querySelector("#fechaInicial").value
    let explicacion = div.querySelector("#explicacionAccion").value
    let seguimiento = div.querySelector("#seguimientoAccion").value
    let estado = div.querySelector("#estadoSelect").value
    let tipo = div.querySelector("#tipoSelect").value
    let plazoImplementacion = div.querySelector("#plazoImplementacion").value
    let ultimoSeguimiento = div.querySelector("#ultimoSeguimiento").value
    let identificador = div.querySelector("#identificador").value
    

    let params = {
        "explicacion": explicacion,
        "idNC": idNC,
        "responsable": responsable,
        "fechaInicial": fechaInicial,
        "seguimiento": seguimiento,
        "estado": estado,
        "tipo": tipo,
        "plazoImplementacion": plazoImplementacion,
        "identificador": identificador,
        "fechaCierre": "",
        "ultimoSeguimiento": ultimoSeguimiento
    }

    if(estado === "Cerrada") {
        params.fechaCierre = div.querySelector("#fechaCierre").value
    }

    if(responsable === "otro"){
        params.responsable = div.querySelector("#responsableSelectOtro").value
    }

    go(config.rootUrl + "/addAccion", 'POST', params)
    .then(acc => {console.log("todo ok")
        let idAc = acc.id
        let promises = [] // se guardan todas las promesas de añadir evidencia, para poder esperar a q se ejecuten todas antes de ejecutar el codigo
        // de mostrar evidencias

        for (let evidenciaDiv of div.querySelectorAll(".evidenciaDiv")) {
            let descripcion = evidenciaDiv.querySelector("#descripcionEvidencia")
            let archivo = evidenciaDiv.querySelector("#evidencia")
            let hasArchivo = false
            if(archivo.files[0]) hasArchivo = true
            if(hasArchivo && descripcion.value != ""){
                promises.push(addEvidencia(evidenciaDiv, idAc))
            }
            
        }
        
        Promise.all(promises).then(()=> {
            
            div.remove()
            let noAccionesMsg = document.getElementById("noAccionesMsg");
            let fakeAccordionItem = document.getElementById("fakeAccordionItem");

            if(noAccionesMsg) {
                noAccionesMsg.style.display = "none";
            }

            if(fakeAccordionItem) {
                fakeAccordionItem.style.display = "block";
            }

            let paramsGetEvidencias = {
                "idAc": idAc
            }

            go(config.rootUrl + "/getEvidenciasAccion", 'POST', paramsGetEvidencias)
            .then(evidencias => {
                /* console.log(evidencias); */

                let responsable = responsableWellWritten(acc.responsable)
                let estado = acc.estado
                /* if(estado === "pendienteVerificacion") estado = "Pendiente" */
    
                const plazoImplementacion = reverseDate(acc.plazoImplementacion);
    
                let accordion = document.getElementById("accionesAccordion")

                let evidenciasHTML = ``

                evidencias.forEach(e => {
                    console.log(e.id);
                    console.log("href=/media/evidencias/" + e.archivo.id + e.archivo.extension);
                    evidenciasHTML += `
                        <div class="row formRow evidenciaDiv" id="${e.id}">
                            <div class="col">
                                <label for="descripcionEvidencia" class="inputLabel">Descripción de la evidencia</label>
                                <input type="text" class="form-control" id="descripcionEvidencia" value="${e.descripcion}" onchange="updateDescripcionEvidencia(event)">
                            </div>
                            <div class="col ">
                                <label for="evidencia" class="inputLabel">Archivo de la evidencia</label>
                                <input type="text" class="form-control evidenciaArchivo" id="evidencia" value="${e.archivo.name}" disabled>
                                <a class="iconLinkButton " href="/media/evidencias/${e.archivo.id}${e.archivo.extension}?downloadName=${e.archivo.name}">
                                    <button type="button" class="btn btn-primary downloadEvidenciaButton">
                                        <i class="bi bi-file-earmark-arrow-down"></i>
                                    </button>
                                </a>
                                <input type="hidden" id="archivoId" value="${e.archivo.id}">
                                <button type="button" class="btn btn-danger" id="deleteEvidenciaButton" data-bs-toggle="modal" data-bs-target="#eliminarEvidenciaModal"  onclick="updateEvidenciaToDelete(event)">
                                    <i class="bi bi-trash"></i>
                                </button>
                            </div>
                        </div>
                    `
                });

                let html=`
                    <div class="accordion-item" id="`+idAc+`">
                        <div class="row accordion-header" >
                            <button class="accordion-button customAccordionButton" type="button" data-bs-toggle="collapse" data-bs-target="#accion`+idAc+`" aria-controls="accion`+idAc+`">
                                <div class="row insideButtonRow">
                                    <div class="col col-md-2">
                                        <p class="accordionHeaderText">`+acc.identificador+`</p>
                                    </div>
                                    <div class="col col-md-2">
                                        <p class="accordionHeaderText">`+acc.tipo+`</p>
                                    </div>
                                    <div class="col col-md-3">
                                        <p class="accordionHeaderText">`+responsable+`</p>
                                    </div>
                                    <div class="col col-md-2">
                                        <p class="accordionHeaderText">`+estado+`</p>
                                    </div>
                                    <div class="col col-md-3">
                                        <p class="accordionHeaderText">`+plazoImplementacion+`</p>
                                    </div>
                                </div>
                            </button>
                        </div>
                        <div id="accion`+idAc+`" class="accordion-collapse collapse show">
                            <div class="row accordion-body">
                                <div class="accionDiv">
                
                                    <div class="row formRow">
                                        <div class="col col-md-6">
                                            <label for="identificador" class="inputLabel">Identificador*</label>
                                            <input type="text" class="form-control" id="identificador" value="`+acc.identificador+`" maxlength="12" onchange="updateAccion(event)" onkeyup="updateAccion(event)">
                                        </div>
                                    </div>
                                    <div class="row formRow">
                                        <div class="col">
                                            <label for="tipoSelect" class="inputLabel">Tipo de acción*</label>
                                            <select class="form-select" id="tipoSelect" onchange="updateAccion(event)">
                                                <option value="correctiva" ${acc.tipo === "CORRECTIVA" ? 'selected' : ''}>Correctiva</option>
                                                <option value="reparadora" ${acc.tipo === "REPARADORA" ? 'selected' : ''}>Reparadora</option>
                                                <option value="contencion" ${acc.tipo === "CONTENCION" ? 'selected' : ''}>Contención</option>
                                            </select>
                                        </div>
                                        <div class="col">
                                            <label for="responsableSelect" class="inputLabel">Responsable*</label>
                                            <select class="form-select" id="responsableSelect" onchange="manageAccionSelect(event); updateAccion(event)">
                                                <option value="direccionGeneral" ${acc.responsable === "direccionGeneral" ? 'selected' : ''}>Dirección general</option>
                                                <option value="directorTecnico" ${acc.responsable === "directorTecnico" ? 'selected' : ''}>Director técnico</option>
                                                <option value="directorCalidad" ${acc.responsable === "directorCalidad" ? 'selected' : ''}>Director de calidad</option>
                                                <option value="otro" ${acc.responsable !== "directorCalidad"  && acc.responsable !== "direccionGeneral" && acc.responsable !== "directorTecnico"? 'selected' : ''}>Otro</option>
                                            </select>
                                            <div class="row otroDiv" id="responsableSelectOtroDiv">
                                                <div class="col col-md-6">
                                                    <label for="responsableSelectOtro" class="inputLabel">Otro responsable</label>
                                                    <input class="form-control" value="` + acc.responsable + `" id="responsableSelectOtro"></input>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="row formRow">
                                        <div class="col">
                                            <label for="fechaInicial" class="inputLabel">Fecha inicial*</label>
                                            <input type="date" class="form-control" id="fechaInicial" value="`+acc.fechaInicial+`" onchange="updateAccion(event)">
                                        </div>
                                        <div class="col">
                                            <label for="plazoImplementacion" class="inputLabel">Plazo de implementación*</label>
                                            <input type="date" class="form-control" id="plazoImplementacion" value="`+acc.plazoImplementacion+`" onchange="updateAccion(event)"></input>
                                        </div>
                                    </div>

                                    <div class="row formRow">
                                        <div class="col col-md-6"></div>
                                        <div class="col ">
                                            <label for="ultimoSeguimiento" class="inputLabel">Último seguimiento</label>
                                            <input type="date" class="form-control" id="ultimoSeguimiento" value="${acc.ultimoSeguimiento}" onchange="updateAccion(event)">
                                        </div>
                                    </div>
                    
                                    <div class="row formRow">
                                        <div class="col col-md-6">
                                            <label for="explicacionAccion" class="inputLabel">Explicación de la acción*</label>
                                            <textarea class="form-control" id="explicacionAccion" onchange="updateAccion(event)" onkeyup="updateAccion(event)">`+acc.explicacion+`</textarea>
                                        </div>
                                        <div class="col">
                                            <label for="seguimientoAccion" class="inputLabel">Seguimiento de la acción</label>
                                            <textarea class="form-control" id="seguimientoAccion" onchange="updateAccion(event)">`+acc.seguimiento+`</textarea>
                                        </div>
                                    </div>
                    
                                    `
                                        +evidenciasHTML+
                                    `
                                    
                                    <div class="row formRow">
                                        <div class="col">
                                            <button type="button" class="btn btn-primary" id="addEvidenciaButton" onclick="addEvidenciaHTML(event)">Nueva evidencia</button>
                                        </div>
                                    </div>
                    
                                    <div class="row formRow">
                                        <div class="col col-md-6">
                                            <label for="estadoSelect" class="inputLabel">Estado</label>
                                            <select class="form-select" id="estadoSelect" onchange="manageSelectCerrada(event); updateAccion(event)">
                                                <option value="Abierta" ${acc.estado === "" ? 'selected' : ''}>Seleccionar...</option>
                                                <option value="Abierta" ${acc.estado === "Abierta" ? 'selected' : ''}>Abierta</option>
                                                <option value="Cerrada" ${acc.estado === "Cerrada" ? 'selected' : ''}>Cerrada</option>
                                                <option value="Pendiente" ${acc.estado === "Pendiente" ? 'selected' : ''}>Pendiente</option>
                                            </select>
                                            
                                        </div>
                                        <div class="col otroDiv" id="estadoSelectCerradaDiv">
                                            <label for="fechaCierre">Fecha cierre</label>
                                            <input type="date" class="form-control" value="`+acc.fechaCierre+`" id="fechaCierre" onchange="updateAccion(event)">
                                        </div>
                                    </div>
                                    <div class="row d-flex justify-content-center formRow">
                                        <button type="button" class="btn btn-danger deleteAccionButton" id="deleteAccionButton" data-bs-toggle="modal" data-bs-target="#eliminarModal" onclick="updateAccionToDelete(event)">
                                            Eliminar acción
                                        </button>
                                    </div>
                                    
                                </div>
                            </div>
                        </div>
                    </div>
                `

                console.log("despues del html");

                accordion.insertAdjacentHTML("beforeend", html)

                let accionAdded = document.getElementById(idAc);
                selectedOptionResponsable = accionAdded.querySelector("#responsableSelect")
                if (selectedOptionResponsable.value === "otro") {
                    accionAdded.querySelector("#responsableSelectOtroDiv").style.display = "block";
                }

                selectedOptionEstado = accionAdded.querySelector("#estadoSelect")
                if (selectedOptionEstado.value === "Cerrada") {
                    accionAdded.querySelector("#estadoSelectCerradaDiv").style.display = "block";
                }

                accionAddedToast.show()

            }).catch((e) =>{ 
                console.log("Error en catch getEvidenciasAccion");
            });

            
        })

    })
    .catch(() => {console.log("Error en catch addAccion");

    })
}

var accionDivSelected

function updateAccionToDelete(e){
    accionDivSelected = e.target.closest(".accordion-item")
    console.log("nuevo id accion a borrar: " + accionDivSelected.id);
}

function getAccionesCount(){
    // -1 porque el la fila de titulos tmb es un accordion item
    var count = document.querySelectorAll(".accordion-item").length -1;
    console.log("count: " + count);
    return count;
}

function deleteAccion(){
    console.log("accion to be deleted: " + accionDivSelected.id);

    let params = {
        "accionId": accionDivSelected.id
    }

    go(config.rootUrl + "/deleteAccion", 'POST', params)
    .then(d => {console.log("todo ok")
        accionDivSelected.remove();
        if(getAccionesCount() <= 0){
            let html = `
                <div id="noAccionesMsg">
                    <h3>No hay acciones asignadas</h3>
                </div>
            `
            document.getElementById("accionesTitle").insertAdjacentHTML("afterend", html)
            document.getElementById("fakeAccordionItem").style.display = "none"
        }
        eliminarModal.hide();
        modifiedToast.show()
    })
    .catch(() => {console.log("Error en catch deleteAccion");

    })
}

var evidenciaDivToDelete

function updateEvidenciaToDelete(e){
    evidenciaDivToDelete = e.target.closest(".evidenciaDiv")
}

function deleteEvidencia(){
    let archivoId = evidenciaDivToDelete.querySelector("#archivoId").value //si se clicka en el <i> el target es <i> y el current target es el <button>
    let accionId = evidenciaDivToDelete.closest(".accordion-item").id
    let evidenciaDiv = evidenciaDivToDelete
    let evidenciaId = evidenciaDiv.id
    console.log("evidencia to be deleted: " + evidenciaId);
    console.log("file to be deleted: " + archivoId);
    console.log("evidencia belongs to accion: " + accionId);

    let params = {
        "evidenciaId": evidenciaId,
        "archivoId": archivoId,
        "accionId": accionId
    }

    go(config.rootUrl + "/deleteEvidencia", 'POST', params)
    .then(d => {console.log("todo ok")
        evidenciaDiv.remove();
        eliminarEvidenciaModal.hide();
        modifiedToast.show()
    })
    .catch(() => {console.log("Error en catch deleteEvidencia");

    })
}

function checkUpdateNC(e){
    var selectedValue = e.target.value;
    if(selectedValue !== 'otro'){
        updateNC();
    }
}

function updateNC(){
    let origen = document.getElementById("origenSelect").value
    let fecha = document.getElementById("fecha").value
    let alcance = document.getElementById("alcanceSelect").value
    let detectadaPor = document.getElementById("detectadaPorSelect").value
    let descripcionDesviacion = document.getElementById("descripcionDesviacion").value
    let numeroExpediente = document.getElementById("numeroExpediente").value
    let acronimoExpediente = document.getElementById("acronimoExpediente").value
    let apartadoNorma = document.getElementById("apartadoNormaSelect").value
    let consultora = document.getElementById("consultora").value
    /* let analisisCausas = document.getElementById("analisisCausasSelect").value */
    let explicacionCausas = document.getElementById("explicacionCausas").value
    let analisisExtension = document.getElementById("analisisExtensionSelect").value
    let explicacionExtension = document.getElementById("explicacionExtension").value
    let asunto = document.getElementById("asunto").value
    let idNC = document.getElementById("idNC").value
    let aplicaExpediente = document.getElementById("aplicaExpedienteSelect").value

    let checkedBoxes = document.querySelectorAll('input[type="checkbox"][name="causa"]:checked');
    let checkedValues = Array.from(checkedBoxes).map(cb => cb.value);

    let params = {"origen": origen,
                    "fecha": fecha,
                    "alcance": alcance,
                    "detectadaPor": detectadaPor,
                    "descripcionDesviacion": descripcionDesviacion,
                    "numeroExpediente": numeroExpediente,
                    "acronimoExpediente": acronimoExpediente,
                    "apartadoNorma": apartadoNorma,
                    "consultora": consultora,
                    "analisisCausas": checkedValues,
                    "analisisExtension": analisisExtension,
                    "explicacionCausas": explicacionCausas,
                    "explicacionExtension": explicacionExtension,
                    "asunto": asunto,
                    "idNC": idNC,
                    "aplicaExpediente": aplicaExpediente
    }; 

    if(origen === "otro") {
        params.origen = document.getElementById("origenSelectOtro").value
    }
    
    if(alcance === "otro") {
        params.alcance = document.getElementById("alcanceSelectOtro").value
    }
    
    if(detectadaPor === "otro") {
        params.detectadaPor = document.getElementById("detectadaPorSelectOtro").value
    }

    if(apartadoNorma === "otro") {
        params.apartadoNorma = document.getElementById("apartadoNormaSelectOtro").value
    }

    /* if(analisisCausas === "otro") {
        params.analisisCausas = document.getElementById("analisisCausasSelectOtro").value
    } */
    if (checkedValues.includes("otro")) {
        checkedValues = checkedValues.filter(val => val !== "otro");
        checkedValues.push(document.getElementById("analisisCausasSelectOtro").value)
        params.analisisCausas = checkedValues
    }

    go(config.rootUrl + "/updateNoConformidad", 'POST', params)
    .then(d => {console.log("todo ok")
        modifiedToast.show();
    })
    .catch(() => {console.log("Error en catch updateNoConformidad");

    })
}

function uploadDocumentoExtension(){
    console.log("--- inside uploadDocumentoExtension ---");
    let idNC = document.getElementById("idNC").value;
    let documentoExtension = document.getElementById("documentoExtension");
    /* console.log("video : " + video.files[0]); */

    let formData = new FormData();
    formData.append("idNC", idNC)
    formData.append("documentoExtension", documentoExtension.files[0])

    let fileName = documentoExtension.files[0].name;
    let fileExtension = fileName.split('.').pop();
    fileExtension = "." + fileExtension

    formData.append("name", fileName)
    formData.append("extension", fileExtension)

    console.log(" fileName: " + fileName + " fileExtension " + fileExtension);

    // cf: CustomFile
    go("/uploadDocumentoExtension", "POST", formData, {}).then(cf => {
        let fileId = cf.id
        console.log("fileId: " + fileId);

        let uploadCol = document.getElementById("uploadDocumentoExtensionCol")
        uploadCol.remove()

        /* console.log("fileId: " + fileId + " fileName: " + fileName + " fileExtension " + fileExtension); */
        console.log("/media/documentosExtension/" + fileId + fileExtension);
        console.log("/media/documentosExtension/" + fileId + cf.extension);

        let html=`
            <div class="col" id="downloadDocumentoExtensionCol">
                <div class="row">
                    <label for="documentoExtensionCustom" class="inputLabel">Documento del análisis de extensión</label>
                </div>
                <div class="row">
                    <div class="col ">
                        <p id="documentoExtensionCustom">`+fileName+`</p>
                        <div class="documentoExtensionButtons">
                            <a class="iconLinkButton" href="/media/documentosExtension/`+ fileId + cf.extension +`?downloadName=` + cf.name + `">
                                <button type="button" class="btn btn-primary downloadButton">
                                    <i class="bi bi-file-earmark-arrow-down"></i>
                                    Descargar
                                </button>
                            </a>
                            <button type="button" class="btn btn-danger" id="deleteDocumentoExtensionButton" value="${fileId}" data-bs-toggle="modal" data-bs-target="#eliminarDocumentoExtensionModal">
                                <i class="bi bi-trash"></i>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `

        let documentoExtensionRow = document.getElementById("documentoExtensionRow")
        documentoExtensionRow.insertAdjacentHTML("beforeend", html)
        modifiedToast.show()
    }).catch((e) =>{ 
        console.log("Error en uploadDocumentoExtension");
    });
}

function deleteDocumentoExtension(){
    let documentoExtensionId = document.getElementById("deleteDocumentoExtensionButton").value
    let ncId = document.getElementById("idNC").value

    console.log("docExtension a borrar: " + documentoExtensionId);

    let params = {
        "documentoExtensionId": documentoExtensionId,
        "ncId": ncId
    }

    go(config.rootUrl + "/deleteDocumentoExtension", 'POST', params)
    .then(d => {console.log("todo ok")
        let downloadCol = document.getElementById("downloadDocumentoExtensionCol")
        downloadCol.remove()

        let html=`
            <div class="col col-md-6" id="uploadDocumentoExtensionCol">
                <label for="documentoExtension" class="inputLabel">Documento del análisis de extensión</label>
                <input type="file" class="form-control" id="documentoExtension" onchange="uploadDocumentoExtension()">
            </div>
        `

        let documentoExtensionRow = document.getElementById("documentoExtensionRow")
        documentoExtensionRow.insertAdjacentHTML("beforeend", html)

        eliminarDocumentoExtensionModal.hide()
        modifiedToast.show()
    })
    .catch(() => {console.log("Error en catch deleteDocumentoExtension");

    })
}

function checkUpdateAccion(e){
    var selectedValue = e.target.value;
    if(selectedValue !== 'otro'){
        updateAccion(e);
    }
}

function updateAccion(e){
    let accion = e.target.closest(".accordion-item")
    let idAc = accion.id
    console.log(idAc);

    let div = accion
    let responsable = div.querySelector("#responsableSelect").value
    let fechaInicial = div.querySelector("#fechaInicial").value
    let explicacion = div.querySelector("#explicacionAccion").value
    let seguimiento = div.querySelector("#seguimientoAccion").value
    let estado = div.querySelector("#estadoSelect").value
    let tipo = div.querySelector("#tipoSelect").value
    let plazoImplementacion = div.querySelector("#plazoImplementacion").value
    let ultimoSeguimiento = div.querySelector("#ultimoSeguimiento").value
    let identificador = div.querySelector("#identificador").value

    console.log(plazoImplementacion);

    let params = {
        "explicacion": explicacion,
        "idAc": idAc,
        "responsable": responsable,
        "fechaInicial": fechaInicial,
        "seguimiento": seguimiento,
        "estado": estado,
        "tipo": tipo,
        "plazoImplementacion": plazoImplementacion,
        "identificador": identificador,
        "fechaCierre": "",
        "ultimoSeguimiento": ultimoSeguimiento
    }

    if(estado === "Cerrada") {
        params.fechaCierre = div.querySelector("#fechaCierre").value
    }

    if(responsable === "otro"){
        params.responsable = div.querySelector("#responsableSelectOtro").value
    }

    console.log(params.fechaCierre);

    go(config.rootUrl + "/updateAccion", 'POST', params)
    .then(d => {console.log("todo ok")
        modifiedToast.show();
    })
    .catch(() => {console.log("Error en catch updateAccion");

    })
    
}

function updateDescripcionEvidencia(e){
    let descripcion = e.target.value
    let evidenciaId = e.target.closest(".evidenciaDiv").id
    console.log(descripcion);
    console.log(evidenciaId);

    let params = {
        "descripcion": descripcion,
        "evidenciaId": evidenciaId
    }

    go(config.rootUrl + "/updateDescripcionEvidencia", 'POST', params)
    .then(d => {console.log("todo ok")
        
    })
    .catch(() => {console.log("Error en catch updateDescripcionEvidencia");

    })
}

function updateEvidencia(div, idAc){
    console.log("--- inside updateEvidencia ---");
    let evidencia = div.querySelector("#evidencia");
    console.log(evidencia);
    let descripcion = div.querySelector("#descripcionEvidencia").value

    console.log("evidencia desc: " + descripcion);

    let formData = new FormData();
    formData.append("idAc", idAc)
    formData.append("descripcion", descripcion)
    formData.append("evidencia", evidencia.files[0])

    console.log("antes de files[0]");
    let fileName = evidencia.files[0].name;
    console.log("despues de files[0]");
    let fileExtension = fileName.split('.').pop();
    fileExtension = "." + fileExtension
    console.log("despues de fileExtension");

    formData.append("name", fileName)
    formData.append("extension", fileExtension)

    console.log("evidencia name: " + fileName);

    

    go("/updateEvidencia", "POST", formData, {}).then(d => {
        console.log("dentro de then");
    }).catch((e) =>{ 
        console.log("Error en catch updateEvidencia");
    });
}

function multipleSelect(e){
    var selectElement = e.target;
    var selectedValues = Array.from(selectElement.selectedOptions).map(option => option.value);
    console.log(selectedValues);
}

function otraCausaIsSelected(){
    console.log("--- inside otraCausa ---");
    let checkboxes = document.querySelectorAll('input[type="checkbox"][name="causa"]');
    let isOtroSelected = false; // add this variable

    checkboxes.forEach(function(checkbox) {
        if(checkbox.value === "otro" && checkbox.checked) { // also check if the checkbox is checked
            isOtroSelected = true; // set the variable to true
        }
    });

    return isOtroSelected;

}

function manageCausasMultipleSelect(){
    console.log("--- inside manageCausasMultipleSelect ---");
    if (otraCausaIsSelected()) {
        let otroDiv = document.getElementById("analisisCausasSelectOtroDiv");
        otroDiv.style.display = "block"
    } else {
        let otroDiv = document.getElementById("analisisCausasSelectOtroDiv");
        otroDiv.style.display = "none"
    }
}

