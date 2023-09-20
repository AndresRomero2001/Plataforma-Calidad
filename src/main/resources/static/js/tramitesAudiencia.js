

const eliminarModal = new bootstrap.Modal(document.querySelector('#eliminarModal'));
var tramiteAudienciaSelected;

function updateIdToDelete(e){
    tramiteAudienciaSelected = e.currentTarget.value
    console.log("taSelected: " + tramiteAudienciaSelected);
}

function deleteTramiteAudiencia(){
    let params = {
        "taId": tramiteAudienciaSelected
    }

    go(config.rootUrl + "/ta/deleteTramiteAudiencia", 'POST', params)
    .then(d => {console.log("todo ok")
        document.getElementById(tramiteAudienciaSelected).remove()
        eliminarModal.hide();
  
    })
    .catch(() => {console.log("Error en catch deleteTramiteAudiencia");

    })
}

function hideAll(){
    document.querySelectorAll(".taBlock").forEach(block => {
        block.style.display = "none"
    })
}

function search(){
    console.log("--- inside search ---");

    let numeroTA = document.getElementById("searchByNumeroTA").value
    let resolucionDefinitiva = document.getElementById("searchByResolucionDefinitivaSelect").value
    let ejercicioFiscal = document.getElementById("searchByEjercicioFiscal").value
    let consultora = document.getElementById("searchByConsultora").value
    let id = document.getElementById("searchByID").value
    let numeroExpediente = document.getElementById("searchByNumeroExpediente").value
    let acronimo = document.getElementById("searchByAcronimo").value

    let numeroIMV = document.getElementById("searchByNumeroIMV").value
    let llegadaNotificacion = document.getElementById("searchByLlegadaNotificacion").value
    let cliente = document.getElementById("searchByCliente").value
    let motivo = document.getElementById("searchByMotivoSelect").value
    let expertoTecnico = document.getElementById("searchByExpertoTecnico").value
    let experto4d = document.getElementById("searchByExperto4d").value
    let resolucion = document.getElementById("searchByResolucionSelect").value
    let codigoUnesco = document.getElementById("searchByCodigoUnesco").value

    let params = {
        "numeroTA": numeroTA,
        "resolucionDefinitiva": resolucionDefinitiva,
        "ejercicioFiscal": ejercicioFiscal,
        "consultora": consultora,
        "numeroExpediente": numeroExpediente,
        "id": id,
        "acronimo": acronimo,
        "numeroIMV": numeroIMV,
        "llegadaNotificacion": llegadaNotificacion,
        "cliente": cliente,
        "motivo": motivo,
        "expertoTecnico": expertoTecnico,
        "experto4d": experto4d,
        "resolucion": resolucion,
        "codigoUnesco": codigoUnesco
    }

    go(config.rootUrl + "/ta/searchTramiteAudiencia", 'POST', params)
    .then(results => {console.log("todo ok")
        history.pushState({}, "", "https://plataformacalidad.duckdns.org/ta/tramitesAudiencia");
        hideAll();

        let html = `
            <div class="taBlock">
        `
        for(let ta of results){
            var color = ``
            if(ta.resolucionDefinitiva === "Favorable parcial"){
                color = `<i class="bi bi-circle-fill pendientesCircle"></i>`
                ta.resolucionDefinitiva = "Fav. parcial"
            } else if (ta.resolucionDefinitiva === "Favorable") {
                color = `<i class="bi bi-circle-fill abiertasCircle"></i>`
            } else if (ta.resolucionDefinitiva === "Desfavorable") {
                color = `<i class="bi bi-circle-fill urgentesCircle"></i>`
            }
            

            html += `
                <div class="row taRow taDiv" id="${ta.id}">
                    <div class="col col-md-1 d-flex align-items-center justify-content-center">
                        <div class="d-flex align-items-center d-flex justify-content-center">
                            <h6 class="headerTitle">${ta.numeroTA}</h6>
                        </div>
                    </div>
                    <div class="col col-md-2 d-flex align-items-center d-flex justify-content-center">
                        <h6 class="headerTitle">${ta.numeroExpediente}</h6>
                    </div>
                    <div class="col col-md-2 d-flex align-items-center d-flex justify-content-center">
                        <div class="d-flex align-items-center d-flex justify-content-center">
                            <h6 class="headerTitle">${ta.acronimo}</h6>
                        </div>
                    </div>
                    <div class="col col-md-2 d-flex align-items-center d-flex">
                        <h6 class="headerTitle">${color}${ta.resolucionDefinitiva}</h6>
                    </div>
                    <div class="col col-md-1 d-flex align-items-center d-flex justify-content-center">
                        <h6 class="headerTitle">${ta.ejercicioFiscal}</h6>
                    </div>
                    <div class="col col-md-2 d-flex align-items-center d-flex justify-content-center">
                        <h6 class="headerTitle">${ta.consultora}</h6>
                    </div>
                    <div class="col col-md-2 d-flex align-items-center justify-content-center">
                        <a class="btn btn-primary taDivButton" href="/ta/manageTramiteAudiencia?taId=${ta.id}"><i class="bi bi-pencil-fill"></i></a>
                        <button class="btn btn-danger taDivButton" data-bs-toggle="modal" data-bs-target="#eliminarModal" value="${ta.id}" onclick="updateIdToDelete(event)"><i class="bi bi-trash"></i></button>
                    </div>
                </div>
            `
        }
        html += `</div>`

        if(results.length != 0){
            document.getElementById("noCoincidencesMsg").style.display = "none"
            document.getElementById("divToAppendBlocks").insertAdjacentHTML("afterend", html)
        } else {
            document.getElementById("noCoincidencesMsg").style.display = "block"
        }
        
    })
    .catch(() => {console.log("Error en catch searchTramiteAudiencia");

    })
}

