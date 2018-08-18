package vn.com.huy.translator.provider;

import java.net.InetSocketAddress;
import java.net.Proxy;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import lombok.Getter;

@Getter
public abstract class ExternalTranslationProvider implements TranslationProvider {

    @Value("${translator.provider.proxy.host:#{null}}")
    private String proxyHost;
    @Value("${translator.provider.proxy.port:80}")
    private int proxyPort;

    protected Proxy proxy = Proxy.NO_PROXY;

    @PostConstruct
    public void initialize() {
        if (StringUtils.isNotBlank(proxyHost)) {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        }
    }

}
