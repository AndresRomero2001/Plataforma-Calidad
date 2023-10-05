var selectedUserId;
const modalDeleteUser = new bootstrap.Modal(document.querySelector('#eliminarUserModal'));
const modalModifyUser = new bootstrap.Modal(document.querySelector('#modificarUserModal'));
const modalCreateUser = new bootstrap.Modal(document.querySelector('#createUserModal'));

function updateSelectedUser(e){
    selectedUserId = e.target.closest(".userDiv").dataset.userId;
    console.log("new selected user id: " + selectedUserId);
}

function modifyUser(){
    let username = document.getElementById("username");
    let password = document.getElementById("password").value;
    let firstName = document.getElementById("firstName").value;
    let lastName = document.getElementById("lastName").value;
    const myForm = document.getElementById("modifyUserForm");

    let params = {
        "username": username.value,
        "password": password,
        "firstName": firstName,
        "lastName": lastName,
        "userId": selectedUserId
    }

    username.setCustomValidity("");

    if(!myForm.checkValidity())//comprueba si se cumplen las condiciones html (required, longitud maxima, formato, etc)
    {
        //si alguna condicion no se cumplia, llamamos a la funcion que muestra automaticamente un mensaje donde estuviera el primer error
        myForm.reportValidity();
    } else {
        go(config.rootUrl + "/admin/modifyUser", 'POST', params)
        .then(d => {console.log("todo ok")
            document.getElementById("username").value = d["username"];
            document.getElementById("firstName").value = d["firstName"];
            document.getElementById("lastName").value = d["lastName"];
            document.getElementById("password").value = "";
            let fullNameId = "fullName"+selectedUserId;
            let fullName = d["firstName"] + " " + d["lastName"]
            document.getElementById(fullNameId).innerText = fullName
            username.setCustomValidity("");
            modalModifyUser.hide()

            // para q solo se cambie el nombre de la navbar si el admin se esta modificando a si mismo
            if(document.getElementById("userId").value == selectedUserId){
                document.getElementById("sidebarFirstName").innerText = d["firstName"]
            }
        })
        .catch(() => {console.log("Error en catch modifyUser");
            username.setCustomValidity("El nombre de usuario ya existe");
            username.reportValidity();
        })
    }

    
}

function updateModalInfo(e){
    let params = {
        "userId": selectedUserId
    }

    go(config.rootUrl + "/admin/getUser", 'POST', params)
    .then(d => {console.log("todo ok")
        document.getElementById("username").value = d["username"];
        document.getElementById("firstName").value = d["firstName"];
        document.getElementById("lastName").value = d["lastName"];
        document.getElementById("password").value = "";
    })
    .catch(() => {console.log("Error en catch getUser");

    })
}

function deleteUser(){
    let params = {
        "userId": selectedUserId
    }

    go(config.rootUrl + "/admin/deleteUser", 'POST', params)
    .then(d => {console.log("todo ok")
        var userDiv = document.querySelector('div[data-user-id="'+selectedUserId+'"]');
        userDiv.remove()
        modalDeleteUser.hide()
    })
    .catch(() => {console.log("Error en catch deleteUser");

    })
}

function createUser(){
    let username = document.getElementById("newUsername");
    let password = document.getElementById("newPassword").value;
    let firstName = document.getElementById("newFirstName").value;
    let lastName = document.getElementById("newLastName").value;
    const myForm = document.getElementById("createUserForm");

    let params = {
        "username": username.value,
        "password": password,
        "firstName": firstName,
        "lastName": lastName
    }

    username.setCustomValidity("");

    if(!myForm.checkValidity())//comprueba si se cumplen las condiciones html (required, longitud maxima, formato, etc)
    {
        //si alguna condicion no se cumplia, llamamos a la funcion que muestra automaticamente un mensaje donde estuviera el primer error
        myForm.reportValidity();
    } else {
        go(config.rootUrl + "/admin/createUser", 'POST', params)
        .then(d => {console.log("todo ok")
            console.log("userid: " + d["userId"]);
            let usersListDiv = document.getElementById("usersList")
            let html = `
                <div class="row userDiv d-flex justify-content-center align-items-center" data-user-id="`+d["userId"]+`">
                    <div class="col-md-4 offset-md-2">
                        <h4 id="fullName`+d["userId"]+`">`+firstName+` `+lastName+`</h4>
                    </div>
                    <div class="col">
                        <button class="btn btn-secondary userButton" data-bs-toggle="modal" data-bs-target="#modificarUserModal" onclick="updateSelectedUser(event); updateModalInfo(event)"><i class="bi bi-pencil-fill"></i></button>
                        <button class="btn btn-danger userButton" data-bs-toggle="modal" data-bs-target="#eliminarUserModal" onclick="updateSelectedUser(event)"><i class="bi bi-trash"></i></button>
                    </div>
                </div>
            `
            usersListDiv.insertAdjacentHTML("afterend", html)
    
            document.getElementById("newUsername").value = "";
            document.getElementById("newPassword").value = "";
            document.getElementById("newFirstName").value = "";
            document.getElementById("newLastName").value  = "";
    
            username.setCustomValidity("");
            modalCreateUser.hide()
        })
        .catch(() => {console.log("Error en catch createUser");
            username.setCustomValidity("El nombre de usuario ya existe");
            username.reportValidity();
        })
    }

    
}