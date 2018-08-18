
package vn.com.huy.translator.provider.deepl.dto.receive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
        "beams",
        "timeAfterPreprocessing",
        "timeReceivedFromEndpoint",
        "timeSentToEndpoint",
        "total_time_endpoint"
})
public class Translation implements Serializable {

    @JsonProperty("beams")
    private List<Beam> beams = new ArrayList<Beam>();
    @JsonProperty("timeAfterPreprocessing")
    private Long timeAfterPreprocessing;
    @JsonProperty("timeReceivedFromEndpoint")
    private Long timeReceivedFromEndpoint;
    @JsonProperty("timeSentToEndpoint")
    private Long timeSentToEndpoint;
    @JsonProperty("total_time_endpoint")
    private Long totalTimeEndpoint;

}
