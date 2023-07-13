package com.group2.server.model;

public class RegistrationDTO {
    private String username;
    private String password;
    
    public RegistrationDTO() {
        super();
    }
    
    public RegistrationDTO(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    //temporary
    public String toString(){
        return "Registeration info: username" + this.username + " password: " + this.password;
    }

    
}
