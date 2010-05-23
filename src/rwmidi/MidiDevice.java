package rwmidi;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

/**
 * Represents the information about a MIDI device as returned by the Java MIDI System. This can either represent
 * a physical MIDI input or MIDI output, or a virtual MIDI device (such as the Java Sound Synthesizer). Some MidiDevices 
 * can be opened for both input and output, however this is platform dependent, so the best way to use MidiDevices
 * is to have separate ones for input and output. This is why the devices are further subclasses into MidiInputDevice and MidiOutputDevice.
 * 
 * You can pass an object when creating an input. Common methods implemented by this object will be registered with the MidiInput. These methods
 * are noteOnReceived(Note), noteOffReceived(Note), controllerChangeReceived(Controller), programChangeReceived(ProgramChange) and
 * sysexReceived(SysexMessage). An additional channel can be given to filter out a specific channel for note and controller change messages.
 * 
 */
public class MidiDevice {
	javax.sound.midi.MidiDevice.Info info;
	javax.sound.midi.MidiDevice device;
	
	/**
	 * Create a Midi Device from a java MidiDeviceInfo structure. This should not be used unless you know what you are doing.
	 * @param _info The javax.sound.midi.MidiDevice.Info structure
	 */
	public MidiDevice(javax.sound.midi.MidiDevice.Info _info) {
		info = _info;
		device = null;
	}
	
	/**
	 * Create a Midi Device from a java MidiDevice structure. This should not be used unless you know what you are doing.
	 * @param _device The javax.sound.midi.MidiDevice structure
	 */
	public MidiDevice(javax.sound.midi.MidiDevice _device) {
		device = _device;
		info = _device.getDeviceInfo();
	}
	
	protected javax.sound.midi.MidiDevice getDevice() {
		if (device != null) {
			return device;
		}
		try {
			device = MidiSystem.getMidiDevice(info);
		} catch (MidiUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return device;
	}
	
	/**
	 * 
	 * @return the name of the device (long version)
	 */
	public String getName() {
		return info.getName() + " " + info.getVendor();
	}
	
	public String toString() {
		return getName();
	}
	
	javax.sound.midi.MidiDevice.Info getInfo() {
		return info;
	}
	
}
