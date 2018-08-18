
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
import lombok.Singular;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "source_lang",
        "source_lang_is_confident",
        "target_lang",
        "translations"
})
public class Result implements Serializable {

    @JsonProperty("source_lang")
    private String sourceLang;
    @JsonProperty("source_lang_is_confident")
    private Long sourceLangIsConfident;
    @JsonProperty("target_lang")
    private String targetLang;
    @JsonProperty("translations")
    @Singular
    private List<Translation> translations = new ArrayList<Translation>();

}
