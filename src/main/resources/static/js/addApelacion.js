

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

function addDocumentoRegistroRecepcion(){
    let html = `
        <div class="col col-md-6 jsCol">
            <label for="documentoRegistroRecepcion" class="inputLabel">Documento</label>
            <input type="file" class="form-control documentoRegistroRecepcion">
        </div>
    `
    document.querySelector("#colToAppendDocumentoRegistroRecepcion").insertAdjacentHTML("beforebegin", html);
}

function addDocumentoAcuseRecibo(){
    let html = `
        <div class="col col-md-6 jsCol">
            <label for="documentoAcuseRecibo" class="inputLabel">Documento</label>
            <input type="file" class="form-control documentoAcuseRecibo">
        </div>
    `
    document.querySelector("#colToAppendDocumentoAcuseRecibo").insertAdjacentHTML("beforebegin", html);
}

function addDocumentoInformeRespuesta(){
    let html = `
        <div class="col col-md-6 jsCol">
            <label for="documentoInformeRespuesta" class="inputLabel">Documento</label>
            <input type="file" class="form-control documentoInformeRespuesta">
        </div>
    `
    document.querySelector("#colToAppendDocumentoInformeRespuesta").insertAdjacentHTML("beforebegin", html);
}

function checkCreateAp(){
    let numeroExpediente = document.getElementById("numeroExpediente").value
    let acronimo = document.getElementById("acronimo").value
    let fechaCertificacion = document.getElementById("fechaCertificacion").value
    let cliente = document.getElementById("cliente").value
    let fechaRegistroRecepcion = document.getElementById("fechaRegistroRecepcion").value

    if(numeroExpediente && acronimo && fechaCertificacion && cliente && fechaRegistroRecepcion){
        document.getElementById("createApButton").disabled = false
    } else {
        document.getElementById("createApButton").disabled = true
    }
   
}

function createApelacion(){
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

    console.log("comentarios: " + comentarios);

    let params = {
        "numeroExpediente": numeroExpediente,
        "acronimo": acronimo,
        "fechaCertificacion": fechaCertificacion,
        "cliente": cliente,
        "fechaRegistroRecepcion": fechaRegistroRecepcion,
        "fechaAcuseRecibo": fechaAcuseRecibo,
        "fechaInformeRespuesta": fechaInformeRespuesta,
        "plazoLimite": plazoLimite,
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
        "resolucion": resolucion,
        "ncIds": ncIds,
        "comentarios": comentarios
    }

    go(config.rootUrl + "/ap/addApelacion", 'POST', params)
    .then(d => {console.log("todo ok")
        let apId = d["apId"];
        let promises = [];

        function processFiles(inputClass) {
            document.querySelectorAll(inputClass).forEach(inputFile => {
                let file = inputFile.files[0];
                if (file) {
                    promises.push(addApFile(apId, file, inputClass.slice(1))); // Add the promise to the promises array
                }
            });
        }

        processFiles('.documentoRegistroRecepcion');
        processFiles('.documentoAcuseRecibo');
        processFiles('.documentoInformeRespuesta');

        Promise.all(promises).then((values) => { 
            window.location.href = "/ap/manageApelacion?apId=" + apId
        });
        
    })
    .catch(() => {console.log("Error en catch addApelacion");

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