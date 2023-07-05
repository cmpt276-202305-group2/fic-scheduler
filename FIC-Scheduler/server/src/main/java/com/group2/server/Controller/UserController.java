package com.group2.server.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.group2.server.Model.User;
import com.group2.server.Repository.UserRepository;

@RestController
@CrossOrigin
public class UserController {

    @Autowired
    private UserRepository userRepository;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        User user = userRepository.findByUsername(username);
        System.out.println(user);

        if (user == null || !user.getPassword().equals(password)) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        } 
        // response status types : 
        // status 200 (OK), status 404 ( it coudnt connect to backend) , status 500 (response had a problem)

        // if(user != null){
        //     if(user.getRole().equals("ADMIN"))
        //     {
        //         return ResponseEntity.ok("/CoordinatorHomePage");
        //     }
        //     else if(user.getRole().equals("PROFESSOR")){
        //         return ResponseEntity.ok("/InstructorHomePage");asdasdasd
        //     }
        // }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("role", user.getRole());

        // reponse : header (status number 200). res.body (response{usernamne, role })
        return ResponseEntity.ok(response);
    }
}
