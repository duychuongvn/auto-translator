package vn.com.huy.translator.provider;

public interface TranslationProvider {
    String translate(String input, String inputLanguage, String outputLanguage);
}