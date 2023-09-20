

window.onload = function() {
    
    const causes = $('#analisisCausasSelect').filterMultiSelect();
}

document.addEventListener("DOMContentLoaded", () => {
    let fecha = document.getElementById("fecha")
    preloadCurrentDate(fecha)
});

document.addEventListener("DOMContentLoaded", () => {
    const targetNode = document.getElementById('analisisCausasSelect').parentNode; // observe the parent

    const observerOptions = {
        childList: true, // Report added/removed nodes
        subtree: true, // Also observe child nodes
    };

    const observer = new MutationObserver(() => {
        if (otraCausaIsSelected()) {
            document.getElementById("analisisCausasSelectOtroDiv").style.display = "block";
        }
    });

    observer.observe(targetNode, observerOptions);

    setTimeout(observer.disconnect(), 1000)
});

function addNoConformidad(){
    
    let origen = document.getElementById("origenSelect").value
    let fecha = document.getElementById("fecha").value
    let alcance = document.getElementById("alcanceSelect").value
    let detectadaPor = document.getElementById("detectadaPorSelect").value
    let descripcionDesviacion = document.getElementById("descripcionDesviacion").value
    let numeroExpediente = document.getElementById("numeroExpediente").value
    let acronimoExpediente = document.getElementById("acronimoExpediente").value
    let apartadosNorma = document.getElementById("apartadosNormaSelect").value
    let consultora = document.getElementById("consultora").value
    /* let analisisCausas = document.getElementById("analisisCausasSelect").value */
    let explicacionCausas = document.getElementById("explicacionCausas").value
    let analisisExtension = document.getElementById("analisisExtensionSelect").value
    let explicacionExtension = document.getElementById("explicacionExtension").value
    let asunto = document.getElementById("asunto").value
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
                    "apartadosNorma": apartadosNorma,
                    "consultora": consultora,
                    "analisisCausas": checkedValues,
                    "analisisExtension": analisisExtension,
                    "explicacionCausas": explicacionCausas,
                    "explicacionExtension": explicacionExtension,
                    "asunto": asunto,
                    "aplicaExpediente": aplicaExpediente
    }; 

    /* console.log("origen params: " + params.origen); */

    if(origen === "otro") {
        params.origen = document.getElementById("origenSelectOtro").value
    }
    
    if(alcance === "otro") {
        params.alcance = document.getElementById("alcanceSelectOtro").value
    }
    
    if(detectadaPor === "otro") {
        params.detectadaPor = document.getElementById("detectadaPorSelectOtro").value
    }

    if(apartadosNorma === "otro") {
        params.apartadosNorma = document.getElementById("apartadosNormaSelectOtro").value
    }

    /* if(analisisCausas === "otro") {
        params.analisisCausas = document.getElementById("analisisCausasSelectOtro").value
    } */
    if (checkedValues.includes("otro")) {
        checkedValues = checkedValues.filter(val => val !== "otro");
        checkedValues.push(document.getElementById("analisisCausasSelectOtro").value)
        params.analisisCausas = checkedValues
    }
    
    let documentoExtensionInput = document.getElementById("documentoExtension")
    let hasDocumentoExtension = false
    if(documentoExtensionInput.files[0]) hasDocumentoExtension = true

    go(config.rootUrl + "/addNoConformidad", 'POST', params)
    .then(d => {console.log("todo ok")
        let idNC = d["idNC"]

        if(hasDocumentoExtension){
            uploadDocumentoExtension(idNC)
        } else {
            window.location.href = '/manageNoConformidad?ncId='+idNC;
        }
    })
    .catch(() => {console.log("Error en catch addNoConformidad");

    })
}

function uploadDocumentoExtension(idNC){
    console.log("--- inside uploadDocumentoExtension ---");
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
    
    /* console.log("name: " + fileName);
    console.log("extension: " + fileExtension); */

    go("/uploadDocumentoExtension", "POST", formData, {}).then(d => {
        window.location.href = '/manageNoConformidad?ncId='+idNC;
    }).catch(
        (e) =>{ //console.log("fallo: "+ Object.values(e))
        alert("Error en uploadDocumentoExtension")
    });
}

function checkCreateNC(){
    console.log("--- inside checkCreateNC ---");

    let origen = document.getElementById("origenSelect").value
    let fecha = document.getElementById("fecha").value
    let alcance = document.getElementById("alcanceSelect").value
    let detectadaPor = document.getElementById("detectadaPorSelect").value
    let asunto = document.getElementById("asunto").value

    let activateCreateButton = true;

    if(origen === "") activateCreateButton = false;
    if(fecha === "" || fecha == null) activateCreateButton = false;
    if(alcance === "") activateCreateButton = false;
    if(detectadaPor === "") activateCreateButton = false;
    if(asunto === "") activateCreateButton = false;

    let otroAlcance = document.getElementById("alcanceSelectOtro").value
    if(alcance === "otro" && otroAlcance === "")  activateCreateButton = false;

    let otroDetectadaPor = document.getElementById("detectadaPorSelectOtro").value
    if(detectadaPor === "otro" && otroDetectadaPor === "")  activateCreateButton = false;

    let otroOrigen = document.getElementById("origenSelectOtro").value
    if(origen === "otro" && otroOrigen === "")  activateCreateButton = false;

    if(activateCreateButton) document.getElementById("addNoConformidadButton").disabled = false
    else document.getElementById("addNoConformidadButton").disabled = true;
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

function checkUpdateNC(){
    // cutre, es pq en el filter-multi-select he puesto q se llame a esta funcion para
    // cuando se esta tratando el manageNC, y da error pq ese JS lo comparte tmb addNC, y addNC
    // no tenia esta funcion
}