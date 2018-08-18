
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
        "jsonrpc",
        "method",
        "params",
        "id"
})
public class DeepLRequest implements Serializable {
    @JsonProperty("jsonrpc")
    private String jsonrpc;
    @JsonProperty("method")
    private String method;
    @JsonProperty("params")
    private Params params;
    @JsonProperty("id")
    private Integer id;
}
