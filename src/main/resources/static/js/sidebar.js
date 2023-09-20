document.addEventListener("DOMContentLoaded", () => {
    let url = window.location.href.toString();

    let urlParts = url.split("/")
    let page = urlParts[urlParts.length-1]

    switch (page){
        case "":
            selectSidebarPage("inicio")
            break;
        case "noConformidades":
            selectSidebarPage("noConformidades")
            break;
        case "tramitesAudiencia":
            selectSidebarPage("tramitesAudiencia")
            break;
        case "apelaciones":
            selectSidebarPage("apelaciones")
            break;
        case "reclamaciones":
            selectSidebarPage("reclamaciones")
            break;
        case "usuarios":
            selectSidebarPage("usuarios")
            break;
        case "showNoConformidad":
            selectSidebarPage("noConformidades")
            break;
        case "addNoConformidad":
            selectSidebarPage("noConformidades")
            break;
        case "addTramiteAudiencia":
            selectSidebarPage("tramitesAudiencia")
            break;
        case "addApelacion":
            selectSidebarPage("apelaciones")
            break;ç
        case "addReclamacion":
            selectSidebarPage("reclamaciones")
            break;
        case "manageReclamacion":
            selectSidebarPage("reclamaciones")
            break;
    }

    switch(true){
        case (page.startsWith("manageNoConformidad")):
            selectSidebarPage("noConformidades")
            break;
        case (page.startsWith("noConformidades")):
            selectSidebarPage("noConformidades")
            break;
        case (page.startsWith("manageTramiteAudiencia")):
            selectSidebarPage("tramitesAudiencia")
            break;
        case (page.startsWith("addTramiteAudiencia")): // pq con el boton the href="#addTAButton" la url cambia
            selectSidebarPage("tramitesAudiencia")
            break;
        case (page.startsWith("tramitesAudiencia")):
            selectSidebarPage("tramitesAudiencia")
            break;
        case (page.startsWith("manageApelacion")):
            selectSidebarPage("apelaciones")
            break;
        case (page.startsWith("apelaciones")):
            selectSidebarPage("apelaciones")
            break;
        case (page.startsWith("manageReclamacion")):
            selectSidebarPage("reclamaciones")
            break;
        case (page.startsWith("reclamaciones")):
            selectSidebarPage("reclamaciones")
            break;
            
/*         case (page.startsWith("showNoConformidad")):
            selectSidebarPage("noConformidades")
            break; */
    }
});

function selectSidebarPage(navLinkId){
    Array.from(document.getElementsByClassName("nav-link")).forEach(e => {
        e.classList.remove("active")
    })

    let actualNavLink = document.getElementById(navLinkId)
    actualNavLink.classList.add("active")
}
