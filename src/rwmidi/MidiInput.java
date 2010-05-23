package rwmidi;

import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Transmitter;

/**
 * Represents a MIDI input used to receive MIDI data. This can be either a physical MIDI Input or a virtual device
 * like the Java Sequencer. Open a MidiInput by using {@link MidiInputDevice-createInput}, and use the {@link MidiInput-plug-Object} method
 * to register callbacks to your objects. Use {@link MidiInput-close} to clear the callback list, and use {@link MidiInput-closeMidi} 
 * to close the corresponding MidiDevice (however, this will close all MidiInputs connected to this device). 
 */
public class MidiInput implements Receiver {
	
	javax.sound.midi.MidiDevice jDevice;
	ArrayList<Plug> plugList;

	/**
	 * Create a MidiInput from a javax.sound.midi.MidiDevice . Don't use this unless you know what you are doing.
	 * @param dev2
	 * @throws MidiUnavailableException
	 */
	public MidiInput(javax.sound.midi.MidiDevice dev2) throws MidiUnavailableException {
		this.jDevice = dev2;
		dev2.open();
		Transmitter trsmt = dev2.getTransmitter();
		trsmt.setReceiver(this);
		plugList = new ArrayList<Plug>();
		currentMessage = new ArrayList();
		System.out.println("Foo");
	}
	
	protected MidiInput(MidiInputDevice _device) throws MidiUnavailableException {
		this(_device.getDevice());
	}
	
	public String getName() {
		javax.sound.midi.MidiDevice.Info info = jDevice.getDeviceInfo();
		return info.getName() + " " + info.getVendor();
	}

	/**
	 * Close the MIDI device attached to this input. This will close all the other inputs as well.
	 */
	public void closeMidi() {
		jDevice.close();
	}

	/**
	 * Close the MIDI input.
	 */
	public void close() {
		plugList.clear();
	}

	ArrayList currentMessage;
	
	public static void printHex(byte[] b) {
		printHex(b, 0, b.length);
	}

	public static void printHex(byte[] b, int start, int length) {
		for (int i = 0; i < length; ++i) {
			if (i % 16 == 0) {
				System.out.print (Integer.toHexString ((i & 0xFFFF) | 0x10000).substring(1,5) + " - ");
			}
			System.out.print (Integer.toHexString((b[i + start]&0xFF) | 0x100).substring(1,3) + " ");
			if (i % 16 == 15 || i == length - 1)
			{
				int j;
				for (j = 16 - i % 16; j > 1; --j)
					System.out.print ("   ");
				System.out.print (" - ");
				int start2 = (i / 16) * 16;
				int end = (length < i + 1) ? length : (i + 1);
				for (j = start2; j < end; ++j)
					if (b[j + start] >= 32 && b[j + start] <= 126)
						System.out.print ((char)b[j + start]);
					else
						System.out.print (".");
				System.out.println ();
			}
		}
		System.out.println();
	}
	

