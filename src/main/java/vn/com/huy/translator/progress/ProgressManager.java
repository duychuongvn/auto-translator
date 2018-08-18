package vn.com.huy.translator.progress;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ProgressManager {

    private Progress progress;
    private int printInterval = 1;

    public ProgressManager(int total) {
        this.progress = Progress.builder()
                .done(0)
                .total(total)
                .build();
    }

    public void step() {
        progress.step();
        if (progress.isComplete() || progress.getDone() % printInterval == 0) {
            print();
        }
    }

    private void print() {
        progressPercentage(progress.getDone(), progress.getTotal());
    }

    private void progressPercentage(int done, int total) {
        String doneChar = "=";
        String defaultChar = ".";
        int barSize = 50;

        int donePer = ((100 * done) / total);
        int barDoneSize = donePer * barSize / 100;

        List<String> barList = new ArrayList<>();
        IntStream.range(0, barSize + 1)
                .forEach(i -> barList.add(defaultChar));
        IntStream.range(0, barDoneSize + 1)
                .forEach(i -> barList.set(i, doneChar));

        System.out.print("\r" + "[" + String.join("", barList) + "]" + " " + donePer + "% " + "- " + done + "/" + total);
        if (done == total) {
            System.out.print("\n");
        }
    }
}
