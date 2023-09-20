

const eliminarRegistroFormularioModal = new bootstrap.Modal(document.querySelector('#eliminarRegistroFormularioModal'));
const eliminarDocumentoModal = new bootstrap.Modal(document.querySelector('#eliminarDocumentoModal'));
const modifiedToast = new bootstrap.Toast(document.querySelector('#modifiedToast'));

var documentoToDelete;

function addDocumentoExtraForm() {
    let html = `
        <div class="col col-md-6 fileCol docExtra">
            <label for="" class="inputLabel">Documento</label>
            <input type="file" class="form-control documentoExtra" onchange="uploadReclamacionFile(event, 'documentoExtra')"></input>
        </div>
    `
    document.getElementById("colToAppendDocumentosExtra").insertAdjacentHTML("beforebegin", html);
}

function addDocumentoRespuestaExtraForm() {
    let html = `
        <div class="col col-md-6 fileCol docRespuestaExtra">
            <label for="" class="inputLabel">Documento</label>
            <input type="file" class="form-control documentoExtra" onchange="uploadReclamacionFile(event, 'documentoRespuestaExtra')"></input>
        </div>
    `
    document.getElementById("colToAppendDocumentosRespuestaExtra").insertAdjacentHTML("beforebegin", html);
}

function addReclamacion(){
    
    let numeroExpediente = document.getElementById("numeroExpediente").value
    let acronimo = document.getElementById("acronimo").value
    let fechaRecepcion = document.getElementById("fechaRecepcion").value
    let fechaMaximaRespuesta = document.getElementById("fechaMaximaRespuesta").value
    let consultora = document.getElementById("consultora").value
    let registroFormulario = document.getElementById("registroFormulario")
    let file = registroFormulario.files[0];
    let estado = document.getElementById("estadoSelect").value


    let params = { 
        "numeroExpediente": numeroExpediente,
        "acronimo": acronimo,
        "consultora": consultora,
        "fechaRecepcion": fechaRecepcion,
        "fechaMaximaRespuesta": fechaMaximaRespuesta,
        "estado": estado
    };

    go(config.rootUrl + "/rc/addReclamacion", 'POST', params)
    .then(d => {console.log("todo ok")
        let rcId = d["rcId"]

        if(file){
            addRegistroFormulario(rcId, file)
            /* window.location.href = '/rc/manageReclamacion?rcId='+rcId; */
        }
        document.querySelectorAll(".docExtra").forEach(docExtra => {
            let file = docExtra.querySelector(".documentoExtra").files[0];
            addReclamacionFile(rcId, file, "documentoExtra")
        }); 
        document.querySelectorAll(".docRespuestaExtra").forEach(docRespuestaExtra => {
            let file = docRespuestaExtra.querySelector(".documentoExtra").files[0];
            addReclamacionFile(rcId, file, "documentoRespuestaExtra")
        }); 
    })
    .catch(() => {console.log("Error en catch addReclamacion");

    })
}

function uploadReclamacionFile(e, fileClass){
    console.log("fileclass: " + fileClass);
    let rcId = document.getElementById("rcId").value
    let archivo = e.target.files[0];
    addReclamacionFile(rcId, archivo, fileClass).then(rcFile => {
        let col = e.target.closest(".fileCol");
        let html = `
            <label for="documentoExtra" class="inputLabel">Documento</label>
            <input type="text" class="form-control reclamacionArchivo" id="documentoExtra" value="${rcFile.archivo.name}" disabled>
            <a class="iconLinkButton " href="/media/documentosExtra/${rcFile.archivo.id}${rcFile.archivo.extension}?downloadName=${rcFile.archivo.name}">
                <button type="button" class="btn btn-primary downloadDocumentoButton">
                    <i class="bi bi-file-earmark-arrow-down"></i>
                </button>
            </a>
            <button type="button" class="btn btn-danger" id="deleteDocumentoButton" data-bs-toggle="modal" data-bs-target="#eliminarDocumentoModal" onclick="updateDocumentoToDelete(event)">
                <i class="bi bi-trash"></i>
            </button>
            <input type="hidden" value="${rcFile.id}" id="rcFileId"></input>
        `
        col.innerHTML = html;
    })
}

function addReclamacionFile(rcId, archivo, fileClass){
    
    let formData = new FormData();
    formData.append("rcId", rcId)
    formData.append("archivo", archivo)
    formData.append("fileClass", fileClass)

    let fileName = archivo.name;
    let fileExtension = fileName.split('.').pop();
    fileExtension = "." + fileExtension

    formData.append("name", fileName)
    formData.append("extension", fileExtension)

    console.log("rcId: " + rcId + " archivo: " + archivo + " fileClass: " + fileClass + " fileName: " + fileName + " fileExtension: " + fileExtension);

    // hay que devolver una promesa para el array de promesas de addAccion
    return go("/rc/addReclamacionFile", "POST", formData, {}).then(rcFile => {
        console.log("dentro de then");
        modifiedToast.show()
        return rcFile
    }).catch((e) =>{ 
        console.log("Error en catch addReclamacionFile");
    });
}

