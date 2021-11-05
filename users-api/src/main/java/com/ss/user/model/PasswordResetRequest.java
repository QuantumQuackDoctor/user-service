package com.ss.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.UUID;

/**
 * InlineObject
 */
@ApiModel
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetRequest {
    @JsonProperty("newPassword")
    @NotBlank
    @Size(min = 8, max = 32, message = "password needs to be 8-32 characters")
    private String newPassword;

    @NotNull
    @JsonProperty("token")
    private UUID token;

}

