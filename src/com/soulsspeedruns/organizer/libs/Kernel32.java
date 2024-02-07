/**
 * 
 */
package com.soulsspeedruns.organizer.libs;


import com.soulsspeedruns.organizer.managers.VersionManager;
import com.sun.jna.IntegerType;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
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

	Kernel32 INSTANCE = VersionManager.isRunningOnWindows() ? Native.load("kernel32", Kernel32.class) : null;

	int PAGE_GUARD = 0x100;
	int MEM_COMMIT = 0x1000;

	int PAGE_READWRITE = 0x04;
	int PAGE_EXECUTE = 0x10;
	int PAGE_EXECUTE_READ = 0x20;
	int PAGE_EXECUTE_READWRITE = 0x40;
	int PAGE_EXECUTE_WRITECOPY = 0x80;

	int PAGE_EXECUTE_ANY = PAGE_EXECUTE | PAGE_EXECUTE_READ | PAGE_EXECUTE_READWRITE | PAGE_EXECUTE_WRITECOPY;


	Pointer OpenProcess(int desired, boolean inherit, int pid);

	boolean CloseHandle(Pointer process);

	boolean ReadProcessMemory(Pointer hProcess, long inBaseAddress, Pointer outputBuffer, int nSize, IntByReference outNumberOfBytesRead);

	boolean WriteProcessMemory(Pointer p, long address, Pointer buffer, int size, IntByReference written);

	int VirtualQueryEx(Pointer readprocess, Pointer lpMinimumApplicationAddress, MEMORY_BASIC_INFORMATION lpBuffer, int dwLength);

	void GetSystemInfo(SYSTEM_INFO lpSystemInfo);


	@FieldOrder({ "baseAddress", "allocationBase", "allocationProtect", "regionSize", "state", "protect", "type" })
	public static class MEMORY_BASIC_INFORMATION extends Structure
	{

		public Pointer baseAddress;

		public Pointer allocationBase;

		public int allocationProtect;

		public SIZE_T regionSize;

		public int state;

		public int protect;

		public int type;


		public static class SIZE_T extends IntegerType
		{

			public static final SIZE_T ZERO = new SIZE_T();

			private static final long serialVersionUID = 1L;


			public SIZE_T()
			{
				this(0);
			}


			public SIZE_T(long value)
			{
				super(Native.SIZE_T_SIZE, value, true);
			}
		}

	}

}
