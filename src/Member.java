import javax.sound.sampled.SourceDataLine;

/**
 * A thread which plays a single kind of note
 */
public class Member implements Runnable {
    // What note am I playing?
    private final BellNote note;
    // The audio device to play on
    private final SourceDataLine line;

    // Initialize the member, tell them which note to play
    public Member(BellNote note, SourceDataLine line) {
        this.note = note;
        this.line = line;
    }

    // When we're asked to run, play our note
    public void run() {
        playNote();
    }

    // Play the note
    private void playNote() {
        final int ms = Math.min(note.length.timeMs(), Note.MEASURE_LENGTH_SEC * 1000);
        final int length = Note.SAMPLE_RATE * ms / 1000;
        line.write(note.note.sample(), 0, length);
        line.write(Note.REST.sample(), 0, 50);
    }

}