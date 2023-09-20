

const desvincularNCModal = new bootstrap.Modal(document.querySelector('#desvincularNCModal'));
const desvincularCodigoUnescoModal = new bootstrap.Modal(document.querySelector('#desvincularCodigoUnescoModal'));
const modifiedToast = new bootstrap.Toast(document.querySelector('#modifiedToast'));
const errorToast = new bootstrap.Toast(document.querySelector('#numeroTAExistsToast'));

window.onload = function() {

    let selectedOption = document.getElementById("motivoSelect")
    if(selectedOption.value === "otro"){
        document.getElementById("motivoSelectOtroDiv").style.display = "block"
        document.getElementById("motivoSelectOtro").value = document.getElementById("motivoText").value
    }

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

}

document.addEventListener("DOMContentLoaded", function() {
    // Query all switches within the accordion bodies.
    const switches = document.querySelectorAll('.accordion-body .customSwitch');
  
    switches.forEach((switchEl) => {
      const column = switchEl.closest('.col');
      const incidenciasRowDiv = column.querySelector('.incidenciasRowDiv');
  
      // Show or hide the textarea based on the switch's state.
      incidenciasRowDiv.style.display = switchEl.checked ? 'block' : 'none';
    });
});

function addCodigoUnescoForm(){
    let html = `
        <div class="col col-md-6 codigoUnescoCol">
            <label class="inputLabel">Código UNESCO</label>
            <input type="text" class="form-control codigoUnesco" id="cuId" onkeypress="return event.charCode >= 48 && event.charCode <= 57" oninput="updateTA()" maxlength="4"></input>
            <button type="button" class="btn btn-danger deleteCodigoUnescoButton" id="unbindCodigoUnescoButton" data-bs-toggle="modal" data-bs-target="#desvincularCodigoUnescoModal" onclick="updateCodigoUnescoToUnbind(event)">
                <i class="bi bi-trash ncIcon"></i>
            </button>
        </div>
    `
    document.querySelector("#codigosUnescoRow").insertAdjacentHTML("beforeend", html);
}

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

function ncAlreadyBinded(ncId, taId){
    let params = {
        "ncId": ncId,
        "taId": taId
    }
    return go(config.rootUrl + "/ta/ncAlreadyBinded", 'POST', params)
    .catch(() => {console.log("Error en catch ncAlreadyBinded");

    })
}

