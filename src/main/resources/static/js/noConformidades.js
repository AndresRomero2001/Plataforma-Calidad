const eliminarModal = new bootstrap.Modal(document.querySelector('#eliminarModal'));
var showReversedByDate = true;
var showReversedById = true;

var noConformidadSelected;

$(document).ready(function(){
    $('#searchInput').focus(function(){
        $('#searchBar').addClass('focused');
        $('#searchIcon').addClass('focused');
    }).blur(function(){
        $('#searchBar').removeClass('focused');
        $('#searchIcon').removeClass('focused');
    });
});

window.onload = function() {

    // si no, las fechas se mostrarian YYYY-MM-DD
    let ncDivList = document.querySelectorAll(".ncDiv");
    ncDivList.forEach((div) => {
        let fecha = div.querySelector("#showFecha").innerHTML;
        const formattedDate = reverseDate(fecha);
        div.querySelector("#showFecha").innerHTML = formattedDate;
    });

    
}

function updateIdToDelete(e){
    noConformidadSelected = e.currentTarget.value
    console.log("ncSelected: " + noConformidadSelected);
}

function deleteNoConformidad(){
    let params = {
        "ncId": noConformidadSelected
    }

    go(config.rootUrl + "/deleteNoConformidad", 'POST', params)
    .then(d => {console.log("todo ok")
        document.getElementById(noConformidadSelected).remove()
        eliminarModal.hide();
  
    })
    .catch(() => {console.log("Error en catch deleteAccion");

    })
}

function hideAll(){
    document.querySelectorAll(".ncBlock").forEach(block => {
        block.style.display = "none"
    })
}

