package com.soulsspeedruns.organizer.games;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.soulsspeedruns.organizer.libs.Kernel32;
import com.soulsspeedruns.organizer.libs.Kernel32.MEMORY_BASIC_INFORMATION;
import com.soulsspeedruns.organizer.libs.SYSTEM_INFO;
import com.soulsspeedruns.organizer.libs.User32;
import com.soulsspeedruns.organizer.managers.GamesManager;
import com.soulsspeedruns.organizer.managers.VersionManager;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;


/**
 * GameProcessHandler.
 * <p>
 * Base class handling hooking into the game processes and offering basic memory reading/writing methods for its subclasses.
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 31 Jan 2024
 */
public abstract class GameProcessHandler
{

	private static final int PROCESS_VM_READ = 0x0010;
	private static final int PROCESS_VM_WRITE = 0x0020;
	private static final int PROCESS_VM_OPERATION = 0x0008;
	private static final int PROCESS_QUERY_INFORMATION = 0x0400;

	private static final int HOOK_THREAD_INTERVAL = 1000;

	protected static final int MIN_PROCESS_LIFETIME = 3000;

	private static HashMap<Pointer, byte[]> mappedMemory;

	private static Pointer process = null;
	private static Game hookedGame = null;


	protected GameProcessHandler()
	{
	}


	/**
	 * Gets the process ID of the currently selected game.
	 * 
	 * @return the process ID if the game process is running. 0 if not or if process handling is not currently supported/possible.
	 */
	private static int getSelectedGameProcessID()
	{
		if (!GamesManager.isDataAppendageAndProcessHandlingSupported())
			return 0;

		GameProcessHandler handler = GamesManager.getSelectedGame().getProcessHandler();

		IntByReference pid = new IntByReference(0);
		User32.INSTANCE.GetWindowThreadProcessId(User32.INSTANCE.FindWindowA(null, handler.getWindowTitle()), pid);

		return pid.getValue();
	}


	/**
	 * Opens a new process handle with the given process ID for the currently selected game.
	 * 
	 * @param pid the process ID
	 * @return the pointer to the opened game process
	 */
	private static Pointer openGameProcessHandle(int pid)
	{
		return Kernel32.INSTANCE.OpenProcess(PROCESS_VM_READ | PROCESS_VM_WRITE | PROCESS_VM_OPERATION | PROCESS_QUERY_INFORMATION, true, pid);
	}


	/**
	 * Closes the given game process handle.
	 * 
	 * @param process the process handle to close
	 * @return whether the close operation was successful
	 */
	private static boolean closeGameProcessHandle(Pointer process)
	{
		return Kernel32.INSTANCE.CloseHandle(process);
	}


