
package vn.com.huy.translator.provider.deepl.dto.receive;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "num_symbols",
        "postprocessed_sentence",
        "score",
        "totalLogProb"
})
public class Beam implements Serializable {

    @JsonProperty("num_symbols")
    private Integer numSymbols;
    @JsonProperty("postprocessed_sentence")
    private String postprocessedSentence;
    @JsonProperty("score")
    private Float score;
    @JsonProperty("totalLogProb")
    private Float totalLogProb;

}
