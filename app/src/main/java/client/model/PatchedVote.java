/**
 * 
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: 0.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package client.model;

import java.net.URI;
import java.util.Date;
import java.util.UUID;
import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;

@ApiModel(description = "")
public class PatchedVote {
  
  @SerializedName("id")
  private UUID id = null;
  @SerializedName("user")
  private URI user = null;
  @SerializedName("photo")
  private URI photo = null;
  @SerializedName("timestamp")
  private Date timestamp = null;

  /**
   **/
  @ApiModelProperty(value = "")
  public UUID getId() {
    return id;
  }
  public void setId(UUID id) {
    this.id = id;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  public URI getUser() {
    return user;
  }
  public void setUser(URI user) {
    this.user = user;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  public URI getPhoto() {
    return photo;
  }
  public void setPhoto(URI photo) {
    this.photo = photo;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  public Date getTimestamp() {
    return timestamp;
  }
  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PatchedVote patchedVote = (PatchedVote) o;
    return (this.id == null ? patchedVote.id == null : this.id.equals(patchedVote.id)) &&
        (this.user == null ? patchedVote.user == null : this.user.equals(patchedVote.user)) &&
        (this.photo == null ? patchedVote.photo == null : this.photo.equals(patchedVote.photo)) &&
        (this.timestamp == null ? patchedVote.timestamp == null : this.timestamp.equals(patchedVote.timestamp));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.id == null ? 0: this.id.hashCode());
    result = 31 * result + (this.user == null ? 0: this.user.hashCode());
    result = 31 * result + (this.photo == null ? 0: this.photo.hashCode());
    result = 31 * result + (this.timestamp == null ? 0: this.timestamp.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class PatchedVote {\n");
    
    sb.append("  id: ").append(id).append("\n");
    sb.append("  user: ").append(user).append("\n");
    sb.append("  photo: ").append(photo).append("\n");
    sb.append("  timestamp: ").append(timestamp).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
