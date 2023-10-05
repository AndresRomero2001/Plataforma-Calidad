

const desvincularNCModal = new bootstrap.Modal(document.querySelector('#desvincularNCModal'));
const eliminarModal = new bootstrap.Modal(document.querySelector('#eliminarDocumentoApelacionModal'));
const modifiedToast = new bootstrap.Toast(document.querySelector('#modifiedToast'));
let documentoToDelete;

document.addEventListener("DOMContentLoaded", function() {
    // Query all switches within the accordion bodies.
    const switches = document.querySelectorAll('.accordion-body .customSwitch');
  
    switches.forEach((switchEl) => {
      const column = switchEl.closest('.col');
      const incidenciasRowDiv = column.querySelector('.incidenciasRowDiv');
  
      // Show or hide the textarea based on the switch's state.
      incidenciasRowDiv.style.display = switchEl.checked ? 'block' : 'none';
    });

    let ncCols = document.querySelectorAll('.ncCol');
    ncCols.forEach((ncCol) => {
        let ncIdInput = ncCol.querySelector('#ncId');
        console.log(ncIdInput.value);
        existsNCQuery(ncIdInput.value).then(exsits => {
            if(!exsits){
                ncCol.querySelector('#showNC').disabled = true;
            } else {
                ncCol.querySelector('#showNC').disabled = false;
            }
        });
        
    });
});

function addNCForm(){
    let html= `
        <div class="col col-md-6 ncCol">
            <div class="row">
                <div class="col col-md-6">
                    <label for="ncId" id="ncIdLabel" class="inputLabel">ID</label>
                    <input type="text" class="form-control" id="ncId">
                </div>
                <div class="col col-md-6 verNcCol" id="buttonsCol">
                    <label for="bindNCButton" class="inputLabel invisibleLabel">Vincular</label>
                    <button type="buttoN" class="btn btn-primary bindNC" id="bindNCButton" onclick="bindNC(event)">Vincular</button>
                </div>
            </div>
            
        </div>
    `
    document.querySelector("#ncRow").insertAdjacentHTML("beforeend", html);
}

function updateApelacion(){
    let apId = document.getElementById("apId").value
    let numeroExpediente = document.getElementById("numeroExpediente").value
    let acronimo = document.getElementById("acronimo").value
    let fechaCertificacion = document.getElementById("fechaCertificacion").value
    let cliente = document.getElementById("cliente").value
    let fechaRegistroRecepcion = document.getElementById("fechaRegistroRecepcion").value
    let fechaAcuseRecibo = document.getElementById("fechaAcuseRecibo").value
    let fechaInformeRespuesta = document.getElementById("fechaInformeRespuesta").value
    let plazoLimite = document.getElementById("plazoLimite").value

    let revisionDocumentalData = getSwitchData("revisionDocumentalSwitch", "incidenciasRevisionDocumental");
    let solicitudRequerimientosData = getSwitchData("solicitudRequerimientosSwitch", "incidenciasSolicitudRequerimientos");
    let imparcialidadExpertosData = getSwitchData("imparcialidadExpertosSwitch", "incidenciasImparcialidadExpertos");
    let revisionInforme4dData = getSwitchData("revisionInforme4dSwitch", "incidenciasRevisionInforme4d");
    let verificacionCompetenciasData = getSwitchData("verificacionCompetenciasSwitch", "incidenciasVerificacionCompetencias");
    let revisionInformeTecnicoData = getSwitchData("revisionInformeTecnicoSwitch", "incidenciasRevisionInformeTecnico");
    let revisionEvaluacionContableData = getSwitchData("revisionEvaluacionContableSwitch", "incidenciasRevisionEvaluacionContable");
    let documentoCertificacionData = getSwitchData("documentoCertificacionSwitch", "incidenciasDocumentoCertificacion");

    let resolucion = document.getElementById("resolucionSelect").value

    let ncList = document.querySelectorAll(".ncCol")
    let ncIds = Array.from(ncList).map(nc => nc.querySelector("#ncId").value)

    let comentarios = document.getElementById("comentarios").value

    let params = {
        "numeroExpediente": numeroExpediente,
        "acronimo": acronimo,
        "fechaCertificacion": fechaCertificacion,
        "cliente": cliente,
        "fechaRegistroRecepcion": fechaRegistroRecepcion,
        "fechaAcuseRecibo": fechaAcuseRecibo,
        "fechaInformeRespuesta": fechaInformeRespuesta,
        "revisionDocumental": revisionDocumentalData.state,
        "revisionDocumentalText": revisionDocumentalData.text,
        "solicitudRequerimientos": solicitudRequerimientosData.state,
        "solicitudRequerimientosText": solicitudRequerimientosData.text,
        "imparcialidadExpertos": imparcialidadExpertosData.state,
        "imparcialidadExpertosText": imparcialidadExpertosData.text,
        "revisionInforme4d": revisionInforme4dData.state,
        "revisionInforme4dText": revisionInforme4dData.text,
        "verificacionCompetencias": verificacionCompetenciasData.state,
        "verificacionCompetenciasText": verificacionCompetenciasData.text,
        "revisionInformeTecnico": revisionInformeTecnicoData.state,
        "revisionInformeTecnicoText": revisionInformeTecnicoData.text,
        "revisionEvaluacionContable": revisionEvaluacionContableData.state,
        "revisionEvaluacionContableText": revisionEvaluacionContableData.text,
        "documentoCertificacion": documentoCertificacionData.state,
        "documentoCertificacionText": documentoCertificacionData.text,
        "ncIds": ncIds,
        "resolucion": resolucion,
        "apId": apId,
        "plazoLimite": plazoLimite,
        "comentarios": comentarios
    }

    go(config.rootUrl + "/ap/updateApelacion", 'POST', params)
    .then(d => {console.log("todo ok")
        modifiedToast.show()
    })
    .catch(() => {console.log("Error en catch updateApelacion");

    })
}

