package vn.com.huy.translator.provider.deepl;

import static java.util.Comparator.comparing;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import vn.com.huy.translator.provider.ExternalTranslationProvider;
import vn.com.huy.translator.provider.deepl.dto.receive.Beam;
import vn.com.huy.translator.provider.deepl.dto.receive.DeepLResult;
import vn.com.huy.translator.provider.deepl.dto.receive.Translation;
import vn.com.huy.translator.provider.deepl.dto.send.DeepLRequest;
import vn.com.huy.translator.provider.deepl.dto.send.Job;
import vn.com.huy.translator.provider.deepl.dto.send.Lang;
import vn.com.huy.translator.provider.deepl.dto.send.Params;

@Profile("deepl")
@Component
public class DeepLTranslationProvider extends ExternalTranslationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DeepLTranslationProvider.class);
    private static final String BASE_URL = "https://www.deepl.com/jsonrpc";

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String translate(String input, String inputLanguage, String outputLanguage) {
        try {
            inputLanguage = inputLanguage.toUpperCase();
            outputLanguage = outputLanguage.toUpperCase();

            DeepLRequest request = DeepLRequest.builder()
                    .jsonrpc("2.0")
                    .method("LMT_handle_jobs")
                    .params(Params.builder()
                            .job(Job.builder()
                                    .kind("default")
                                    .rawEnSentence(input)
                                    .build())
                            .lang(Lang.builder()
                                    .userPreferredLangs(Arrays.asList(inputLanguage, outputLanguage))
                                    .sourceLangUserSelected(inputLanguage)
                                    .targetLang(outputLanguage)
                                    .build())
                            .priority(-1)
                            .build())
                    .id(18)
                    .build();

            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setProxy(proxy);
            RestTemplate restTemplate = new RestTemplate(requestFactory);
            String textResult = restTemplate.postForObject(BASE_URL, request, String.class);
            return lookupTranslation(input, objectMapper.readValue(textResult, DeepLResult.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String lookupTranslation(String input, DeepLResult deepLResult) {
        Optional<String> translation = deepLResult.getResult().getTranslations()
                .stream()
                .map(Translation::getBeams)
                .flatMap(Collection::stream)
                .sorted(comparing(Beam::getScore).reversed())
                .map(Beam::getPostprocessedSentence)
                .findFirst();
        if (!translation.isPresent()) {
            LOG.debug("Can't find translation from input=[{}], result from DeepL=[{}]", input, deepLResult);
            return input;
        }
        return translation.get();
    }
}
