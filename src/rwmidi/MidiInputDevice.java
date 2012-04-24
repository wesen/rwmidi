package rwmidi;

import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiUnavailableException;

/**
 * Represents a device that can be opened for reading and receiving MIDI messages. An object with callbacks can
 * be given when creating an input from this input device.
 *
 * @author manuel
 */
public class MidiInputDevice extends rwmidi.MidiDevice {

  MidiInputDevice(Info _info) {
    super(_info);
  }

  /**
   * Create an input object for the device.
   *
   * @return the created input
   */
  public rwmidi.MidiInput createInput() {
    try {
      return new rwmidi.MidiInput(this);
    } catch (MidiUnavailableException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Create an input object for the device. See {@Link MidiInput-plug-Object}
   *
   * @param obj Object to be registered as callback
   * @return the created input
   */
  public rwmidi.MidiInput createInput(Object obj) {
    return createInput(obj, -1);
  }

  /**
   * Create an input object for the device and register the object given as argument as a callback for messages on the given channel.
   *
   * @param obj     Object to be registered as callback
   * @param channel Channel on which the object is to be registered
   * @return the created input
   */
  public rwmidi.MidiInput createInput(Object obj, int channel) {
    rwmidi.MidiInput input = createInput();
    input.plug(obj, channel);
    return input;
  }
}
