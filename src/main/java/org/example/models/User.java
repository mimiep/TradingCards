package org.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class User {
    private UUID id;

    @JsonProperty("Username") public String username;
    @JsonProperty("Password") public String password;
    @JsonProperty("Token") public String token;
    @JsonProperty("Coins") public Integer coins;
    @JsonProperty("Name") public String name;
    @JsonProperty("Bio") public String bio;
    @JsonProperty("Image") public String image;

    public User() {}

    // Konstruktor für die Benutzerklasse
    public User(UUID id, String username, String password, String token, Integer coins, String name, String bio, String image) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.token = token;
        this.coins = coins;
        this.name = name;
        this.bio = bio;
        this.image = image;
    }

    public UUID getUserId() {
        return id;
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

    public Integer getCoins() {
        return coins;
    }

    public void setCoins(Integer coins) {
        this.coins = coins;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