showReversedByNumeroTA = true;
showReversedByEjercicioFiscal = false;

function sortCurrentList(e){
    console.log("--- inside sortCurrentList ---");

    let numeroTA = document.getElementById("searchByNumeroTA").value
    let resolucionDefinitiva = document.getElementById("searchByResolucionDefinitivaSelect").value
    let ejercicioFiscal = document.getElementById("searchByEjercicioFiscal").value
    let consultora = document.getElementById("searchByConsultora").value
    let id = document.getElementById("searchByID").value
    let numeroExpediente = document.getElementById("searchByNumeroExpediente").value
    let acronimo = document.getElementById("searchByAcronimo").value

    let numeroIMV = document.getElementById("searchByNumeroIMV").value
    let llegadaNotificacion = document.getElementById("searchByLlegadaNotificacion").value
    let cliente = document.getElementById("searchByCliente").value
    let motivo = document.getElementById("searchByMotivoSelect").value
    let expertoTecnico = document.getElementById("searchByExpertoTecnico").value
    let experto4d = document.getElementById("searchByExperto4d").value
    let resolucion = document.getElementById("searchByResolucionSelect").value
    let codigoUnesco = document.getElementById("searchByCodigoUnesco").value

    let params = {
        "numeroTA": numeroTA,
        "resolucionDefinitiva": resolucionDefinitiva,
        "ejercicioFiscal": ejercicioFiscal,
        "consultora": consultora,
        "numeroExpediente": numeroExpediente,
        "id": id,
        "acronimo": acronimo,
        "numeroIMV": numeroIMV,
        "llegadaNotificacion": llegadaNotificacion,
        "cliente": cliente,
        "motivo": motivo,
        "expertoTecnico": expertoTecnico,
        "experto4d": experto4d,
        "resolucion": resolucion,
        "codigoUnesco": codigoUnesco,
        "sort": "",
        "order": ""
    }

    if(e.currentTarget.id === "sortByNumeroTADiv"){
        showReversedByNumeroTA = !showReversedByNumeroTA;
        params.sort = "byNumeroTA";
        params.order = showReversedByNumeroTA;
    } else if (e.currentTarget.id === "sortByEjercicioFiscalDiv"){
        showReversedByEjercicioFiscal = !showReversedByEjercicioFiscal;
        params.sort = "byEjercicioFiscal";
        params.order = showReversedByEjercicioFiscal;
    }

    let url = new URL(window.location.href);
    let urlParams = new URLSearchParams(url.search);
    if(resolucionDefinitiva === "" && urlParams.has('resolucionDefinitiva')) {
        params.resolucionDefinitiva = urlParams.get("resolucionDefinitiva")
        if(urlParams.get("resolucionDefinitiva") === "FavorableParcial") params.resolucionDefinitiva = "Favorable parcial";
    }

    go(config.rootUrl + "/ta/sortCurrentTramiteAudienciaList", 'POST', params)
    .then(results => {console.log("todo ok")
        
        hideAll();
        
        let html = `
            <div class="taBlock">
        `
        for(let ta of results){
            var color = ``
            if(ta.resolucionDefinitiva === "Favorable parcial"){
                color = `<i class="bi bi-circle-fill pendientesCircle"></i>`
                ta.resolucionDefinitiva = "Fav. parcial"
            } else if (ta.resolucionDefinitiva === "Favorable") {
                color = `<i class="bi bi-circle-fill abiertasCircle"></i>`
            } else if (ta.resolucionDefinitiva === "Desfavorable") {
                color = `<i class="bi bi-circle-fill urgentesCircle"></i>`
            }
   
            html += `
                <div class="row taRow taDiv" id="${ta.id}">
                    <div class="col col-md-1 d-flex align-items-center justify-content-center">
                        <div class="d-flex align-items-center d-flex justify-content-center">
                            <h6 class="headerTitle">${ta.numeroTA}</h6>
                        </div>
                    </div>
                    <div class="col col-md-2 d-flex align-items-center d-flex justify-content-center">
                        <h6 class="headerTitle">${ta.numeroExpediente}</h6>
                    </div>
                    <div class="col col-md-2 d-flex align-items-center d-flex justify-content-center">
                        <div class="d-flex align-items-center d-flex justify-content-center">
                            <h6 class="headerTitle">${ta.acronimo}</h6>
                        </div>
                    </div>
                    <div class="col col-md-2 d-flex align-items-center d-flex">
                        <h6 class="headerTitle">${color}${ta.resolucionDefinitiva}</h6>
                    </div>
                    <div class="col col-md-1 d-flex align-items-center d-flex justify-content-center">
                        <h6 class="headerTitle">${ta.ejercicioFiscal}</h6>
                    </div>
                    <div class="col col-md-2 d-flex align-items-center d-flex justify-content-center">
                        <h6 class="headerTitle">${ta.consultora}</h6>
                    </div>
                    <div class="col col-md-2 d-flex align-items-center justify-content-center">
                        <a class="btn btn-primary taDivButton" href="/ta/manageTramiteAudiencia?taId=${ta.id}"><i class="bi bi-pencil-fill"></i></a>
                        <button class="btn btn-danger taDivButton" data-bs-toggle="modal" data-bs-target="#eliminarModal" value="${ta.id}" onclick="updateIdToDelete(event)"><i class="bi bi-trash"></i></button>
                    </div>
                </div>
            `
        }
        html += `</div>`

        document.getElementById("divToAppendBlocks").insertAdjacentHTML("afterend", html)
    })
    .catch(() => {console.log("Error en catch sortCurrentList");

    })
}

function clearInputs(){
    document.getElementById("searchByNumeroTA").value = ""
    document.getElementById("searchByResolucionDefinitivaSelect").value = ""
    document.getElementById("searchByEjercicioFiscal").value = ""
    document.getElementById("searchByConsultora").value = ""
    document.getElementById("searchByID").value = ""
    document.getElementById("searchByNumeroExpediente").value = ""
    document.getElementById("searchByAcronimo").value = ""

    document.getElementById("searchByNumeroIMV").value = ""
    document.getElementById("searchByLlegadaNotificacion").value = ""
    document.getElementById("searchByCliente").value = ""
    document.getElementById("searchByMotivoSelect").value = ""
    document.getElementById("searchByExpertoTecnico").value = ""
    document.getElementById("searchByExperto4d").value = ""
    document.getElementById("searchByResolucionSelect").value = ""
}