function uploadRegistroFormulario(e){
    let rcId = document.getElementById("rcId").value
    let archivo = e.target.files[0];
    addRegistroFormulario(rcId, archivo).then(file => {
        modifiedToast.show();
        let col = document.getElementById("registroFormularioCol");
        let html = `
            <label for="registroFormulario" class="inputLabel">Registro del formulario web*</label>
            <input type="text" class="form-control reclamacionArchivo" id="reclamacion" value="${file.name}" disabled>
            <a class="iconLinkButton " href="/media/reclamaciones/${file.id}${file.extension}?downloadName=${file.name}">
                <button type="button" class="btn btn-primary downloadDocumentoButton">
                    <i class="bi bi-file-earmark-arrow-down"></i>
                </button>
            </a>
            <button type="button" class="btn btn-danger" id="deleteDocumentoButton" data-bs-toggle="modal" data-bs-target="#eliminarRegistroFormularioModal" onclick="updateDocumentoToDelete(event)">
                <i class="bi bi-trash"></i>
            </button>
            <input type="hidden" value="${file.id}" id="rcFileId"></input>
        `
        col.innerHTML = html;
    })

}

function addRegistroFormulario(rcId, archivo){
    
    let formData = new FormData();
    formData.append("rcId", rcId)
    formData.append("archivo", archivo)

    let fileName = archivo.name;
    let fileExtension = fileName.split('.').pop();
    fileExtension = "." + fileExtension

    formData.append("name", fileName)
    formData.append("extension", fileExtension)

    console.log("rcId: " + rcId + " archivo: " + archivo + " fileName: " + fileName + " fileExtension: " + fileExtension);

    // hay que devolver una promesa para el array de promesas de addAccion
    return go("/rc/addRegistroFormulario", "POST", formData, {}).then(file => {
        console.log("dentro de then");
        
        return file
    }).catch((e) =>{ 
        console.log("Error en catch addRegistroFormulario");
    });
}

function deleteRegistroFormulario() {
    let rcId = document.getElementById("rcId").value
    let params = {"rcId": rcId}

    go("/rc/deleteRegistroFormulario", "POST", params).then(d => {
        console.log("borrado correctamente");
        let col = document.getElementById("registroFormularioCol");
        let html = `
            <label for="registroFormulario" class="inputLabel">Registro del formulario web*</label>
            <input type="file" class="form-control" id="registroFormulario" name="registroFormulario" onchange="uploadRegistroFormulario(event)">
        `
        col.innerHTML = html;
        eliminarRegistroFormularioModal.hide();
        modifiedToast.show()
    }).catch((e) =>{ 
        console.log("Error en catch deleteRegistroFormulario");
    });
}

function updateDocumentoToDelete(e) {
    documentoToDelete = e.target.closest(".fileCol")
}

function deleteDocumento(){
    let rcId = document.getElementById("rcId").value
    let rcFileId = documentoToDelete.closest(".fileCol").querySelector("#rcFileId").value
    let params = {
        "rcId": rcId,
        "rcFileId": rcFileId
    }

    go(config.rootUrl + "/rc/deleteDocumento", 'POST', params)
    .then(d => {
        console.log("todo ok");
        documentoToDelete.remove()
        eliminarDocumentoModal.hide()
        modifiedToast.show()
        if(countClassElements("docExtra") == 0){
            let html = `
                <div class="col col-md-6 fileCol docExtra">
                    <label for="" class="inputLabel">Documento</label>
                    <input type="file" class="form-control documentoExtra" onchange="uploadReclamacionFile(event, 'documentoExtra')"></input>
                </div>
            `
            document.getElementById("colToAppendDocumentosExtra").insertAdjacentHTML("beforebegin", html);
        } else if (countClassElements("docRespuestaExtra") == 0) {
            let html = `
                <div class="col col-md-6 fileCol docRespuestaExtra">
                    <label for="" class="inputLabel">Documento</label>
                    <input type="file" class="form-control documentoExtra" onchange="uploadReclamacionFile(event, 'documentoRespuestaExtra')"></input>
                </div>
            `
            document.getElementById("colToAppendDocumentosRespuestaExtra").insertAdjacentHTML("beforebegin", html);
        }
    })
    .catch(() => {console.log("Error en catch deleteDocumento");

    })
}

function autoloadFechaMaximaRespuesta(e) {
    let fechaRecepcion = e.target.value;
    
    // Convert the string date into a Date object
    let date = new Date(fechaRecepcion);
    
    // Add 2 months to the date
    date.setMonth(date.getMonth() + 2);

    // Convert the Date object back to string format 'YYYY-MM-DD'
    let updatedDate = date.toISOString().slice(0,10);

    let fechaMaximaRespuesta = document.getElementById("fechaMaximaRespuesta");
    fechaMaximaRespuesta.value = updatedDate;
}

function updateReclamacion(){
    
    let numeroExpediente = document.getElementById("numeroExpediente").value
    let acronimo = document.getElementById("acronimo").value
    let fechaRecepcion = document.getElementById("fechaRecepcion").value
    let fechaMaximaRespuesta = document.getElementById("fechaMaximaRespuesta").value
    let consultora = document.getElementById("consultora").value
    let estado = document.getElementById("estadoSelect").value
    let rcId = document.getElementById("rcId").value

    let params = { 
        "numeroExpediente": numeroExpediente,
        "acronimo": acronimo,
        "consultora": consultora,
        "fechaRecepcion": fechaRecepcion,
        "fechaMaximaRespuesta": fechaMaximaRespuesta,
        "estado": estado,
        "rcId": rcId
    };

    go(config.rootUrl + "/rc/updateReclamacion", 'POST', params)
    .then(d => {console.log("todo ok")
        let rcId = d["rcId"]

        modifiedToast.show()
    })
    .catch(() => {console.log("Error en catch updateReclamacion");

    })
}

function countClassElements(classToCount){
    let elements = document.getElementsByClassName(classToCount);
    for (let i = 0; i < elements.length; i++) {
        console.log(elements[i]);
    }
    console.log("elements.length: " + elements.length);
    return elements.length;
}