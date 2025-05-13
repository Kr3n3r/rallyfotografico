package client.model;

import com.google.gson.annotations.SerializedName;

/**
 * Enum for Photo status values.
 * "Pending" - Pending
 * "Approved" - Approved
 * "Rejected" - Rejected
 */
public enum StatusEnum {

  @SerializedName("Pending")
  Pending,

  @SerializedName("Approved")
  Approved,

  @SerializedName("Rejected")
  Rejected
}