	/**
	 * Gets the UNIX epoch time for when the process with the given pid was created/started. Java 8 does not natively support getting process
	 * information like this yet, so this implementation uses a WMI command to retrieve it.
	 * 
	 * @param pid the ID of the process
	 * @return the start time of the process
	 */
	private static long getProcessStartTime(int pid)
	{
		String pidString = String.valueOf(pid);
		try
		{
			Process process = new ProcessBuilder(new String[] { "wmic", "process", "get", "processid,creationdate" }).start();

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())))
			{
				String line = "";
				while ((line = reader.readLine()) != null)
				{
					line = line.trim();
					if (line.endsWith(pidString))
					{
						SimpleDateFormat parser = new SimpleDateFormat("yyyyMMddHHmmss");
						return parser.parse(line.substring(0, 14)).getTime();

					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return -1;
	}


	/**
	 * Reads all memory regions of the currently opened process handle and puts the byte array contents in a HashMap for quick AoB scans.
	 */
	private static void readMemoryRegions()
	{
		List<MEMORY_BASIC_INFORMATION> memRegions = new ArrayList<>();

		SYSTEM_INFO info = new SYSTEM_INFO();

		Kernel32.INSTANCE.GetSystemInfo(info);

		Pointer memRegionPtr = info.lpMinimumApplicationAddress;
		Pointer maxAddressPtr = info.lpMaximumApplicationAddress;

		long maxAddress = Pointer.nativeValue(maxAddressPtr);

		int queryResult = 0;

		// get a list of base addresses
		do
		{
			MEMORY_BASIC_INFORMATION memInfo = new MEMORY_BASIC_INFORMATION();
			queryResult = Kernel32.INSTANCE.VirtualQueryEx(process, memRegionPtr, memInfo, memInfo.size());
			if (queryResult != 0)
			{
				if ((memInfo.state & Kernel32.MEM_COMMIT) != 0 && (memInfo.protect & Kernel32.PAGE_GUARD) == 0
						&& (memInfo.protect & Kernel32.PAGE_READWRITE) != 0)
					memRegions.add(memInfo);
				memRegionPtr = memRegionPtr.share(memInfo.regionSize.longValue());
			}

		}
		while (queryResult != 0 && Pointer.nativeValue(memRegionPtr) < maxAddress);

		mappedMemory = new HashMap<>(memRegions.size());
		Memory mem = null;
		// Read out all bytes between base addresses and put them in the HashMap
		for (MEMORY_BASIC_INFORMATION memRegion : memRegions)
		{
			long address = Pointer.nativeValue(memRegion.baseAddress);
			int size = memRegion.regionSize.intValue();
			mem = new Memory(size);
			Kernel32.INSTANCE.ReadProcessMemory(process, address, mem, size, null);
			mappedMemory.put(memRegion.baseAddress, mem.getByteArray(0, size));
		}

	}


	/**
	 * Scans the given CE-style AoB string in the games memory and returns the address where the AoB begins.
	 * 
	 * @param aob the CE-style AoB string
	 * @return the address of the AoB. 0 if the AoB is not found
	 */
	protected static long scanForAOB(String aob)
	{
		int[] pattern = getPatternFromAOB(aob);

		return scanForAOB(pattern);
	}


	/**
	 * Scans the given pattern of bytes as an int array in the games memory and returns the address where the pattern begins.
	 * 
	 * @param pattern the pattern of bytes as int array
	 * @return the address of the AoB. 0 if the AoB is not found
	 */
	protected static long scanForAOB(int[] pattern)
	{
		long address = 0;

		for (Pointer baseAddress : mappedMemory.keySet())
		{
			byte[] text = mappedMemory.get(baseAddress);
			int index = scan(text, pattern);
			if (index != -1)
			{
				address = Pointer.nativeValue(baseAddress.share(index));
				return address;
			}
		}

		return address;
	}


	/**
	 * Searches for the given int array pattern in the given byte array text and returns the index at which the pattern starts, if it's there.
	 * 
	 * @param text    the text to search within
	 * @param pattern the pattern to search for
	 * @return the index at which the pattern begins within the text
	 */
	private static int scan(byte[] text, int[] pattern)
	{
		for (int i = 0; i < text.length - pattern.length; i++)
		{
			for (int j = 0; j < pattern.length; j++)
			{
				if (pattern[j] != -1 && pattern[j] != text[i + j])
					break;

				else if (j == pattern.length - 1)
					return i;
			}
		}

		return -1;
	}


	/**
	 * Returns an int array pattern from the given string CE-style AoB. Used for AoB scans.
	 * 
	 * @param aob the CE-style AoB as string
	 * @return the corresponding int array pattern
	 */
	protected static int[] getPatternFromAOB(String aob)
	{
		String[] bytes = aob.split("\\s");
		int[] pattern = new int[bytes.length];
		for (int i = 0; i < bytes.length; i++)
		{
			if (bytes[i].equals("?"))
			{
				pattern[i] = -1;
				continue;
			}
			int firstDigit = Character.digit(bytes[i].charAt(0), 16);
			int secondDigit = Character.digit(bytes[i].charAt(1), 16);
			Byte boxedByte = (byte) ((firstDigit << 4) + secondDigit);
			pattern[i] = boxedByte.intValue();
		}

		return pattern;
	}


	/**
	 * Returns a boxed Byte array from the given CE-style AoB string.
	 * 
	 * @param aob the CE-style AoB as string
	 * @return the boxed Byte array representing the CE-style AoB string
	 */
	protected static Byte[] getByteArrayFromAOB(String aob)
	{
		String[] bytes = aob.split("\\s");
		Byte[] byteArray = new Byte[bytes.length];
		for (int i = 0; i < bytes.length; i++)
		{
			if (bytes[i].equals("?"))
			{
				byteArray[i] = null;
				continue;
			}
			int firstDigit = Character.digit(bytes[i].charAt(0), 16);
			int secondDigit = Character.digit(bytes[i].charAt(1), 16);
			byteArray[i] = (byte) ((firstDigit << 4) + secondDigit);
		}

		return byteArray;
	}


	/**
	 * Unboxes a given boxed Byte array and returns an int array pattern to use for AoB scans.
	 * 
	 * @param aob the boxed Byte array
	 * @return the int array pattern
	 */
	protected static int[] unboxByteArrayAOB(Byte[] aob)
	{
		int[] pattern = new int[aob.length];
		for (int i = 0; i < aob.length; i++)
		{
			if (aob[i] != null)
			{
				pattern[i] = aob[i].intValue();
				continue;
			}
			pattern[i] = -1;
		}
		return pattern;
	}


	/**
	 * Reads the byte at the given address and offsets for the currently hooked process.
	 * 
	 * @param address the base address
	 * @param offsets the offsets from the base address
	 * @return the byte that was read, -1 if no process is hooked
	 */
	protected static byte readByte(long address, int[] offsets)
	{
		if (!isHooked())
			return -1;

		return readMemory(address, offsets, 2).getByte(0);
	}


	/**
	 * Reads the short value at the given address and offsets for the currently hooked process.
	 * 
	 * @param address the base address
	 * @param offsets the offsets from the base address
	 * @return the short value that was read, -1 if no process is hooked
	 */
	protected static short readShort(long address, int[] offsets)
	{
		if (!isHooked())
			return -1;

		return readMemory(address, offsets, 2).getShort(0);
	}


	/**
	 * Reads the int value at the given address and offsets for the currently hooked process.
	 * 
	 * @param address the base address
	 * @param offsets the offsets from the base address
	 * @return the int value that was read, -1 if no process is hooked
	 */
	protected static int readInt(long address, int[] offsets)
	{
		if (!isHooked())
			return -1;

		return readMemory(address, offsets, 4).getInt(0);
	}


	/**
	 * Reads the game's memory at the given address and offsets with the specific amount of bytes to read
	 * 
	 * @param address     the base address
	 * @param offsets     the offsets from the base address
	 * @param bytesToRead the number of bytes to read
	 * @return a memory object containing the bytes that were read, null if no process is hooked
	 */
	protected static Memory readMemory(long address, int[] offsets, int bytesToRead)
	{
		if (!isHooked())
			return null;

		if (offsets != null && offsets.length > 0)
			address = findDynamicAddress(address, offsets, bytesToRead);

		Memory output = new Memory(bytesToRead);
		Kernel32.INSTANCE.ReadProcessMemory(process, address, output, bytesToRead, null);

		return output;
	}


	/**
	 * Writes the given byte at the given address and offsets.
	 * 
	 * @param address the base address
	 * @param offsets the offsets from the base address
	 * @param data    the byte to write
	 * @return whether the write action was successful. False if no process is hooked
	 */
	protected static boolean writeByte(long address, int[] offsets, byte data)
	{
		if (!isHooked())
			return false;

		byte[] array = new byte[] { data };

		return writeBytes(address, offsets, array);
	}


	/**
	 * Writes the given short value at the given address and offsets.
	 * 
	 * @param address the base address
	 * @param offsets the offsets from the base address
	 * @param data    the short value to write
	 * @return whether the write action was successful. False if no process is hooked
	 */
	protected static boolean writeShort(long address, int[] offsets, short data)
	{
		if (!isHooked())
			return false;

		byte[] array = new byte[] { (byte) (data >> 0), (byte) (data >> 8) };

		return writeBytes(address, offsets, array);
	}


	/**
	 * Writes the given int value at the given address and offsets.
	 * 
	 * @param address the base address
	 * @param offsets the offsets from the base address
	 * @param data    the int value to write
	 * @return whether the write action was successful. False if no process is hooked
	 */
	protected static boolean writeInt(long address, int[] offsets, int data)
	{
		if (!isHooked())
			return false;

		byte[] array = new byte[] { (byte) (data >> 0), (byte) (data >> 8), (byte) (data >> 16), (byte) (data >> 24) };

		return writeBytes(address, offsets, array);
	}


	/**
	 * Writes the given byte array at the given address and offsets.
	 * 
	 * @param address the base address
	 * @param offsets the offsets from the base address
	 * @param data    the bytes to write
	 * @return whether the write action was successful. False if no process is hooked
	 */
	protected static boolean writeBytes(long address, int[] offsets, byte[] data)
	{
		if (!isHooked())
			return false;

		if (offsets != null && offsets.length > 0)
			address = findDynamicAddress(address, offsets, 4);

		Memory toWrite = new Memory(data.length);

		for (int i = 0; i < data.length; i++)
			toWrite.setByte(i, data[i]);

		return Kernel32.INSTANCE.WriteProcessMemory(process, address, toWrite, data.length, null);
	}


	/**
	 * Finds the dynamic address based on the given base address and offsets.
	 * 
	 * @param baseAddress the base address
	 * @param offsets     the offsets from the base address
	 * @param bytesToRead the number of bytes to read
	 * @return the dynamic address
	 */
	private static long findDynamicAddress(long baseAddress, int[] offsets, int bytesToRead)
	{
		Memory temp = new Memory(bytesToRead);

		long pointer = 0;
		Kernel32.INSTANCE.ReadProcessMemory(process, baseAddress, temp, bytesToRead, null);

		for (int i = 0; i < offsets.length; i++)
		{
			pointer = temp.getInt(0) + offsets[i];
			if (i < offsets.length - 1)
				Kernel32.INSTANCE.ReadProcessMemory(process, pointer, temp, bytesToRead, null);
		}

		return pointer;
	}


	/**
	 * Starts the thread responsible for hooking and unhooking the game processes in a regular interval.
	 */
	public static void startHookThread()
	{
		if (!VersionManager.isRunningOnWindows())
			return;

		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(new TimerTask()
		{

			@Override
			public void run()
			{
				Game selectedGame = GamesManager.getSelectedGame();
				if (selectedGame != hookedGame)
				{
					hook();
					return;
				}

				// check if the hooked game was closed, if yes, remove hook
				int pid = getSelectedGameProcessID();
				if (pid == 0)
					unhook();

			}
		}, 0, HOOK_THREAD_INTERVAL);
	}


	/**
	 * Hooks the process of the currently selected game, if that game supports it.
	 */
	private static void hook()
	{
		if (isHooked())
			unhook();

		int pid = getSelectedGameProcessID();
		if (pid == 0)
			return;
		process = openGameProcessHandle(pid);
		if (process == null)
			return;

		long startTime = getProcessStartTime(pid);

		if (System.currentTimeMillis() - startTime < GamesManager.getSelectedGame().getProcessHandler().getMinimumProcessLifeTime())
		{
			closeGameProcessHandle(process);
			return;
		}

		readMemoryRegions();

		hookedGame = GamesManager.getSelectedGame();
		hookedGame.getProcessHandler().processHandleOpened();
		
		GamesManager.fireGameProcessHookedEvent(hookedGame);
	}


	/**
	 * Unhooks from the process of whatever game was hooked. Does nothing if no hook is active.
	 */
	private static void unhook()
	{
		if (!isHooked())
			return;

		closeGameProcessHandle(process);
		process = null;

		hookedGame.getProcessHandler().processHandleClosed();
		GamesManager.fireGameProcessUnhookedEvent(hookedGame);
		
		hookedGame = null;
	}


	/**
	 * Returns whether any game process is currently hooked into.
	 * 
	 * @return true if a hook is active
	 */
	public static boolean isHooked()
	{
		return hookedGame != null && process != null;
	}


	/**
	 * The amount of time in ms that the game process must have been alive for the organizer to hook into it. A default value is given with the static
	 * field MIN_PROCESS_LIFETIME which may be used as a return value. If the organizer hooks too early, memory regions might not have been
	 * initialized properly yet and AoB scans might fail.
	 * 
	 * @return the minimum life time of the game process
	 */
	protected abstract int getMinimumProcessLifeTime();

	/**
	 * Called whenever a game process is hooked.
	 */
	protected abstract void processHandleOpened();

	/**
	 * Called whenever a game process is unhooked.
	 */
	protected abstract void processHandleClosed();

	/**
	 * The window name of the game process that will be tried to hook into.
	 * 
	 * @return the window name of the game process as string
	 */
	protected abstract String getWindowTitle();

}
