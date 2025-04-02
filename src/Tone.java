import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Tone {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Please specifiy a song file");
            System.exit(1);
        }
        List<BellNote> song = loadSong(args[0]);
        final AudioFormat af = new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, false);
        try {
            Tone t = new Tone(af);
            t.playSong(song);
        } catch (LineUnavailableException e) {
            System.err.println("Error playing song: Couldn't access audio device");
        } catch (InterruptedException e) {
            System.err.println("Error playing song: Interrupted");
        }
    }

    private static List<BellNote> loadSong(String filename) {
        List<BellNote> song = new ArrayList<>();
        boolean success = true;
        int lineNumber = 0;
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    Note note;
                    try {
                        note = Note.valueOf(parts[0]);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Line #" + lineNumber + " is invalid. " + parts[0] + " isn't a valid note.");
                        System.err.println("Valid notes are: " + Arrays.toString(Note.values()));
                        success = false;
                        continue;
                    }
                    NoteLength length = NoteLength.WHOLE;
                    switch (parts[1]) {
                        case "1":
                            length = NoteLength.WHOLE;
                            break;
                        case "2":
                            length = NoteLength.HALF;
                            break;
                        case "4":
                            length = NoteLength.QUARTER;
                            break;
                        case "8":
                            length = NoteLength.EIGTH;
                            break;
                        default:
                            System.err.println("Line #" + lineNumber + " is invalid. Unknown length: " + parts[1]);
                            System.err.println("Valid lengths are: [1, 2, 4, 8]");
                            success = false;
                    }
                    song.add(new BellNote(note, length));
                } else {
                    System.err.println("Line #" + lineNumber
                            + " is invalid. Note and length should be separated by a single space");
                    success = false;
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error loading song: " + filename);
            success = false;
        }
        if (!success) {
            System.exit(1);
        }
        return song;
    }

    private final AudioFormat af;

    Tone(AudioFormat af) {
        this.af = af;
    }

    private Map<BellNote, Member> members = new HashMap<>();

    void playSong(List<BellNote> song) throws LineUnavailableException, InterruptedException {
        try (final SourceDataLine line = AudioSystem.getSourceDataLine(af)) {
            line.open();
            line.start();

            for (BellNote bellNote : song) {
                Member member;
                if (members.containsKey(bellNote)) {
                    member = members.get(bellNote);
                } else {
                    member = new Member(bellNote, line);
                    members.put(bellNote, member);
                }
                System.out.println("Playing note: " + bellNote.note);
                member.run();
                Thread.sleep(bellNote.length.timeMs());
            }
            line.drain();
        }
    }

}

class BellNote {
    final Note note;
    final NoteLength length;

    BellNote(Note note, NoteLength length) {
        this.note = note;
        this.length = length;
    }
}

enum NoteLength {
    WHOLE(1.0f),
    HALF(0.5f),
    QUARTER(0.25f),
    EIGTH(0.125f);

    private final int timeMs;

    private NoteLength(float length) {
        timeMs = (int) (length * Note.MEASURE_LENGTH_SEC * 1500);
    }

    public int timeMs() {
        return timeMs;
    }
}

enum Note {
    // REST Must be the first 'Note'
    REST,
    A4,
    A4S,
    B4,
    C4,
    C4S,
    D4,
    D4S,
    E4,
    F4,
    F4S,
    G4,
    G4S,
    A5;

    public static final int SAMPLE_RATE = 48 * 1024; // ~48KHz
    public static final int MEASURE_LENGTH_SEC = 1;

    // Circumference of a circle divided by # of samples
    private static final double step_alpha = (2.0d * Math.PI) / SAMPLE_RATE;

    private final double FREQUENCY_A_HZ = 440.0d;
    private final double MAX_VOLUME = 127.0d;

    private final byte[] sinSample = new byte[MEASURE_LENGTH_SEC * SAMPLE_RATE];

    private Note() {
        int n = this.ordinal();
        if (n > 0) {
            // Calculate the frequency!
            final double halfStepUpFromA = n - 1;
            final double exp = halfStepUpFromA / 12.0d;
            final double freq = FREQUENCY_A_HZ * Math.pow(2.0d, exp);

            // Create sinusoidal data sample for the desired frequency
            final double sinStep = freq * step_alpha;
            for (int i = 0; i < sinSample.length; i++) {
                sinSample[i] = (byte) (Math.sin(i * sinStep) * MAX_VOLUME);
            }
        }
    }

    public byte[] sample() {
        return sinSample;
    }
}
