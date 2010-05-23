package rwmidi;

/**
 * Represents a MIDI Controller Change message. The values are parsed into the CC number and the value, which
 * you can access using the methods {@Link Controller-getCC} and {@Link Controller-getValue}.
 * 
 */
public class Controller extends MidiEvent{
	/**
	 * Create a Controller Change message.
	 * @param _channel Controller Change channel
	 * @param _number Controller Change number
	 * @param _value Controller Change value
	 */
	public Controller(final int _channel, final int _number, final int _value){
		super(CONTROL_CHANGE | _channel, _number, _value);
	}

	public Controller(final int _number, final int _value){
		super(CONTROL_CHANGE, _number, _value);
	}

	/**
	 * 
	 * @return the CC number of the message
	 */
	public int getCC(){
		return getData1();
	}

	/**
	 * 
	 * @return the value of the CC message
	 */
	public int getValue(){
		return getData2();
	}

	public String toString() {
		return "rwmidi.Controller cc: " + getCC() + " value: " + getValue();
	}
}
