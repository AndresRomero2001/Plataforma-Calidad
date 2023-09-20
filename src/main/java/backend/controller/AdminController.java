package backend.controller;

import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;

import backend.model.User;
import backend.model.User.Role;
import backend.model.dataBase.DB;

/**
 *  Site administration.
 *
 *  Access to this end-point is authenticated - see SecurityConfig
 */
@Controller
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private EntityManager em;

    @Autowired
	private PasswordEncoder passwordEncoder;

    private DB db = new DB();

	private static final Logger log = LogManager.getLogger(AdminController.class);

	@GetMapping("/")
    public String index(Model model) {
        return "admin";
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        List<User> lu = db.getUsersList(em);
        model.addAttribute("usersList", lu);
        return "usuarios";
    }

    @PostMapping(path = "/modifyUser", produces = "application/json")
    @ResponseBody
    @Transactional
    public User modifyUser(HttpSession session, @RequestBody JsonNode o){
        log.info("@@@@ inside modifyUser");
        
        String username = o.get("username").asText();
        String plainPassword = o.get("password").asText();
        String encodedPassword = passwordEncoder.encode(plainPassword);
        String firstName = o.get("firstName").asText();
        String lastName = o.get("lastName").asText();
        Long userId = o.get("userId").asLong();

        User userBeforeChanges = db.getUser(em, userId);
        String password = encodedPassword;
        if(plainPassword.equals("")){
            password = userBeforeChanges.getPassword();
        }

        if(!userBeforeChanges.getUsername().equals(username)){
            if(db.existsUserByUsername(em, username)) return null;
        }
        

        User u = db.modifyUser(em, username, password, firstName, lastName, userId);

        // para que se actualice la propia info del admin en la sesion, a los usuarios normales no les afecta pq 
        // no pueden acceder a la pagina de modificar
        if(userBeforeChanges.hasRole(Role.ADMIN)){
            session.setAttribute("u", u);
        }
        
        
        return u;
    }

    @PostMapping(path = "/getUser", produces = "application/json")
    @ResponseBody
    @Transactional
    public User getUser(@RequestBody JsonNode o){
        log.info("@@@@ inside getUser");
        
        Long userId = o.get("userId").asLong();

        User u = db.getUser(em, userId);
        
        return u;
    }

    @PostMapping(path = "/deleteUser", produces = "application/json")
    @ResponseBody
    @Transactional
    public String deleteUser(@RequestBody JsonNode o){
        log.info("@@@@ inside deleteUser");
        
        Long userId = o.get("userId").asLong();

        db.deleteUser(em, userId);

        return "{\"isok\": \"isok\"}";
    }

    @PostMapping(path = "/createUser", produces = "application/json")
    @ResponseBody
    @Transactional
    public String createUser(@RequestBody JsonNode o){
        log.info("@@@@ inside createUser");
        
        String username = o.get("username").asText();
        String plainPassword = o.get("password").asText();
        String encodedPassword = passwordEncoder.encode(plainPassword);
        String firstName = o.get("firstName").asText();
        String lastName = o.get("lastName").asText();

        if(db.existsUserByUsername(em, username)){
            return null;
        }

        long userId = db.createUser(em, username, encodedPassword, firstName, lastName);

        return "{\"userId\": "+userId+"}";
    }
}
