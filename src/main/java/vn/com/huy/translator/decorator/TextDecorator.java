package vn.com.huy.translator.decorator;

import org.apache.poi.xwpf.usermodel.XWPFRun;

public interface TextDecorator {
    XWPFRun decorate(XWPFRun input, String text);
}