package MCTG.core.models.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Profile {
    private String username;
    @JsonProperty("Name")
    private String pName;
    @JsonProperty("Bio")
    private String bio;
    @JsonProperty("Image")
    private String image;

    public Profile () {
        this.username = "";
        this.pName = "";
        this.bio = "";
        this.image = "";
    }

    public Profile (String username) {
        this.username = username;
        this.pName = "";
        this.bio = "";
        this.image = "";
    }

    public Profile (String pName, String bio, String image) {
        this.pName = pName;
        this.bio = bio;
        this.image = image;
    }

    @Override
    public String toString() {
        return "Profile\n----------------------\nUsername: " + this.username + "\nName: " + this.pName + "\nBio: " + this.bio + "\nImage: " + this.image;
    }
}