function bindNC(e){
    let ncCol = e.target.closest(".ncCol")
    let ncId = ncCol.querySelector("#ncId").value
    let apId = document.getElementById("apId").value
    let params = {
        "ncId": ncId,
        "apId": apId
    }

    // si existe se vincula, si no no
    existsNCQuery(ncId).then(exists => {
        if(!exists){
            console.log("no existe");
            let idLabel = ncCol.querySelector("#ncIdLabel")
            idLabel.classList.add("text-danger")
            idLabel.textContent = "ID introducido no existe"
        } else {
            ncAlreadyBinded(ncId, apId).then(alreadyBinded => {
                if(alreadyBinded) {
                    let idLabel = ncCol.querySelector("#ncIdLabel")
                    idLabel.classList.add("text-danger")
                    idLabel.textContent = "NC ya vinculada"
                } else {
                    go(config.rootUrl + "/ap/bindNC", 'POST', params)
                    .then(d => {
                        console.log("todo ok");
                        let buttonsCol = ncCol.querySelector("#buttonsCol")
        
                        let html = `
                            <label for="ncId" class="inputLabel invisibleLabel">Ver N.C.</label>
                            <button class="btn btn-primary verNcButton" id="showNC" value="${ncId}" onclick="showNC(event)"><i class="bi bi-eye-fill ncIcon"></i></button>
                            <button type="button" class="btn btn-danger deleteNcButton" id="unbindNCButton" data-bs-toggle="modal" data-bs-target="#desvincularNCModal" onclick="updateNCToUnbind(event)">
                                <i class="bi bi-trash ncIcon"></i>
                            </button>
                        `
                        buttonsCol.innerHTML = html
        
                        ncCol.querySelector("#ncId").disabled = true
                        let idLabel = ncCol.querySelector("#ncIdLabel")
                        idLabel.classList.remove("text-danger")
                        idLabel.textContent = "ID"

                        modifiedToast.show()
                    })
                    .catch(() => {console.log("Error en catch bindNC");
        
                    })
                }
            }); 
            
        }
    });
    
}

