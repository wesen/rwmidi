package rwmidi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

/**
 * Simple wrapper around MIDI messages, used to abstract from the actual bytes and provide a
 * more symbolic representation of the MIDI data. This class is used as a superclass for
 * messages received on a {@Link MidiInput} object. You don't usually have to create such objects yourself.
 */
public class MidiEvent extends ShortMessage {
  public static final int SYSEX_START    = 0xF0;
  public static final int SYSEX_END      = 0xF7;
  public static final int NOTE_OFF       = 0x80;
  public static final int NOTE_ON        = 0x90;
  public static final int CONTROL_CHANGE = 0xB0;
  public static final int PROGRAM_CHANGE = 0xC0;
  private             int midiChannel    = 0;

  rwmidi.MidiInput input = null;

  protected MidiEvent(byte[] data) {
    super(data);
  }

  MidiEvent(final MidiMessage _midiMessage) {
    this(_midiMessage.getMessage());
  }

  MidiEvent(int command, int number, int value) {
    this(new byte[]{ (byte) NOTE_ON, 0, 0 });
    try {
      setMessage(command | midiChannel, number, value);
    } catch (InvalidMidiDataException e) {
      e.printStackTrace();
    }
  }

  /**
   * @return the input on which this message was received.
   */
  public rwmidi.MidiInput getInput() {
    return input;
  }

  void setInput(rwmidi.MidiInput _input) {
    input = _input;
  }

  void setData1(int b) throws InvalidMidiDataException {
    setMessage(getStatus(), b, getData2());
  }

  void setData2(int b) throws InvalidMidiDataException {
    setMessage(getStatus(), getData1(), b);
  }


  /**
   * Create a new MidiEvent object out of a MidiMessage from the java midi stack. Dispatches on the command type to
   * create the appropriate subclass.
   *
   * @param msg
   * @return
   */
  protected static MidiEvent create(MidiMessage msg) {
    if (msg instanceof javax.sound.midi.SysexMessage) {
      return new rwmidi.SysexMessage((javax.sound.midi.SysexMessage) msg);
    } else if (msg instanceof ShortMessage) {
      ShortMessage smsg = (ShortMessage) msg;
      final int midiCommand = smsg.getCommand();
      final int midiChannel = smsg.getChannel();
      final int midiData1 = smsg.getData1();
      final int midiData2 = smsg.getData2();

      if (midiCommand == MidiEvent.NOTE_ON && midiData2 > 0) {
        return new rwmidi.Note(midiCommand, midiChannel, midiData1, midiData2);
      } else if (midiCommand == MidiEvent.NOTE_OFF || ((midiCommand == NOTE_ON) && (midiData2 == 0))) {
        return new rwmidi.Note(midiCommand, midiChannel, midiData1, midiData2);
      } else if (midiCommand == MidiEvent.CONTROL_CHANGE) {
        return new rwmidi.Controller(midiChannel, midiData1, midiData2);
      } else if (midiCommand == MidiEvent.PROGRAM_CHANGE) {
        return new rwmidi.ProgramChange(midiData1);
      }
    }
    return null;
  }
}
