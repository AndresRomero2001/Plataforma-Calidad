
function manageSelect(e){
    if (e.target.value === "otro") {
        let otroDiv = document.getElementById(e.target.id + "OtroDiv");
        otroDiv.style.display = "block"
    } else {
        let otroDiv = document.getElementById(e.target.id + "OtroDiv");
        otroDiv.style.display = "none"
    }
}

function manageAccionSelect(e){
    let accion = e.target.closest(".accordion-item")
    if (e.target.value === "otro") {
        let otroDiv = accion.querySelector("#" + e.target.id + "OtroDiv");
        otroDiv.style.display = "block"
        console.log(otroDiv);

    } else {
        let otroDiv = accion.querySelector("#" + e.target.id + "OtroDiv");
        otroDiv.style.display = "none"
        console.log(otroDiv);
    }
}

function manageSelectCerrada(e){
    let accion = e.target.closest(".accordion-item")
    if (e.target.value === "Cerrada") {
        let otroDiv = accion.querySelector("#" + e.target.id + "CerradaDiv");
        otroDiv.style.display = "block"
    } else {
        let otroDiv = accion.querySelector("#" + e.target.id + "CerradaDiv");
        otroDiv.style.display = "none"
    }
}

function preloadCurrentDate(dateInput){
    let currentDate = new Date();

    // Format date as yyyy-mm-dd
    let formattedDate = currentDate.getFullYear() + '-' +
                        ('0' + (currentDate.getMonth() + 1)).slice(-2) + '-' + 
                        ('0' + currentDate.getDate()).slice(-2);
    
    dateInput.value = formattedDate
}

function reverseDate(date){
    return date.split('-').reverse().join('-');
}

function manageSwitch(e) {
    let accordion = e.target.closest(".accordion-item")
    let incidenciasRow = accordion.querySelector("#incidenciasRow")
    
    if(e.target.checked){
        incidenciasRow.style.display = "block"
    } else {
        incidenciasRow.style.display = "none"
    }
    
}

function getSwitchData(switchId, textId) {
    let switchState = document.getElementById(switchId).checked;
    let switchText = switchState ? document.getElementById(textId).value : "";
    return {
        state: switchState,
        text: switchText
    };
}

function addNCForm(){
    let html= `
        <div class="col col-md-6 ncCol">
            <div class="row">
                <div class="col col-md-6">
                    <label for="ncId" class="inputLabel">ID</label>
                    <input type="text" class="form-control" id="ncId">
                </div>
                <div class="col col-md-6">
                    
                </div>
            </div>
            
        </div>
    `
    document.querySelector("#ncRow").insertAdjacentHTML("beforeend", html);
}

function checkExistsNC(e){
    let ncId = e.target.value
    existsNCQuery(ncId).then(exists => {
        if(!exists){
            e.target.closest(".ncCol").querySelector("#showNC").disabled = true;
        } else {
            e.target.closest(".ncCol").querySelector("#showNC").disabled = false;
        }
    });
    
}

function existsNCQuery(ncId){
    let params = {
        "ncId": ncId
    }
    return go(config.rootUrl + "/checkExistsNC", 'POST', params)
    .catch(() => {console.log("Error en catch checkExistsNC");

    })
}

function showNC(e){
    let ncId = e.target.closest(".ncCol").querySelector("#ncId").value;
    window.location.href = "/manageNoConformidad?ncId=" + ncId
}
