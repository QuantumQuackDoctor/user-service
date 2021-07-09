package com.ss.user.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

/**
 * UserSettings
 */
@Getter
@Setter
@NoArgsConstructor
public class UserSettings {
    @JsonProperty("notifications")
    @NotBlank
    private UserSettingsNotifications notifications;
    @JsonProperty("theme")
    @NotBlank
    private ThemeEnum theme;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserSettings userSettings = (UserSettings) o;
        return Objects.equals(this.notifications, userSettings.notifications) &&
                Objects.equals(this.theme, userSettings.theme);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notifications, theme);
    }

    @Override
    public String toString() {

        return "class UserSettings {\n" +
                "    notifications: " + toIndentedString(notifications) + "\n" +
                "    theme: " + toIndentedString(theme) + "\n" +
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

    /**
     * Gets or Sets theme
     */
    public enum ThemeEnum {
        LIGHT("light"),

        DARK("dark");

        private String value;

        ThemeEnum(String value) {
            this.value = value;
        }

        @JsonCreator
        public static ThemeEnum fromValue(String value) {
            for (ThemeEnum b : ThemeEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
}

