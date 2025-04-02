import javax.sound.sampled.SourceDataLine;

public class Member implements Runnable {
    private final BellNote note;
    private final SourceDataLine line;

    public Member(BellNote note, SourceDataLine line) {
        this.note = note;
        this.line = line;
    }

    public void run() {
        playNote();
    }

    private void playNote() {
        final int ms = Math.min(note.length.timeMs(), Note.MEASURE_LENGTH_SEC * 1000);
        final int length = Note.SAMPLE_RATE * ms / 1000;
        line.write(note.note.sample(), 0, length);
        line.write(Note.REST.sample(), 0, 50);
    }

}