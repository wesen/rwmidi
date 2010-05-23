package rwmidi;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.MidiDevice.Info;

/**
 * Represents a device that can be opened for sending MIDI messages.
 * @author manuel
 *
 */
public class MidiOutputDevice extends MidiDevice {

	public MidiOutputDevice(Info _info) {
		super(_info);
	}

	/**
	 * Create an output object for the MIDI device.
	 * @return the created output
	 */
	public MidiOutput createOutput() {
		try {
			return new MidiOutput(this);
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
			return null;
		}
	}


}