function ncAlreadyBinded(ncId, apId){
    let params = {
        "ncId": ncId,
        "apId": apId
    }
    return go(config.rootUrl + "/ap/ncAlreadyBinded", 'POST', params)
    .catch(() => {console.log("Error en catch ncAlreadyBinded");

    })
}

var ncColSelected

function updateNCToUnbind(e){
    ncColSelected = e.target.closest(".ncCol")
}

function unbindNC(){
    let ncId = ncColSelected.querySelector("#showNC").value
    let apId = document.getElementById("apId").value
    let params = {
        "ncId": ncId,
        "apId": apId
    }

    go(config.rootUrl + "/ap/unbindNC", 'POST', params)
    .then(d => {
        console.log("todo ok");
        ncColSelected.remove()
        desvincularNCModal.hide()
        modifiedToast.show()
    })
    .catch(() => {console.log("Error en catch unbindNC");

    })
}

function addDocumentoRegistroRecepcionHTML(){
    let html = `
        <div class="col col-md-6 jsCol fileCol">
            <label for="documentoRegistroRecepcion" class="inputLabel">Documento</label>
            <input type="file" class="form-control documentoRegistroRecepcion" oninput="addDocumentoRegistroRecepcion(event)">
        </div>
    `
    document.querySelector("#colToAppendDocumentoRegistroRecepcion").insertAdjacentHTML("beforebegin", html);
}

function addDocumentoAcuseReciboHTML(){
    let html = `
        <div class="col col-md-6 jsCol fileCol">
            <label for="documentoAcuseRecibo" class="inputLabel">Documento</label>
            <input type="file" class="form-control documentoAcuseRecibo" oninput="addDocumentoAcuseRecibo(event)">
        </div>
    `
    document.querySelector("#colToAppendDocumentoAcuseRecibo").insertAdjacentHTML("beforebegin", html);
}

function addDocumentoInformeRespuestaHTML(){
    let html = `
        <div class="col col-md-6 jsCol fileCol">
            <label for="documentoInformeRespuesta" class="inputLabel">Documento</label>
            <input type="file" class="form-control documentoInformeRespuesta" oninput="addDocumentoInformeRespuesta(event)">
        </div>
    `
    document.querySelector("#colToAppendDocumentoInformeRespuesta").insertAdjacentHTML("beforebegin", html);
}

function updateDocumentoToDelete(e){
    documentoToDelete = e.target.closest(".fileCol")

    /* let id = documentoToDelete.querySelector("#archivoId").value
    console.log("id to be deleted: " + id); */
}

function deleteDocumento(){
    let apId = document.getElementById("apId").value
    let apFileId = documentoToDelete.id
    let fileId = documentoToDelete.querySelector("#archivoId").value
    let params = {
        "apId": apId,
        "apFileId": apFileId,
        "fileId": fileId
    }

    go(config.rootUrl + "/ap/deleteDocumento", 'POST', params)
    .then(d => {
        console.log("todo ok");
        documentoToDelete.remove()
        eliminarModal.hide()
        modifiedToast.show()
    })
    .catch(() => {console.log("Error en catch deleteDocumento");

    })
}

function addDocumentoRegistroRecepcion(e) {
    let apId = document.getElementById("apId").value
    let file = e.target.files[0]
    let fileClass = "documentoRegistroRecepcion"
    addApFile(apId, file, fileClass).then(apFile => {
        let html = `
        <div class="col col-md-6 fileCol" id="${apFile.id}">
            <label class="inputLabel">Documento</label>
            <input type="text" class="form-control apelacionArchivo" id="apelacion" value="${apFile.archivo.name}" disabled>
            <a class="iconLinkButton " href="/media/apelaciones/${apFile.archivo.id}${apFile.archivo.extension}?downloadName=${apFile.archivo.name}">
                <button type="button" class="btn btn-primary downloadDocumentoButton">
                    <i class="bi bi-file-earmark-arrow-down"></i>
                </button>
            </a>
            <input type="hidden" id="archivoId" value="${apFile.archivo.id}">
            <button type="button" class="btn btn-danger" id="deleteDocumentoButton" data-bs-toggle="modal" data-bs-target="#eliminarDocumentoApelacionModal"  onclick="updateDocumentoToDelete(event)">
                <i class="bi bi-trash"></i>
            </button>
        </div>
        `
        e.target.closest(".fileCol").remove()
        document.getElementById("colToAppendDocumentoRegistroRecepcion").insertAdjacentHTML("beforebegin", html)
        modifiedToast.show()
    })
}