function search(){
    console.log("--- inside search ---");

    let asunto = document.getElementById("searchByAsunto").value
    let fecha = document.getElementById("searchByFecha").value
    let origen = document.getElementById("searchByOrigenSelect").value
    let estado = document.getElementById("searchByEstado").value
    let id = document.getElementById("searchByID").value

    let params = {
        "asunto": asunto,
        "fecha": fecha,
        "origen": origen,
        "estado": estado,
        "id": id
    }

    go(config.rootUrl + "/searchNoConformidad", 'POST', params)
    .then(results => {console.log("todo ok")
        /* window.location.href = "http://localhost:8080/noConformidades" */
        history.pushState({}, "", "https://plataformacalidad.es/noConformidades");
        hideAll();
        let html = `
            <div class="ncBlock">
        `
        for(let nc of results){
            let fecha = reverseDate(nc.fecha)

            let estado = nc.estado
            htmlEstado = ``;
            if(estado === "Abierta"){
                htmlEstado += `
                    <i class="bi bi-circle-fill abiertasCircle"></i>
                `
            } else if (estado === "Pendiente") {
                htmlEstado += `
                    <i class="bi bi-circle-fill pendientesCircle"></i>
                `
                estado = "Pend."
            } else if (estado === "Cerrada") {
                htmlEstado += `
                    <i class="bi bi-circle-fill cerradasCircle"></i>
                `
            } else if (estado === "Urgente") {
                htmlEstado += `
                    <i class="bi bi-circle-fill urgentesCircle"></i>
                `
            }

            html += `
            <div class="row ncDiv d-flex align-items-center" id="${nc.id}">
                <div class="col col-md-1 d-flex align-items-center">
                    ${htmlEstado}
                    <h6>${estado}</h6>
                </div>
                <div class="col col-md-1 d-flex align-items-center justify-content-center">
                    <h6>${nc.id}</h6>
                </div>
                <div class="col col-md-2 d-flex align-items-center justify-content-center">
                    <h6 id="showFecha">${fecha}</h6>
                </div>
                <div class="col col-md-2 d-flex align-items-center justify-content-center">
                    <h6>${nc.origen}</h6>
                </div>
                <div class="col col-md-4 d-flex align-items-center justify-content-center">
                    <h6>${nc.asunto}</h6>
                </div>
                <div class="col col-md-2 d-flex align-items-center justify-content-center">
                    <a class="btn btn-primary ncDivButton" href="/manageNoConformidad?ncId=${nc.id}"><i class="bi bi-pencil-fill"></i></a>
                    <button class="btn btn-danger ncDivButton" data-bs-toggle="modal" data-bs-target="#eliminarModal" value="${nc.id}" onclick="updateIdToDelete(event)"><i class="bi bi-trash"></i></button>
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
    .catch(() => {console.log("Error en catch searchNoConformidad");

    })
}

function sortCurrentList(e){
    console.log("--- inside sortCurrentList ---");
    let asunto = document.getElementById("searchByAsunto").value
    let fecha = document.getElementById("searchByFecha").value
    let origen = document.getElementById("searchByOrigenSelect").value
    let estado = document.getElementById("searchByEstado").value
    let id = document.getElementById("searchByID").value

    let params = {
        "asunto": asunto,
        "fecha": fecha,
        "origen": origen,
        "estado": estado,
        "id": id,
        "sort": "",
        "order": ""
    }

    if(e.currentTarget.id === "sortByIdDiv"){
        showReversedById = !showReversedById;
        params.sort = "byId";
        params.order = showReversedById;
    } else if (e.currentTarget.id === "sortByFechaDiv"){
        showReversedByDate = !showReversedByDate;
        params.sort = "byFecha";
        params.order = showReversedByDate;
    }

    let url = new URL(window.location.href);
    let urlParams = new URLSearchParams(url.search);
    if(estado === "" && urlParams.has('estado')) {
        params.estado = urlParams.get("estado")
    }

    go(config.rootUrl + "/sortCurrentNoConformidadList", 'POST', params)
    .then(results => {console.log("todo ok")
        
        hideAll();
        
        let html = `
            <div class="ncBlock">
        `
        for(let nc of results){
            let fecha = reverseDate(nc.fecha)

            let estado = nc.estado
            htmlEstado = ``;
            if(estado === "Abierta"){
                htmlEstado += `
                    <i class="bi bi-circle-fill abiertasCircle"></i>
                `
            } else if (estado === "Pendiente") {
                htmlEstado += `
                    <i class="bi bi-circle-fill pendientesCircle"></i>
                `
                estado = "Pend."
            } else if (estado === "Cerrada") {
                htmlEstado += `
                    <i class="bi bi-circle-fill cerradasCircle"></i>
                `
            } else if (estado === "Urgente") {
                htmlEstado += `
                    <i class="bi bi-circle-fill urgentesCircle"></i>
                `
            }

            html += `
            <div class="row ncDiv d-flex align-items-center" id="${nc.id}">
                <div class="col col-md-1 d-flex align-items-center">
                    ${htmlEstado}
                    <h6>${estado}</h6>
                </div>
                <div class="col col-md-1 d-flex align-items-center justify-content-center">
                    <h6>${nc.id}</h6>
                </div>
                <div class="col col-md-2 d-flex align-items-center justify-content-center">
                    <h6 id="showFecha">${fecha}</h6>
                </div>
                <div class="col col-md-2 d-flex align-items-center justify-content-center">
                    <h6>${nc.origen}</h6>
                </div>
                <div class="col col-md-4 d-flex align-items-center justify-content-center">
                    <h6>${nc.asunto}</h6>
                </div>
                <div class="col col-md-2 d-flex align-items-center justify-content-center">
                    <a class="btn btn-primary ncDivButton" href="/manageNoConformidad?ncId=${nc.id}"><i class="bi bi-pencil-fill"></i></a>
                    <button class="btn btn-danger ncDivButton" data-bs-toggle="modal" data-bs-target="#eliminarModal" value="${nc.id}" onclick="updateIdToDelete(event)"><i class="bi bi-trash"></i></button>
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
    document.getElementById("searchByAsunto").value = ""
    document.getElementById("searchByFecha").value = ""
    document.getElementById("searchByOrigenSelect").value = ""
    document.getElementById("searchByEstado").value = ""
    document.getElementById("searchByID").value = ""
}