package com.ss.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

/**
 * AuthResponse
 */
@Setter
@Getter
@AllArgsConstructor
public class AuthResponse {
    @JsonProperty("jwt")
    @NotBlank
    private String jwt;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthResponse authResponse = (AuthResponse) o;
        return Objects.equals(this.jwt, authResponse.jwt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jwt);
    }

    @Override
    public String toString() {

        return "class AuthResponse {\n" +
                "    jwt: " + toIndentedString(jwt) + "\n" +
                "}";
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

