/*
Part of the proMIDI lib - http://texone.org/promidi

Copyright (c) 2005 Christian Riekoff

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General
Public License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place, Suite 330,
Boston, MA  02111-1307  USA
*/

package rwmidi;

/**
 * Wrapper around Note MIDI messages. You can extract the pitch using the {@Link Note-getPitch} and the velocity using
 * {@Link Note-getVelocity}.
 *
 */
public class Note extends MidiEvent{
	/**
	 * Create a Note object.
	 * @param i_pitch the pitch of the note
	 * @param i_velocity the velocity of the note
	 */
	public Note(final int i_pitch, final int i_velocity){
		super(NOTE_ON, i_pitch, i_velocity);
	}
	
	public Note(int midiChannel, int _pitch, int _velocity) {
		super(NOTE_ON | midiChannel, _pitch, _velocity);
	}

	public Note(int midiCommand, int midiChannel, int _pitch, int _velocity) {
		super(midiCommand | midiChannel, _pitch, _velocity);
	}

	/**
	 * 
	 * @return Pitch of the note
	 */
	public int getPitch(){
		return getData1();
	}
	
    void setPitch(final int i_pitch){
		setData1(i_pitch);
    }
    
    /**
     * 
     * @return Velocity of the note
     */
	public int getVelocity(){
		return getData2();
	}

	void setVelocity(final int i_velocity){
		setData2(i_velocity);
    }
	
	public String toString() {
		return "rwmidi.Note pitch: " + getPitch() + " velocity: " + getVelocity(); 
	}
}
