package com.ss.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

/**
 * User DTO
 */
@ApiModel(description = "User DTO")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("email")
    @ApiModelProperty(example = "email@example.com")
    @NotBlank
    @Email
    private String email;

    @JsonProperty("firstName")
    @NotBlank
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("DOB")
    @ApiModelProperty(example = "2021-02-10", value = "Date of birth")
    @NotBlank
    private String DOB;

    @ApiModelProperty(value = "To be used in account creation only, DELETE THIS WHEN SENDING!!!")
    @NotBlank(message = "password required")
    @Size(min = 5, max = 32, message = "password needs to be 5-32 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("isVeteran")
    @NotNull
    private Boolean isVeteran;

    @JsonProperty("points")
    private Integer points;

    private UserSettings settings;

    @JsonProperty ("orders")
    private List<Long> orders;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(this.id, user.id) &&
                Objects.equals(this.email, user.email) &&
                Objects.equals(this.firstName, user.firstName) &&
                Objects.equals(this.lastName, user.lastName) &&
                Objects.equals(this.DOB, user.DOB) &&
                Objects.equals(this.password, user.password) &&
                Objects.equals(this.isVeteran, user.isVeteran) &&
                Objects.equals(this.points, user.points) &&
                Objects.equals(this.settings, user.settings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, firstName, lastName, DOB, password, isVeteran, points, settings);
    }

    @Override
    public String toString() {

        return "class User {\n" +
                "    id: " + toIndentedString(id) + "\n" +
                "    email: " + toIndentedString(email) + "\n" +
                "    firstName: " + toIndentedString(firstName) + "\n" +
                "    lastName: " + toIndentedString(lastName) + "\n" +
                "    DOB: " + toIndentedString(DOB) + "\n" +
                "    password: " + toIndentedString(password) + "\n" +
                "    veteranStatus: " + toIndentedString(isVeteran) + "\n" +
                "    points: " + toIndentedString(points) + "\n" +
                "    settings: " + toIndentedString(settings) + "\n" +
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

