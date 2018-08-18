package vn.com.huy.translator.progress;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Progress {

    private int total;
    private int done;

    public void step() {
        done++;
    }

    public void step(long n) {
        done += n;
    }

    public boolean isComplete() {
        return done >= total;
    }


}
