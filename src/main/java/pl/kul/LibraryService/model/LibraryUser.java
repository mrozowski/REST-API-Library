package pl.kul.LibraryService.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class LibraryUser {
    private final UUID id;
    private final String username;
    private final String email;

    public LibraryUser(@JsonProperty("id") UUID id,
                       @JsonProperty("username") String username,
                       @JsonProperty("email") String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
