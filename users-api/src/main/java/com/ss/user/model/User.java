package com.ss.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * User DTO
 */
@ApiModel(description = "User DTO")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-30T21:42:53.280827-06:00[America/Denver]")
public class User {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("DOB")
    private String DOB;

    @JsonProperty("password")
    private String password;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("veteranStatus")
    private Boolean veteranStatus;

    @JsonProperty("points")
    private Integer points;

    private UserSettings settings;

    public User id(Long id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     *
     * @return id
     */
    @ApiModelProperty(value = "")


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User email(String email) {
        this.email = email;
        return this;
    }

    /**
     * Get email
     *
     * @return email
     */
    @ApiModelProperty(example = "email@example.com", value = "")

    @Pattern(regexp = "/^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$/gm")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    /**
     * Get firstName
     *
     * @return firstName
     */
    @ApiModelProperty(value = "")


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public User lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    /**
     * Get lastName
     *
     * @return lastName
     */
    @ApiModelProperty(value = "")


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public User DOB(String DOB) {
        this.DOB = DOB;
        return this;
    }

    /**
     * Date of birth, test regex
     *
     * @return DOB
     */
    @ApiModelProperty(example = "2021-02-10", value = "Date of birth, test regex")

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public User password(String password) {
        this.password = password;
        return this;
    }

    /**
     * To be used in account creation only, DELETE THIS WHEN SENDING!!!
     *
     * @return password
     */
    @ApiModelProperty(value = "To be used in account creation only, DELETE THIS WHEN SENDING!!!")


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User veteranStatus(Boolean veteranStatus) {
        this.veteranStatus = veteranStatus;
        return this;
    }

    /**
     * Get veteranStatus
     *
     * @return veteranStatus
     */
    @ApiModelProperty(value = "")


    public Boolean getVeteranStatus() {
        return veteranStatus;
    }

    public void setVeteranStatus(Boolean veteranStatus) {
        this.veteranStatus = veteranStatus;
    }

    public User points(Integer points) {
        this.points = points;
        return this;
    }

    /**
     * Get points
     *
     * @return points
     */
    @ApiModelProperty(value = "")


    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public User settings(UserSettings settings) {
        this.settings = settings;
        return this;
    }

    /**
     * Get settings
     *
     * @return settings
     */
    @ApiModelProperty(value = "")

    @Valid

    public UserSettings getSettings() {
        return settings;
    }

    public void setSettings(UserSettings settings) {
        this.settings = settings;
    }


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
                Objects.equals(this.veteranStatus, user.veteranStatus) &&
                Objects.equals(this.points, user.points) &&
                Objects.equals(this.settings, user.settings);
    }

    public List<String> getNullFields() {
        Field fields[] = this.getClass().getDeclaredFields();
        List<String> nullFields = new ArrayList<>();
        for (Field field : fields) {
            try {
                Object value = field.get(this);
                if (value == null) {
                    nullFields.add(field.getName());
                }
            } catch (IllegalAccessException | IllegalArgumentException e) {
                nullFields.add(field.getName());
            }
        }
        return nullFields;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, firstName, lastName, DOB, password, veteranStatus, points, settings);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class User {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    email: ").append(toIndentedString(email)).append("\n");
        sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
        sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
        sb.append("    DOB: ").append(toIndentedString(DOB)).append("\n");
        sb.append("    password: ").append(toIndentedString(password)).append("\n");
        sb.append("    veteranStatus: ").append(toIndentedString(veteranStatus)).append("\n");
        sb.append("    points: ").append(toIndentedString(points)).append("\n");
        sb.append("    settings: ").append(toIndentedString(settings)).append("\n");
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
}

