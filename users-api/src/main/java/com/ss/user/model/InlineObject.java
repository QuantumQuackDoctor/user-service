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
 * InlineObject
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-30T21:42:53.280827-06:00[America/Denver]")
public class InlineObject   {
  @JsonProperty("password")
  private String password;

  @JsonProperty("key")
  private String key;

  public InlineObject password(String password) {
    this.password = password;
    return this;
  }

  /**
   * new password
   * @return password
  */
  @ApiModelProperty(value = "new password")


  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public InlineObject key(String key) {
    this.key = key;
    return this;
  }

  /**
   * Session key from email, should be passed in with url parameter
   * @return key
  */
  @ApiModelProperty(value = "Session key from email, should be passed in with url parameter")


  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InlineObject inlineObject = (InlineObject) o;
    return Objects.equals(this.password, inlineObject.password) &&
        Objects.equals(this.key, inlineObject.key);
  }

  @Override
  public int hashCode() {
    return Objects.hash(password, key);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InlineObject {\n");
    
    sb.append("    password: ").append(toIndentedString(password)).append("\n");
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
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

