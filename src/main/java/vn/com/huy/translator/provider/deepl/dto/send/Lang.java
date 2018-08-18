
package vn.com.huy.translator.provider.deepl.dto.send;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "user_preferred_langs",
        "source_lang_user_selected",
        "target_lang"
})
public class Lang implements Serializable {

    @JsonProperty("user_preferred_langs")
    private List<String> userPreferredLangs = new ArrayList<String>();
    @JsonProperty("source_lang_user_selected")
    private String sourceLangUserSelected;
    @JsonProperty("target_lang")
    private String targetLang;

}
