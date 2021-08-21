package com.ss.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

/**
 * InlineObject
 */
@Getter
@Setter
@AllArgsConstructor
public class PasswordResetRequest {
    @JsonProperty("password")
    @NotBlank
    private String password;

    @NotBlank
    @JsonProperty("key")
    private String key;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PasswordResetRequest passwordResetRequest = (PasswordResetRequest) o;
        return Objects.equals(this.password, passwordResetRequest.password) &&
                Objects.equals(this.key, passwordResetRequest.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(password, key);
    }

    @Override
    public String toString() {

        return "class InlineObject {\n" +
                "    password: " + toIndentedString(password) + "\n" +
                "    key: " + toIndentedString(key) + "\n" +
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

