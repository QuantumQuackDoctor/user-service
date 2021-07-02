package com.ss.user.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Authentication request
 */
@ApiModel(description = "Authentication request")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-07-01T14:52:34.175022700-06:00[America/Denver]")
public class AuthRequest   {
  @JsonProperty("email")
  private String email;

  @JsonProperty("password")
  private String password;

  @JsonProperty("isDriver")
  private Boolean isDriver;

  public AuthRequest email(String email) {
    this.email = email;
    return this;
  }

  public boolean validate(){
    return email != null && password != null && isDriver != null;
  }

  /**
   * Get email
   * @return email
  */
  @ApiModelProperty(value = "")


  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public AuthRequest password(String password) {
    this.password = password;
    return this;
  }

  /**
   * Get password
   * @return password
  */
  @ApiModelProperty(value = "")


  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public AuthRequest isDriver(Boolean isDriver) {
    this.isDriver = isDriver;
    return this;
  }

  /**
   * Get isDriver
   * @return isDriver
  */
  @ApiModelProperty(value = "")


  public Boolean getIsDriver() {
    return isDriver;
  }

  public void setIsDriver(Boolean isDriver) {
    this.isDriver = isDriver;
  }


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
    StringBuilder sb = new StringBuilder();
    sb.append("class AuthRequest {\n");
    
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    password: ").append(toIndentedString(password)).append("\n");
    sb.append("    isDriver: ").append(toIndentedString(isDriver)).append("\n");
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

