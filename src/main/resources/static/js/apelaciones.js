

const eliminarModal = new bootstrap.Modal(document.querySelector('#eliminarModal'));
var apelacionSelected;

function updateIdToDelete(e){
    apelacionSelected = e.currentTarget.value
    console.log("apSelected: " + apelacionSelected);
}

function deleteApelacion(){
    let params = {
        "apId": apelacionSelected
    }

    go(config.rootUrl + "/ap/deleteApelacion", 'POST', params)
    .then(d => {console.log("todo ok")
        document.getElementById(apelacionSelected).remove()
        eliminarModal.hide();
  
    })
    .catch(() => {console.log("Error en catch deleteApelacion");

    })
}

function hideAll(){
    document.querySelectorAll(".apBlock").forEach(block => {
        block.style.display = "none"
    })
}

function search(){
    console.log("--- inside search ---");

    let resolucion = document.getElementById("searchByResolucionSelect").value
    let fechaRecepcion = document.getElementById("searchByFechaRecepcion").value
    let id = document.getElementById("searchByID").value
    let numeroExpediente = document.getElementById("searchByNumeroExpediente").value
    let acronimo = document.getElementById("searchByAcronimo").value

    let params = {
        "resolucion": resolucion,
        "fechaRecepcion": fechaRecepcion,
        "numeroExpediente": numeroExpediente,
        "id": id,
        "acronimo": acronimo
    }

    go(config.rootUrl + "/ap/searchApelacion", 'POST', params)
    .then(results => {console.log("todo ok")
        history.pushState({}, "", "https://plataformacalidad.duckdns.org/ap/apelaciones");
        hideAll();

        let html = `
            <div class="apBlock">
        `
        for(let ap of results){
            var color = ``
            if(ap.resolucion === "Estimada"){
                color = `<i class="bi bi-circle-fill abiertasCircle"></i>`
            } else if (ap.resolucion === "Desestimada") {
                color = `<i class="bi bi-circle-fill urgentesCircle"></i>`
            } 
            let fecha = reverseDate(ap.fechaRegistroRecepcion)

            html += `
                <div class="row apRow apDiv" id="${ap.id}">
                    <div class="col col-md-1 d-flex align-items-center justify-content-center">
                        <div class="d-flex align-items-center d-flex justify-content-center">
                            <h6 class="headerTitle">${ap.id}</h6>
                        </div>
                    </div>
                    <div class="col col-md-2 d-flex align-items-center d-flex justify-content-center">
                        <h6 class="headerTitle">${ap.numeroExpediente}</h6>
                    </div>
                    <div class="col col-md-2 d-flex align-items-center d-flex justify-content-center">
                        <div class="d-flex align-items-center d-flex justify-content-center">
                            <h6 class="headerTitle">${ap.acronimo}</h6>
                        </div>
                    </div>
                    <div class="col col-md-2 d-flex align-items-center d-flex">
                        ${color}
                        <h6 class="headerTitle">${ap.resolucion}</h6>
                    </div>
                    <div class="col col-md-3 d-flex align-items-center d-flex justify-content-center">
                        <h6 class="headerTitle">${fecha}</h6>
                    </div>
                    <div class="col col-md-2 d-flex align-items-center justify-content-center">
                        <a class="btn btn-primary apDivButton" href="/ap/manageApelacion?apId=${ap.id}"><i class="bi bi-pencil-fill"></i></a>
                        <button class="btn btn-danger apDivButton" data-bs-toggle="modal" data-bs-target="#eliminarModal" value="${ap.id}" onclick="updateIdToDelete(event)"><i class="bi bi-trash"></i></button>
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
    .catch(() => {console.log("Error en catch searchApelacion");

    })
}

showReversedById = true;
showReversedByFechaRecepcion = false;

function sortCurrentList(e){
    console.log("--- inside sortCurrentList ---");
    let resolucion = document.getElementById("searchByResolucionSelect").value
    let fechaRecepcion = document.getElementById("searchByFechaRecepcion").value
    let id = document.getElementById("searchByID").value
    let numeroExpediente = document.getElementById("searchByNumeroExpediente").value
    let acronimo = document.getElementById("searchByAcronimo").value

    let params = {
        "resolucion": resolucion,
        "fechaRecepcion": fechaRecepcion,
        "numeroExpediente": numeroExpediente,
        "id": id,
        "acronimo": acronimo,
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
    if(urlParams.has('resolucion')) {
        params.resolucion = urlParams.get("resolucion")
    }

    go(config.rootUrl + "/ap/sortCurrentApelacionList", 'POST', params)
    .then(results => {console.log("todo ok")
        
        hideAll();
        
        let html = `
            <div class="apBlock">
        `
        for(let ap of results){
            var color = ``
            if(ap.resolucion === "Estimada"){
                color = `<i class="bi bi-circle-fill abiertasCircle"></i>`
            } else if (ap.resolucion === "Desestimada") {
                color = `<i class="bi bi-circle-fill urgentesCircle"></i>`
            } 
            let fecha = reverseDate(ap.fechaRegistroRecepcion)
   
            html += `
            <div class="row apRow apDiv" id="${ap.id}">
                <div class="col col-md-1 d-flex align-items-center justify-content-center">
                    <div class="d-flex align-items-center d-flex justify-content-center">
                        <h6 class="headerTitle">${ap.id}</h6>
                    </div>
                </div>
                <div class="col col-md-2 d-flex align-items-center d-flex justify-content-center">
                    <h6 class="headerTitle">${ap.numeroExpediente}</h6>
                </div>
                <div class="col col-md-2 d-flex align-items-center d-flex justify-content-center">
                    <div class="d-flex align-items-center d-flex justify-content-center">
                        <h6 class="headerTitle">${ap.acronimo}</h6>
                    </div>
                </div>
                <div class="col col-md-2 d-flex align-items-center d-flex">
                    ${color}
                    <h6 class="headerTitle">${ap.resolucion}</h6>
                </div>
                <div class="col col-md-3 d-flex align-items-center d-flex justify-content-center">
                    <h6 class="headerTitle">${fecha}</h6>
                </div>
                <div class="col col-md-2 d-flex align-items-center justify-content-center">
                    <a class="btn btn-primary apDivButton" href="/ap/manageApelacion?apId=${ap.id}"><i class="bi bi-pencil-fill"></i></a>
                    <button class="btn btn-danger apDivButton" data-bs-toggle="modal" data-bs-target="#eliminarModal" value="${ap.id}" onclick="updateIdToDelete(event)"><i class="bi bi-trash"></i></button>
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