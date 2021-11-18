package com.ss.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ss.user.validators.ValidPassword;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * User DTO
 */
@ApiModel(description = "Driver DTO")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Driver {
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

    @JsonProperty("car")
    @ApiModelProperty(example = "make model")
    @NotBlank
    private String car;

    @JsonProperty("password")
    @ApiModelProperty(value = "To be used in account creation only, DELETE THIS WHEN SENDING!!!")
    @NotBlank(message = "password required")
    @ValidPassword
    private String password;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("settings")
    @NotNull
    private UserSettings settings;

    @JsonProperty("ratings")
    private List<DriverRating> ratings;

}

