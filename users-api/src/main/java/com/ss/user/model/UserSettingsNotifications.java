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
 * UserSettingsNotifications
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-30T21:42:53.280827-06:00[America/Denver]")
public class UserSettingsNotifications   {
  @JsonProperty("email")
  private Boolean email;

  @JsonProperty("text")
  private Boolean text;

  public UserSettingsNotifications email(Boolean email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   * @return email
  */
  @ApiModelProperty(value = "")


  public Boolean getEmail() {
    return email;
  }

  public void setEmail(Boolean email) {
    this.email = email;
  }

  public UserSettingsNotifications text(Boolean text) {
    this.text = text;
    return this;
  }

  /**
   * Get text
   * @return text
  */
  @ApiModelProperty(value = "")


  public Boolean getText() {
    return text;
  }

  public void setText(Boolean text) {
    this.text = text;
  }


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
    StringBuilder sb = new StringBuilder();
    sb.append("class UserSettingsNotifications {\n");
    
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
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

