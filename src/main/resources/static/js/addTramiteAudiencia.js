

const errorToast = new bootstrap.Toast(document.querySelector('#numeroTAExistsToast'));

window.onresize = positionFixedButton;

function positionFixedButton() {
    var contentBody = document.querySelector('.contentBody');
    var floatButtonDiv = document.querySelector('#floatButtonDiv');

    // Calculate the right edge of .contentBody
    var contentBodyRight = contentBody.getBoundingClientRect().right;

    // Calculate the right position for #floatButtonDiv
    var buttonRight = window.innerWidth - contentBodyRight + 30; // 30 px de cortesia para q haya un poco mas de margen

    // Set the calculated value
    floatButtonDiv.style.right = buttonRight + 'px';
}

// Initial position
positionFixedButton();


function addCodigoUnescoForm(){
    let html = `
        <div class="col col-md-6 codigoUnescoCol">
            <label class="inputLabel">Código UNESCO</label>
            <input type="text" class="form-control codigoUnesco" id="cuId" onkeypress="return event.charCode >= 48 && event.charCode <= 57" maxlength="4"></input>
        </div>
    `
    document.querySelector("#codigosUnescoRow").insertAdjacentHTML("beforeend", html);
}

function addTramiteAudiencia(){
    console.log("--- inside addTramiteAudiencia ---");

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
        "comentarios": comentarios
    };

    if(motivoSelect === "otro"){
        params.motivo = document.getElementById("motivoSelectOtro").value
    }

    go(config.rootUrl + "/ta/addTramiteAudiencia", 'POST', params)
    .then(d => {console.log("todo ok")
        window.location.href = "/ta/manageTramiteAudiencia?taId=" + d["taId"]
    })
    .catch(() => {console.log("Error en catch addTramiteAudiencia");
        errorToast.show();
    })
}

function checkCreateTA(){
    let numeroTA = document.getElementById("numeroTA").value
    let numeroExpediente = document.getElementById("numeroExpediente").value
    let acronimo = document.getElementById("acronimo").value
    let ejercicioFiscal = document.getElementById("ejercicioFiscal").value
    let numeroIMV = document.getElementById("numeroIMV").value
    let cliente = document.getElementById("cliente").value
    let resolucionSelect = document.getElementById("resolucionSelect").value
    let llegadaNotificacion = document.getElementById("llegadaNotificacion").value

    if(numeroTA && numeroExpediente && acronimo && ejercicioFiscal && numeroIMV && cliente && resolucionSelect && llegadaNotificacion && ejercicioFiscal.length === 4){
        document.getElementById("createTAButton").disabled = false
    } else {
        document.getElementById("createTAButton").disabled = true
    }
   
}

/* function checkExistsNC(e){

} */