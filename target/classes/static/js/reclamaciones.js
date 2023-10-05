

const eliminarModal = new bootstrap.Modal(document.querySelector('#eliminarModal'));
var divToDelete;

function updateDivToDelete(e) {
    divToDelete = e.target.closest(".rcDiv");
}

function deleteReclamacion() {
    let rcId = divToDelete.id;

    console.log("rcSelected: " + rcId);

    let params = {
        "rcId": rcId
    }

    go(config.rootUrl + "/rc/deleteReclamacion", 'POST', params)
    .then(d => {console.log("todo ok")
        divToDelete.remove()
        eliminarModal.hide();
  
    })
    .catch(() => {console.log("Error en catch deleteReclamacion");

    })
}

function hideAll(){
    document.querySelectorAll(".rcBlock").forEach(block => {
        block.style.display = "none"
    })
}

function search(){
    console.log("--- inside search ---");

    let estado = document.getElementById("searchByEstadoSelect").value
    let fechaRecepcion = document.getElementById("searchByFechaRecepcion").value
    let id = document.getElementById("searchByID").value
    let numeroExpediente = document.getElementById("searchByNumeroExpediente").value
    let acronimo = document.getElementById("searchByAcronimo").value
    let consultora = document.getElementById("searchByConsultora").value

    let params = {
        "estado": estado,
        "fechaRecepcion": fechaRecepcion,
        "numeroExpediente": numeroExpediente,
        "id": id,
        "acronimo": acronimo,
        "consultora": consultora
    }

    go(config.rootUrl + "/rc/searchReclamacion", 'POST', params)
    .then(results => {console.log("todo ok")
        history.pushState({}, "", "https://plataformacalidad.es/rc/reclamaciones");
        hideAll();

        let html = `
            <div class="rcBlock">
        `
        for(let rc of results){
            var color = ``
            if(rc.estado === "Abierta"){
                color = `<i class="bi bi-circle-fill abiertasCircle"></i>`
            } else if (rc.estado === "Cerrada") {
                color = `<i class="bi bi-circle-fill cerradasCircle"></i>`
            } 
            let fecha = reverseDate(rc.fechaRecepcion)

            html += `
                <div class="row rcRow rcDiv" id="${rc.id}">
                    <div class="col col-md-1 d-flex align-items-center justify-content-center">
                        <div class="d-flex align-items-center d-flex justify-content-center">
                            <h6 class="headerTitle">${rc.id}</h6>
                        </div>
                    </div>
                    <div class="col col-md-2 d-flex align-items-center justify-content-center">
                        <h6 class="headerTitle">${rc.numeroExpediente}</h6>
                    </div>
                    <div class="col col-md-2 d-flex align-items-center justify-content-center">
                        <div class="d-flex align-items-center d-flex justify-content-center">
                            <h6 class="headerTitle">${rc.acronimo}</h6>
                        </div>
                    </div>
                    <div class="col col-md-1 d-flex align-items-center">
                        ${color}
                        <h6 class="headerTitle">${rc.estado}</h6>
                    </div>
                    <div class="col col-md-2 d-flex align-items-center justify-content-center">
                        <h6 class="headerTitle">${rc.consultora}</h6>
                    </div>
                    <div class="col col-md-2 d-flex align-items-center justify-content-center">
                        <h6 class="headerTitle">${fecha}</h6>
                    </div>
                    <div class="col col-md-2 d-flex align-items-center justify-content-center">
                        <a class="btn btn-primary rcDivButton" href="/rc/manageReclamacion?rcId=${rc.id}"><i class="bi bi-pencil-fill"></i></a>
                        <button class="btn btn-danger rcDivButton" data-bs-toggle="modal" data-bs-target="#eliminarModal" value="${rc.id}" onclick="updateDivToDelete(event)"><i class="bi bi-trash"></i></button>
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
    .catch(() => {console.log("Error en catch searchReclamacion");

    })
}

showReversedById = true;
showReversedByFechaRecepcion = false;

function sortCurrentList(e){
    console.log("--- inside sortCurrentList ---");
    let estado = document.getElementById("searchByEstadoSelect").value
    let fechaRecepcion = document.getElementById("searchByFechaRecepcion").value
    let id = document.getElementById("searchByID").value
    let numeroExpediente = document.getElementById("searchByNumeroExpediente").value
    let acronimo = document.getElementById("searchByAcronimo").value
    let consultora = document.getElementById("searchByConsultora").value

    let params = {
        "estado": estado,
        "fechaRecepcion": fechaRecepcion,
        "numeroExpediente": numeroExpediente,
        "id": id,
        "acronimo": acronimo,
        "consultora": consultora,
        "sort": "",
        "order": ""
    }

    if(e.currentTarget.id === "sortByIdDiv"){
        showReversedById = !showReversedById;
        params.sort = "byId";
        params.order = showReversedById;
    } else if (e.currentTarget.id === "sortByFechaRecepcionDiv"){
        showReversedByFechaRecepcion = !showReversedByFechaRecepcion;
        params.sort = "byFechaRecepcion";
        params.order = showReversedByFechaRecepcion;
    }

    let url = new URL(window.location.href);
    let urlParams = new URLSearchParams(url.search);
    if(urlParams.has('estado')) {
        params.estado = urlParams.get("estado")
    }

    go(config.rootUrl + "/rc/sortCurrentReclamacionList", 'POST', params)
    .then(results => {console.log("todo ok")
        
        hideAll();
        
        let html = `
            <div class="rcBlock">
        `
        for(let rc of results){
            var color = ``
            if(rc.estado === "Abierta"){
                color = `<i class="bi bi-circle-fill abiertasCircle"></i>`
            } else if (rc.estado === "Cerrada") {
                color = `<i class="bi bi-circle-fill cerradasCircle"></i>`
            } 
            let fecha = reverseDate(rc.fechaRecepcion)

            html += `
                <div class="row rcRow rcDiv" id="${rc.id}">
                    <div class="col col-md-1 d-flex align-items-center justify-content-center">
                        <div class="d-flex align-items-center d-flex justify-content-center">
                            <h6 class="headerTitle">${rc.id}</h6>
                        </div>
                    </div>
                    <div class="col col-md-2 d-flex align-items-center justify-content-center">
                        <h6 class="headerTitle">${rc.numeroExpediente}</h6>
                    </div>
                    <div class="col col-md-2 d-flex align-items-center justify-content-center">
                        <div class="d-flex align-items-center d-flex justify-content-center">
                            <h6 class="headerTitle">${rc.acronimo}</h6>
                        </div>
                    </div>
                    <div class="col col-md-1 d-flex align-items-center">
                        ${color}
                        <h6 class="headerTitle">${rc.estado}</h6>
                    </div>
                    <div class="col col-md-2 d-flex align-items-center justify-content-center">
                        <h6 class="headerTitle">${rc.consultora}</h6>
                    </div>
                    <div class="col col-md-2 d-flex align-items-center justify-content-center">
                        <h6 class="headerTitle">${fecha}</h6>
                    </div>
                    <div class="col col-md-2 d-flex align-items-center justify-content-center">
                        <a class="btn btn-primary rcDivButton" href="/rc/manageReclamacion?rcId=${rc.id}"><i class="bi bi-pencil-fill"></i></a>
                        <button class="btn btn-danger rcDivButton" data-bs-toggle="modal" data-bs-target="#eliminarModal" value="${rc.id}" onclick="updateDivToDelete(event)"><i class="bi bi-trash"></i></button>
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

function clearInputs() {
    document.getElementById("searchByEstadoSelect").value = ""
    document.getElementById("searchByFechaRecepcion").value = ""
    document.getElementById("searchByID").value = ""
    document.getElementById("searchByNumeroExpediente").value = ""
    document.getElementById("searchByAcronimo").value = ""
    document.getElementById("searchByConsultora").value = ""
}