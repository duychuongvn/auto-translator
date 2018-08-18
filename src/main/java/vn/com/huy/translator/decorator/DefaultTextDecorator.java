package vn.com.huy.translator.decorator;

import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
public class DefaultTextDecorator implements TextDecorator {

    @Override
    public XWPFRun decorate(XWPFRun input, String text) {
        input.setText("(" + text + ")");
        input.setColor("0000FF");
        input.setBold(false);
        input.setFontFamily("Calibri");
        input.setFontSize(10);
        return input;
    }

}
