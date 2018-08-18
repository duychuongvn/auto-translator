package vn.com.huy.translator.encoder;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

@Component
public class SecretStringEncoder implements Encoder {

    private AtomicLong counter = new AtomicLong(0);

    @Override
    public String encode(String text) {
        return "XXXXXXX" + counter.incrementAndGet();
    }
}
