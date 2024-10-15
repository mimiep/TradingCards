package org.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class User {
    private UUID id;

    @JsonProperty("Username") public String username;
    @JsonProperty("Password") public String password;
    @JsonProperty("Token") public String token;


    public User() {}

    // Konstruktor für die Benutzerklasse
    public User(UUID id, String username, String password, String token) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.token = token;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
