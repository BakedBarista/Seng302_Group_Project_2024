package nz.ac.canterbury.seng302.gardenersgrove.model.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Condition component of the weather JSON response
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Condition {
    @JsonDeserialize
    @JsonProperty("text")
    private String conditions;

    @JsonDeserialize
    @JsonProperty("icon")
    private String iconUrl;

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
