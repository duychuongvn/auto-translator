
package vn.com.huy.translator.provider.deepl.dto.send;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "kind",
        "raw_en_sentence"
})
public class Job implements Serializable {
    @JsonProperty("kind")
    private String kind;
    @JsonProperty("raw_en_sentence")
    private String rawEnSentence;
}
