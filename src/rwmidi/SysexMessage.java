package rwmidi;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * This is a wrapper around a SysexMessage. You can access the bytes using getMessage(). 
 */
public class SysexMessage extends MidiEvent {
	protected SysexMessage(javax.sound.midi.SysexMessage msg) {
		super(msg);
	}
	
	/**
	 * Create a sysex message from the given bytes
	 * @param data sysex bytes (must contain 0xF0 and 0xF7)
	 */
	public SysexMessage(byte[] data) {
		super(data);
	}
	
	static String printHex(byte[] b) {
		try {
			Writer sw = new StringWriter();
			for (int i = 0; i < b.length; ++i) {
				if (i % 16 == 0) {
					sw.append(Integer.toHexString ((i & 0xFFFF) | 0x10000).substring(1,5) + " - ");
				}
				sw.append(Integer.toHexString((b[i]&0xFF) | 0x100).substring(1,3) + " ");
				if (i % 16 == 15 || i == b.length - 1)
				{
					int j;
					for (j = 16 - i % 16; j > 1; --j)
						sw.append("   ");
					sw.append(" - ");
					int start = (i / 16) * 16;
					int end = (b.length < i + 1) ? b.length : (i + 1);
					for (j = start; j < end; ++j)
						if (b[j] >= 32 && b[j] <= 126)
							sw.append((char)b[j]);
						else
							sw.append(".");
					sw.append("\n");
				}
			}
			sw.append("\n");

			return sw.toString();
		} catch (IOException e) {
			return "";
		}
	}

	public String toString() {
		return "Sysex Message: \n" + printHex(getMessage());
	}
}
