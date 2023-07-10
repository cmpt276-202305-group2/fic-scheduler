// package com.group2.server.Config;


// import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;

// import com.group2.server.Model.ApplicationUser;
// import com.group2.server.Repository.UserRepository;



// @Component
// public class DatabaseSeeder implements CommandLineRunner {
    
//     private UserRepository userRepository;


//     public DatabaseSeeder(UserRepository userRepository) {
//         this.userRepository = userRepository;
//     }

//     @Override
//     public void run(String... args) throws Exception {
//         // Seed users into database
//         ApplicationUser admin = new ApplicationUser("coordinator", "coordinator", "ADMIN");
//         ApplicationUser professor = new ApplicationUser("instructor", "instructor", "PROFESSOR");

//         userRepository.save(admin);
//         userRepository.save(professor);
//     }
// }