function addDocumentoAcuseRecibo(e) {
    let apId = document.getElementById("apId").value
    let file = e.target.files[0]
    let fileClass = "documentoAcuseRecibo"
    addApFile(apId, file, fileClass).then(apFile => {
        let html = `
        <div class="col col-md-6 fileCol" id="${apFile.id}">
            <label class="inputLabel">Documento</label>
            <input type="text" class="form-control apelacionArchivo" id="apelacion" value="${apFile.archivo.name}" disabled>
            <a class="iconLinkButton " href="/media/apelaciones/${apFile.archivo.id}${apFile.archivo.extension}?downloadName=${apFile.archivo.name}">
                <button type="button" class="btn btn-primary downloadDocumentoButton">
                    <i class="bi bi-file-earmark-arrow-down"></i>
                </button>
            </a>
            <input type="hidden" id="archivoId" value="${apFile.archivo.id}">
            <button type="button" class="btn btn-danger" id="deleteDocumentoButton" data-bs-toggle="modal" data-bs-target="#eliminarDocumentoApelacionModal"  onclick="updateDocumentoToDelete(event)">
                <i class="bi bi-trash"></i>
            </button>
        </div>
        `
        e.target.closest(".fileCol").remove()
        document.getElementById("colToAppendDocumentoAcuseRecibo").insertAdjacentHTML("beforebegin", html)
        modifiedToast.show()
    })
}

function addDocumentoInformeRespuesta(e) {
    let apId = document.getElementById("apId").value
    let file = e.target.files[0]
    let fileClass = "documentoInformeRespuesta"
    addApFile(apId, file, fileClass).then(apFile => {
        let html = `
        <div class="col col-md-6 fileCol" id="${apFile.id}">
            <label class="inputLabel">Documento</label>
            <input type="text" class="form-control apelacionArchivo" id="apelacion" value="${apFile.archivo.name}" disabled>
            <a class="iconLinkButton " href="/media/apelaciones/${apFile.archivo.id}${apFile.archivo.extension}?downloadName=${apFile.archivo.name}">
                <button type="button" class="btn btn-primary downloadDocumentoButton">
                    <i class="bi bi-file-earmark-arrow-down"></i>
                </button>
            </a>
            <input type="hidden" id="archivoId" value="${apFile.archivo.id}">
            <button type="button" class="btn btn-danger" id="deleteDocumentoButton" data-bs-toggle="modal" data-bs-target="#eliminarDocumentoApelacionModal"  onclick="updateDocumentoToDelete(event)">
                <i class="bi bi-trash"></i>
            </button>
        </div>
        `
        e.target.closest(".fileCol").remove()
        document.getElementById("colToAppendDocumentoInformeRespuesta").insertAdjacentHTML("beforebegin", html)
        modifiedToast.show()
    })
}

function addApFile(apId, archivo, fileClass){
    
    let formData = new FormData();
    formData.append("apId", apId)
    formData.append("archivo", archivo)
    formData.append("fileClass", fileClass)

    let fileName = archivo.name;
    let fileExtension = fileName.split('.').pop();
    fileExtension = "." + fileExtension

    formData.append("name", fileName)
    formData.append("extension", fileExtension)

    console.log("apId: " + apId + " archivo: " + archivo + " fileClass: " + fileClass + " fileName: " + fileName + " fileExtension: " + fileExtension);

    // hay que devolver una promesa para el array de promesas de addAccion
    return go("/ap/addApFile", "POST", formData, {}).then(apFile => {
        console.log("dentro de then");
        return apFile
    }).catch((e) =>{ 
        console.log("Error en catch addApFile");
    });
}