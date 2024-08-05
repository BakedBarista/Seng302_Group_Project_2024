package nz.ac.canterbury.seng302.gardenersgrove.entity.dto;

import org.springframework.web.util.HtmlUtils;

public class PlantInfoDTO {
    private String label;
    private String description;
    private String id;
    private String image;

    public PlantInfoDTO() {
    }

    public PlantInfoDTO(String label, String description, String id, String image) {
        this.label = label;
        this.description = description;
        this.id = id;
        this.image = image;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    /**
     * Gets a formatted HTML string, suitable for autocomplete suggestions.
     * @return An HTML string.
     */
    public String getFormatted() {
        StringBuilder sb = new StringBuilder();
        sb.append("<strong>");
        sb.append(HtmlUtils.htmlEscape(label));
        sb.append("</strong> &ndash; <em>");
        sb.append(HtmlUtils.htmlEscape(description));
        sb.append("</em>");
        return sb.toString();
    }
}
