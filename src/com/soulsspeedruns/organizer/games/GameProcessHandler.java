package com.soulsspeedruns.organizer.games;


import java.util.Timer;
import java.util.TimerTask;

import com.soulsspeedruns.organizer.libs.Kernel32;
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

	private static final int HOOK_THREAD_INTERVAL = 1000;

	// static to prevent multiple game processes from being hooked at any given time
	private static Pointer process = null;
	private static Game hookedGame = null;


	protected GameProcessHandler()
	{
	}


	/**
	 * Gets the process ID of the currently selected game.
	 * 
	 * @return the process ID if the game process is running. 0 if not or if the selected game does not support process handling.
	 */
	private static int getSelectedGameProcessID()
	{
		Game selectedGame = GamesManager.getSelectedGame();
		GameProcessHandler handler = selectedGame.getProcessHandler();
		if (handler == null)
			return 0;

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
	private static Pointer openGameProcess(int pid)
	{
		return Kernel32.INSTANCE.OpenProcess(PROCESS_VM_READ | PROCESS_VM_WRITE | PROCESS_VM_OPERATION, true, pid);
	}


	/**
	 * Closes the given game process handle.
	 * 
	 * @param process the process handle to close
	 * @return whether the close operation was successful
	 */
	private static boolean closeGameProcess(Pointer process)
	{
		return Kernel32.INSTANCE.CloseHandle(process);
	}


	/**
	 * Reads the int value at the given address and offsets for the currently hooked process.
	 * 
	 * @param address the base address
	 * @param offsets the offsets from the base address
	 * @return the int value that was read
	 */
	protected static int readInt(long address, int[] offsets)
	{
		return readMemory(address, offsets, 4).getInt(0);
	}


	/**
	 * Reads the game's memory at the given address and offsets with the specific amount of bytes to read
	 * 
	 * @param address     the base address
	 * @param offsets     the offsets from the base address
	 * @param bytesToRead the number of bytes to read
	 * @return a memory object containing the bytes that were read
	 */
	protected static Memory readMemory(long address, int[] offsets, int bytesToRead)
	{
		if (process == null)
			return null;

		if (offsets != null && offsets.length > 0)
			address = findDynamicAddress(address, offsets, bytesToRead);

		Memory output = new Memory(bytesToRead);
		Kernel32.INSTANCE.ReadProcessMemory(process, address, output, bytesToRead, null);

		return output;
	}


	/**
	 * Writes the given int value at the given address and offsets.
	 * 
	 * @param address the base address
	 * @param offsets the offsets from the base address
	 * @param data    the int value to write
	 * @return whether the write action was successful
	 */
	protected static boolean writeInt(long address, int[] offsets, int data)
	{
		byte[] array = new byte[] { (byte) (data >> 0), (byte) (data >> 8), (byte) (data >> 16), (byte) (data >> 24), };

		return writeBytes(address, offsets, array);
	}


	/**
	 * Writes the given byte array at the given address and offsets.
	 * 
	 * @param address the base address
	 * @param offsets the offsets from the base address
	 * @param data    the bytes to write
	 * @return whether the write action was successful
	 */
	protected static boolean writeBytes(long address, int[] offsets, byte[] data)
	{
		if (process == null)
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
		if(!VersionManager.isRunningOnWindows())
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
		process = openGameProcess(pid);

		if (isHooked())
		{
			hookedGame = GamesManager.getSelectedGame();
			hookedGame.getProcessHandler().init();
		}

	}


	/**
	 * Unhooks from the process of whatever game was hooked. Does nothing if no hook is active.
	 */
	private static void unhook()
	{
		if (process == null)
			return;

		closeGameProcess(process);
		process = null;
		
		hookedGame.getProcessHandler().close();
		hookedGame = null;
	}


	/**
	 * Returns whether any game process is currently hooked into.
	 * 
	 * @return whether a hook is currently active
	 */
	public static boolean isHooked()
	{
		return process != null;
	}


	/**
	 * Called whenever a game process is hooked.
	 */
	protected abstract void init();

	/**
	 * Called whenever a game process is unhooked.
	 */
	protected abstract void close();

	/**
	 * The window name of the game process that will be tried to hook into.
	 * 
	 * @return the window name of the game process as string
	 */
	protected abstract String getWindowTitle();

}