	public void send(final MidiMessage message, final long timeStamp) {
					if ((message.getLength() > 1)) {
						System.out.println("message " + message);
			printHex(message.getMessage());
		}
		if (message instanceof javax.sound.midi.SysexMessage || message.getStatus() == (byte)0xF7) {
			if (message.getStatus() == 0xF0) {
//				System.out.println("clear message and start new ");
				currentMessage.clear(); // no shortcut for sysex messages
				currentMessage.add((byte)0xF0);
			}
			for (byte b : ((javax.sound.midi.SysexMessage)message).getData()) {
				if (b == (byte)0xF7) {
					currentMessage.add((byte)0xF7);
					byte msg[] = new byte[currentMessage.size()];
					for (int i = 0; i < currentMessage.size(); i++) {
						msg[i] = ((Byte)currentMessage.get(i)).byteValue();
					}
					javax.sound.midi.SysexMessage newMsg = new javax.sound.midi.SysexMessage();
					try {
						newMsg.setMessage(msg, msg.length);
						for (Plug plug : plugList)
							plug.callPlug(this, newMsg);
					} catch (InvalidMidiDataException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					currentMessage.add(b);
				}
			}
		} else {
			if (message.getStatus() >= 0xF8) {
				return;
			} else {
//				System.out.println("clear current message " + Integer.toHexString((byte)message.getStatus()));
				currentMessage.clear(); // discard maybe sysex message
			}
//			System.out.println("received message ");
//			printHex(message.getMessage());
			for (Plug plug : plugList) 
				plug.callPlug(this, message);
		}
	}

	/**
	 * Register a callback method on a specific channel for a specific MIDI command. The value field is the MIDI status byte, 
	 * for example 0x90 for NOTE ON.
	 * @param _object Callback object
	 * @param _methodName Name of the method of the callback object that will be called by the input
	 * @param channel Channel of the message, -1 for all channels
	 * @param value MIDI status byte, -1 for all messages
	 */
	public void plug(final Object _object, 
			  final String _methodName,
			  final int channel,
			  final int value) {
		if (Plug.objectHasMethod(_object, _methodName)) {
			Plug plug = new Plug(_object, _methodName, channel, value);
			plugList.add(plug);
		}
	}
	
	/**
	 * Register a callback method for all MIDI messages received on this input.
	 * @param object Callback object
	 * @param methodName Name of the method of the callback object that will be called by the input
	 */
	public void plug(
			final Object object,
			final String methodName) {
		plug(object, methodName, -1, -1);
	}
	
	/**
	 * Register a callback method for all MIDI messages received on a specific channel on this input.
	 * @param object Callback object
	 * @param methodName Name of the method of the callback object that will be called by the input.
	 * @param channel Channel on which the messages are going to be received
	 */
	public void plug(
			final Object object, 
			final String methodName, 
			final int channel) {
		plug(object, methodName, channel, -1);
	}

	/**
	 * Register an object with standard midi callbacks on all channels. The callbacks are noteOnReceived(Note),
	 * noteOffReceived(Note), controllerChangeReceived(Controller), programChangeReceived(ProgramChange) and
	 * sysexReceived(SysexMessage).
	 * @param obj the object with standard callbacks
	 */
	public void plug(Object obj) {
		plug(obj, -1);
	}
	
	/**
	 * Register an object with standard midi callbacks on a specific channels. The callbacks are noteOnReceived(Note),
	 * noteOffReceived(Note), controllerChangeReceived(Controller), programChangeReceived(ProgramChange) and
	 * sysexReceived(SysexMessage).
	 * @param obj the object with standard callbacks
	 * @param channel the channel on which to receive note and controller change messages 
	 */
	public void plug(Object obj, int channel) {
		if (obj != null) {
			plug(obj, "noteOnReceived", channel, MidiEvent.NOTE_ON);
			plug(obj, "noteOffReceived", channel, MidiEvent.NOTE_OFF);
			plug(obj, "controllerChangeReceived", channel, MidiEvent.CONTROL_CHANGE);
			plug(obj, "programChangeReceived", -1, MidiEvent.PROGRAM_CHANGE);
			plug(obj, "sysexReceived", -1, MidiEvent.SYSEX_START);
		}
	}


	public void unplug(Object obj) {
		for (Plug plug: plugList) {
			if (plug.getObject().equals(obj))
				plugList.remove(plug);
		}
	}

	public void unplug(Object obj, int channel) {
		for (Plug plug: plugList) {
			if (plug.getObject().equals(obj) && plug.getChannel() == channel)
				plugList.remove(plug);
		}
	}

	public void unplug(Object obj, String methodName, int channel) {
		for (Plug plug: plugList) {
			if (plug.getObject().equals(obj) && plug.getChannel() == channel && plug.getMethodName().equals(methodName))
				plugList.remove(plug);
		}
	}


}
