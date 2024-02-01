/**
 * 
 */
package com.soulsspeedruns.organizer.libs;


import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;


/**
 * Kernel32.
 * <p>
 * Interface using JNA to implement native methods for reading from and writing to game processes.
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 31 Jan 2024
 */
public interface Kernel32 extends StdCallLibrary
{

	Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);


	Pointer OpenProcess(int desired, boolean inherit, int pid);
	
	boolean CloseHandle(Pointer process);

	boolean ReadProcessMemory(Pointer hProcess, long inBaseAddress, Pointer outputBuffer, int nSize, IntByReference outNumberOfBytesRead);

	boolean WriteProcessMemory(Pointer p, long address, Pointer buffer, int size, IntByReference written);

}
