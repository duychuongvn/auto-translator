package vn.com.huy.translator.encoder;

//@Component
public class HashCodeEncoder implements Encoder {
    @Override
    public String encode(String text) {
        return String.valueOf(text.hashCode());
    }
}