function bindNC(e){
    let ncCol = e.target.closest(".ncCol")
    let ncId = ncCol.querySelector("#ncId").value
    let taId = document.getElementById("taId").value
    let params = {
        "ncId": ncId,
        "taId": taId
    }

    // si existe se vincula, si no no
    existsNCQuery(ncId).then(exists => {
        if(!exists){
            console.log("no existe");
            let idLabel = ncCol.querySelector("#ncIdLabel")
            idLabel.classList.add("text-danger")
            idLabel.textContent = "ID introducido no existe"
        } else {
            ncAlreadyBinded(ncId, taId).then(alreadyBinded => {
                if(alreadyBinded) {
                    let idLabel = ncCol.querySelector("#ncIdLabel")
                    idLabel.classList.add("text-danger")
                    idLabel.textContent = "NC ya vinculada"
                } else {
                    go(config.rootUrl + "/ta/bindNC", 'POST', params)
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

var ncColSelected

function updateNCToUnbind(e){
    ncColSelected = e.target.closest(".ncCol")
}

var codigoUnescoColSelected

function updateCodigoUnescoToUnbind(e){
    codigoUnescoColSelected = e.target.closest(".codigoUnescoCol")
}

function unbindNC(){
    let ncId = ncColSelected.querySelector("#showNC").value
    let taId = document.getElementById("taId").value
    let params = {
        "ncId": ncId,
        "taId": taId
    }

    go(config.rootUrl + "/ta/unbindNC", 'POST', params)
    .then(d => {
        console.log("todo ok");
        ncColSelected.remove()
        desvincularNCModal.hide()
        modifiedToast.show()
    })
    .catch(() => {console.log("Error en catch unbindNC");

    })
}

function unbindCodigoUnesco(){
    let taId = document.getElementById("taId").value
    let cuId = codigoUnescoColSelected.querySelector("#cuId").value

    let params = {
        "cuId": cuId,
        "taId": taId
    }

    go(config.rootUrl + "/ta/unbindCodigoUnesco", 'POST', params)
    .then(d => {
        console.log("todo ok");
        codigoUnescoColSelected.remove()
        desvincularCodigoUnescoModal.hide()
        modifiedToast.show()
    })
    .catch(() => {console.log("Error en catch unbindCodigoUnesco");

    })
}

function checkUpdateTA(e){
    if(e.target.value !== "otro"){
        updateTA();
    }
}

function updateTA(){
    console.log("--- inside updateTA ---");

    let numeroTA = document.getElementById("numeroTA").value
    let numeroExpediente = document.getElementById("numeroExpediente").value
    let acronimo = document.getElementById("acronimo").value
    let ejercicioFiscal = document.getElementById("ejercicioFiscal").value
    let numeroIMV = document.getElementById("numeroIMV").value
    let cliente = document.getElementById("cliente").value
    let resolucionSelect = document.getElementById("resolucionSelect").value
    let llegadaNotificacion = document.getElementById("llegadaNotificacion").value
    let consultora = document.getElementById("consultora").value
    let experto4d = document.getElementById("experto4d").value
    let expertoTecnico = document.getElementById("expertoTecnico").value
    let motivoSelect = document.getElementById("motivoSelect").value
    let resolucionDefinitiva = document.getElementById("resolucionDefinitivaSelect").value
    let fechaInterposicion = document.getElementById("fechaInterposicion").value
    let fechaResolucionMinisterio = document.getElementById("fechaResolucionMinisterio").value
    let fechaEmisionInforme = document.getElementById("fechaEmision").value

    var codigosUnescoInputs = document.querySelectorAll('.codigoUnesco');
    var codigosUnesco = Array.from(codigosUnescoInputs).map(input => input.value);

    console.log(codigosUnesco);

    let revisionDocumentalData = getSwitchData("revisionDocumentalSwitch", "incidenciasRevisionDocumental");
    let solicitudRequerimientosData = getSwitchData("solicitudRequerimientosSwitch", "incidenciasSolicitudRequerimientos");
    let imparcialidadExpertosData = getSwitchData("imparcialidadExpertosSwitch", "incidenciasImparcialidadExpertos");
    let revisionInforme4dData = getSwitchData("revisionInforme4dSwitch", "incidenciasRevisionInforme4d");
    let verificacionCompetenciasData = getSwitchData("verificacionCompetenciasSwitch", "incidenciasVerificacionCompetencias");
    let revisionInformeTecnicoData = getSwitchData("revisionInformeTecnicoSwitch", "incidenciasRevisionInformeTecnico");
    let revisionEvaluacionContableData = getSwitchData("revisionEvaluacionContableSwitch", "incidenciasRevisionEvaluacionContable");
    let documentoCertificacionData = getSwitchData("documentoCertificacionSwitch", "incidenciasDocumentoCertificacion");

    let ncList = document.querySelectorAll(".ncCol")
    let ncIds = Array.from(ncList).map(nc => nc.querySelector("#ncId").value)

    let comentarios = document.getElementById("comentarios").value

    let taId = document.getElementById("taId").value

    let params = {
        "numeroTA": numeroTA,
        "numeroExpediente": numeroExpediente,
        "acronimo": acronimo,
        "ejercicioFiscal": ejercicioFiscal,
        "numeroIMV": numeroIMV,
        "cliente": cliente,
        "resolucion": resolucionSelect,
        "llegadaNotificacion": llegadaNotificacion,
        "consultora": consultora,
        "experto4d": experto4d,
        "expertoTecnico": expertoTecnico,
        "motivo": motivoSelect,
        "resolucionDefinitiva": resolucionDefinitiva,
        "fechaInterposicion": fechaInterposicion,
        "fechaResolucionMinisterio": fechaResolucionMinisterio,
        "fechaEmisionInforme": fechaEmisionInforme,
        "codigosUnesco": codigosUnesco,
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
        "comentarios": comentarios,
        "taId": taId
    };

    if(motivoSelect === "otro"){
        params.motivo = document.getElementById("motivoSelectOtro").value
    }

    go(config.rootUrl + "/ta/updateTramiteAudiencia", 'POST', params)
    .then(d => {console.log("todo ok")
        modifiedToast.show()
    })
    .catch(() => {console.log("Error en catch updateTramiteAudiencia");
        errorToast.show()
    })
}