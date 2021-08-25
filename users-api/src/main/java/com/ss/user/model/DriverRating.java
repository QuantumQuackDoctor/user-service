package com.ss.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(description = "driver rating")
@Getter
@Setter
@EqualsAndHashCode
public class DriverRating {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("reviewer")
    private String reviewer;

    @JsonProperty("stars")
    @NotNull
    private Integer stars;

    @JsonProperty("description")
    @NotBlank
    private String description;
}
