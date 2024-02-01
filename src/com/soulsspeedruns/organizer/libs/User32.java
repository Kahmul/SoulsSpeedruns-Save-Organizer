/**
 * 
 */
package com.soulsspeedruns.organizer.libs;


import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APIOptions;


/**
 * <ShortDescription>
 * <p>
 * <LongDescription>
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 31 Jan 2024
 */
public interface User32 extends W32APIOptions
{

	User32 INSTANCE = Native.load("user32", User32.class);


	Pointer FindWindowA(String winClass, String title);

	int GetWindowThreadProcessId(Pointer hWnd, IntByReference lpdwProcessId);

}
