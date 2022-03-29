package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Image {

    @JsonProperty("data")
    private byte[] data;
    @JsonProperty("name")
    private String name;


    @JsonCreator
    public Image(@JsonProperty("data") final byte[] data, @JsonProperty("name") final String name) {
        this.data = data;
        this.name = name;
    }

    public byte[] getData() {
        return data;
    }

    public String getName() {
        return name;
    }
}
