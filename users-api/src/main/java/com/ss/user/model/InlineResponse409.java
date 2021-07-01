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
 * InlineResponse409
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-30T21:42:53.280827-06:00[America/Denver]")
public class InlineResponse409   {
  @JsonProperty("emailValid")
  private Boolean emailValid;

  @JsonProperty("usernameValid")
  private Boolean usernameValid;

  public InlineResponse409 emailValid(Boolean emailValid) {
    this.emailValid = emailValid;
    return this;
  }

  /**
   * Get emailValid
   * @return emailValid
  */
  @ApiModelProperty(value = "")


  public Boolean getEmailValid() {
    return emailValid;
  }

  public void setEmailValid(Boolean emailValid) {
    this.emailValid = emailValid;
  }

  public InlineResponse409 usernameValid(Boolean usernameValid) {
    this.usernameValid = usernameValid;
    return this;
  }

  /**
   * Get usernameValid
   * @return usernameValid
  */
  @ApiModelProperty(value = "")


  public Boolean getUsernameValid() {
    return usernameValid;
  }

  public void setUsernameValid(Boolean usernameValid) {
    this.usernameValid = usernameValid;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InlineResponse409 inlineResponse409 = (InlineResponse409) o;
    return Objects.equals(this.emailValid, inlineResponse409.emailValid) &&
        Objects.equals(this.usernameValid, inlineResponse409.usernameValid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(emailValid, usernameValid);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InlineResponse409 {\n");
    
    sb.append("    emailValid: ").append(toIndentedString(emailValid)).append("\n");
    sb.append("    usernameValid: ").append(toIndentedString(usernameValid)).append("\n");
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

