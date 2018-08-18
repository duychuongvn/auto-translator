
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
        "id",
        "jsonrpc",
        "result"
})
public class DeepLResult implements Serializable {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("jsonrpc")
    private String jsonrpc;
    @JsonProperty("result")
    private Result result;

}
