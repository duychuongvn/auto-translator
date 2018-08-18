
package vn.com.huy.translator.provider.deepl.dto.send;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "jobs",
        "lang",
        "priority"
})
public class Params implements Serializable {

    @Singular
    @JsonProperty("jobs")
    private List<Job> jobs = new ArrayList<Job>();
    @JsonProperty("lang")
    private Lang lang;
    @JsonProperty("priority")
    private Integer priority;

}
