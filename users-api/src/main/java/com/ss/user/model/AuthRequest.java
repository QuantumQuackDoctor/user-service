package com.ss.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Authentication request
 */
@ApiModel(description = "Authentication request")
@Getter
@Setter
@NoArgsConstructor
public class AuthRequest {
    @JsonProperty("email")
    @NotBlank
    private String email;

    @JsonProperty("password")
    @NotBlank
    private String password;

    @JsonProperty("isDriver")
    @NotNull
    private Boolean isDriver;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthRequest authRequest = (AuthRequest) o;
        return Objects.equals(this.email, authRequest.email) &&
                Objects.equals(this.password, authRequest.password) &&
                Objects.equals(this.isDriver, authRequest.isDriver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password, isDriver);
    }

    @Override
    public String toString() {

        return "class AuthRequest {\n" +
                "    email: " + toIndentedString(email) + "\n" +
                "    password: " + toIndentedString(password) + "\n" +
                "    isDriver: " + toIndentedString(isDriver) + "\n" +
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

