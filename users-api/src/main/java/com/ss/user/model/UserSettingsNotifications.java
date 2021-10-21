package com.ss.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * UserSettingsNotifications
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingsNotifications {
    @JsonProperty("email")
    @NotNull
    private Boolean email;

    @JsonProperty("emailOrder")
    @NotNull
    private Boolean emailOrder;

    @JsonProperty("emailDelivery")
    @NotNull
    private Boolean emailDelivery;

    @JsonProperty("text")
    @NotNull
    private Boolean text;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserSettingsNotifications userSettingsNotifications = (UserSettingsNotifications) o;
        return Objects.equals(this.email, userSettingsNotifications.email) &&
                Objects.equals(this.text, userSettingsNotifications.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, text);
    }

    @Override
    public String toString() {

        return "class UserSettingsNotifications {\n" +
                "    email: " + toIndentedString(email) + "\n" +
                "    text: " + toIndentedString(text) + "\n" +
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

