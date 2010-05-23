package rwmidi;

import java.util.ArrayList;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JFrame;

/**
 * RWMidi is a class containing static methods to get a list of the available MIDI devices.
 * You can ask RWMidi about available input and output devices. You can then use the methods
 * on the returned objects to open an input or output midi port. 
 * 
 * RWMidi doesn't provide access to the available MIDI devices through an index number, but
 * rather requires you to use the returned MidiDevice object. This is because the number of
 * available devices can change between a call to the function listing the device and the call 
 * opening the device, which could potentially lead to an index mismatch.
 */
public class RWMidi {
	/**
	 * 
	 * @return the list of the available input devices.
	 */
	public static MidiInputDevice[] getInputDevices() {
		javax.sound.midi.MidiDevice.Info infos[] = MidiSystem.getMidiDeviceInfo();
		ArrayList<MidiInputDevice> result = new ArrayList<MidiInputDevice>();
		for (javax.sound.midi.MidiDevice.Info info : infos) {
			javax.sound.midi.MidiDevice device;
			try {
				device = MidiSystem.getMidiDevice(info);
				if (device.getMaxTransmitters() == 0)
					continue;
				result.add(new MidiInputDevice(info));
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
				continue;
			}
		}
		return result.toArray(new MidiInputDevice[0]);
	}
	
	/**
	 * 
	 * @return the list of the available output devices.
	 */
	public static MidiOutputDevice[] getOutputDevices() {
		javax.sound.midi.MidiDevice.Info infos[] = MidiSystem.getMidiDeviceInfo();
		ArrayList<MidiOutputDevice> result = new ArrayList<MidiOutputDevice>();
		for (javax.sound.midi.MidiDevice.Info info : infos) {
			javax.sound.midi.MidiDevice device;
			try {
				device = MidiSystem.getMidiDevice(info);
				if (device.getMaxReceivers() == 0)
					continue;
				result.add(new MidiOutputDevice(info));
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
				continue;
			}
		}
		return result.toArray(new MidiOutputDevice[0]);
	}

	/**
	 * 
	 * @return a list of the output devices names
	 */
	public static String[] getOutputDeviceNames() {
		MidiOutputDevice[] devices = getOutputDevices();
		ArrayList<String> result = new ArrayList<String>();
		for (MidiOutputDevice device : devices) {
			result.add(device.getName());
		}
		return result.toArray(new String[0]);
	}
	
	/**
	 * Returns a specific output device
	 * @param name the name of the output device
	 * @return
	 */
	public static MidiOutputDevice getOutputDevice(String name) {
		MidiOutputDevice[] devices = getOutputDevices();
		
		for (MidiOutputDevice device : devices) {
			if (name.equals(device.getName()))
				return device;
		}
		
		return null;
	}

	/**
	 * 
	 * @return a list of the output devices names
	 */
	public static String[] getInputDeviceNames() {
		MidiInputDevice[] devices = getInputDevices();
		ArrayList<String> result = new ArrayList<String>();
		for (MidiInputDevice device : devices) {
			result.add(device.getName());
		}
		return result.toArray(new String[0]);
	}
	
	/**
	 * Returns a specific input device
	 * @param name the name of the input device
	 * @return
	 */
	public static MidiInputDevice getInputDevice(String name) {
		MidiInputDevice[] devices = getInputDevices();
		
		for (MidiInputDevice device : devices) {
			if (name.equals(device.getName()))
				return device;
		}
		
		return null;
	}

	public static void main(String args[]) {
		class Foobar {
			Foobar() {
				
			}
			
			void noteOnReceived(Note note) {
				System.out.println("note on " + note);
			}

			void noteOffReceived(Note note) {
				System.out.println("note off " + note);
			}
			
			void sysexReceived(SysexMessage msg) {
				System.out.println("sysex " + msg);
			}
		}
		
		int i = 0;
		for (MidiDevice device : getInputDevices()) {
			System.out.println(i + " input device " + device);
			i++;
		}
		
		i = 0;
		for (MidiDevice device : getOutputDevices()) {
			System.out.println(i + " output device " + device);
			i++;
		}
		
		Foobar foo = new Foobar();
		MidiInput input = RWMidi.getInputDevices()[2].createInput(foo);
		MidiOutput output = RWMidi.getOutputDevices()[1].createOutput();
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
