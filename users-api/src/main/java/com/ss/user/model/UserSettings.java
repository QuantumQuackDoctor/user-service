package com.ss.user.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.util.Objects;

/**
 * UserSettings
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-30T21:42:53.280827-06:00[America/Denver]")
public class UserSettings {
    @JsonProperty("notifications")
    private UserSettingsNotifications notifications;
    @JsonProperty("theme")
    private ThemeEnum theme;

    public UserSettings notifications(UserSettingsNotifications notifications) {
        this.notifications = notifications;
        return this;
    }

    /**
     * Get notifications
     *
     * @return notifications
     */
    @ApiModelProperty(value = "")

    @Valid

    public UserSettingsNotifications getNotifications() {
        return notifications;
    }

    public void setNotifications(UserSettingsNotifications notifications) {
        this.notifications = notifications;
    }

    public UserSettings theme(ThemeEnum theme) {
        this.theme = theme;
        return this;
    }

    /**
     * Get theme
     *
     * @return theme
     */
    @ApiModelProperty(value = "")


    public ThemeEnum getTheme() {
        return theme;
    }

    public void setTheme(ThemeEnum theme) {
        this.theme = theme;
    }

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
        StringBuilder sb = new StringBuilder();
        sb.append("class UserSettings {\n");

        sb.append("    notifications: ").append(toIndentedString(notifications)).append("\n");
        sb.append("    theme: ").append(toIndentedString(theme)).append("\n");
        sb.append("}");
        return sb.toString();
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

