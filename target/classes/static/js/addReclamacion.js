

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

function addDocumentoExtraForm() {
    let html = `
        <div class="col col-md-6 fileCol docExtra">
            <label for="" class="inputLabel">Documento</label>
            <input type="file" class="form-control documentoExtra"></input>
        </div>
    `
    document.getElementById("colToAppendDocumentosExtra").insertAdjacentHTML("beforebegin", html);
}

function addDocumentoRespuestaExtraForm() {
    let html = `
        <div class="col col-md-6 fileCol docRespuestaExtra">
            <label for="" class="inputLabel">Documento</label>
            <input type="file" class="form-control documentoExtra"></input>
        </div>
    `
    document.getElementById("colToAppendDocumentosRespuestaExtra").insertAdjacentHTML("beforebegin", html);
}

function checkCreateRc(){
    let numeroExpediente = document.getElementById("numeroExpediente").value
    let acronimo = document.getElementById("acronimo").value
    let fechaRecepcion = document.getElementById("fechaRecepcion").value
    let consultora = document.getElementById("consultora").value
    let registroFormulario = document.getElementById("registroFormulario")
    let file = registroFormulario.files[0];

    if(numeroExpediente && acronimo && fechaRecepcion && consultora && file){
        document.getElementById("createRcButton").disabled = false
    } else {
        document.getElementById("createRcButton").disabled = true
    }
   
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
    let comentarios = document.getElementById("comentarios").value


    let params = { 
        "numeroExpediente": numeroExpediente,
        "acronimo": acronimo,
        "consultora": consultora,
        "fechaRecepcion": fechaRecepcion,
        "fechaMaximaRespuesta": fechaMaximaRespuesta,
        "estado": estado,
        "comentarios": comentarios
    };

    go(config.rootUrl + "/rc/addReclamacion", 'POST', params)
    .then(d => {console.log("todo ok")
        let rcId = d["rcId"]
        let promises = [];

        if(file){
            promises.push(addRegistroFormulario(rcId, file))
        }
        document.querySelectorAll(".docExtra").forEach(docExtra => {
            let file = docExtra.querySelector(".documentoExtra").files[0];
            if(file) promises.push(addReclamacionFile(rcId, file, "documentoExtra"))
            
        }); 
        document.querySelectorAll(".docRespuestaExtra").forEach(docRespuestaExtra => {
            let file = docRespuestaExtra.querySelector(".documentoExtra").files[0];
            if(file) promises.push(addReclamacionFile(rcId, file, "documentoRespuestaExtra"))
        }); 

        Promise.all(promises).then((values) => { 
            window.location.href = '/rc/manageReclamacion?rcId='+rcId;
        });
    })
    .catch(() => {console.log("Error en catch addReclamacion");

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
        return rcFile
    }).catch((e) =>{ 
        console.log("Error en catch addReclamacionFile");
    });
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
    return go("/rc/addRegistroFormulario", "POST", formData, {}).then(rcFile => {
        console.log("dentro de then");
        return rcFile
    }).catch((e) =>{ 
        console.log("Error en catch addRegistroFormulario");
    });
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