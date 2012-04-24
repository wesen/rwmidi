package rwmidi;

import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiUnavailableException;

/**
 * Represents a device that can be opened for sending MIDI messages.
 *
 * @author manuel
 */
public class MidiOutputDevice extends rwmidi.MidiDevice {

  public MidiOutputDevice(Info _info) {
    super(_info);
  }

  /**
   * Create an output object for the MIDI device.
   *
   * @return the created output
   */
  public rwmidi.MidiOutput createOutput() {
    try {
      return new rwmidi.MidiOutput(this);
    } catch (MidiUnavailableException e) {
      e.printStackTrace();
      return null;
    }
  }


}
