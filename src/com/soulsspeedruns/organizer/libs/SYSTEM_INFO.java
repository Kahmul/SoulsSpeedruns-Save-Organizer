/**
 * 
 */
package com.soulsspeedruns.organizer.libs;


import com.sun.jna.IntegerType;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.Union;


/**
 * SYSTEM_INFO
 * <p>
 * JNA struct containing information about the system environment.
 * 
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 6 Feb 2024
 */
@FieldOrder({ "processorArchitecture", "dwPageSize", "lpMinimumApplicationAddress", "lpMaximumApplicationAddress", "dwActiveProcessorMask",
		"dwNumberOfProcessors", "dwProcessorType", "dwAllocationGranularity", "wProcessorLevel", "wProcessorRevision" })
public class SYSTEM_INFO extends Structure
{

	@FieldOrder({ "wProcessorArchitecture", "wReserved" })
	public static class PI extends Structure
	{

		public static class ByReference extends PI implements Structure.ByReference
		{

		}


		/**
		 * System's processor architecture. This value can be one of the following values:
		 *
		 * PROCESSOR_ARCHITECTURE_UNKNOWN PROCESSOR_ARCHITECTURE_INTEL PROCESSOR_ARCHITECTURE_IA64 PROCESSOR_ARCHITECTURE_AMD64
		 */
		public short wProcessorArchitecture;
		/**
		 * Reserved for future use.
		 */
		public short wReserved;
	}


	public static class UNION extends Union
	{

		public static class ByReference extends UNION implements Structure.ByReference
		{

		}


		/**
		 * An obsolete member that is retained for compatibility with Windows NT 3.5 and earlier. New applications should use the
		 * wProcessorArchitecture branch of the union. Windows Me/98/95: The system always sets this member to zero, the value defined for
		 * PROCESSOR_ARCHITECTURE_INTEL.
		 */
		public int dwOemID;
		/**
		 * Processor architecture (unnamed struct).
		 */
		public PI pi;


		@Override
		public void read()
		{
			// dwOemID is obsolete but users may have come to rely on its value because it
			// was initialized by default, so we retain its initialization for
			// compatibility.
			setType("dwOemID");
			super.read();
			// pi requires type defined for initialization as a structure.
			setType("pi");
			super.read();
		}
	}


	/**
	 * Processor architecture (unnamed union).
	 */
	public UNION processorArchitecture;
	/**
	 * Page size and the granularity of page protection and commitment.
	 */
	public int dwPageSize;
	/**
	 * Pointer to the lowest memory address accessible to applications and dynamic-link libraries (DLLs).
	 */
	public Pointer lpMinimumApplicationAddress;
	/**
	 * Pointer to the highest memory address accessible to applications and DLLs.
	 */
	public Pointer lpMaximumApplicationAddress;
	/**
	 * Mask representing the set of processors configured into the system. Bit 0 is processor 0; bit 31 is processor 31.
	 */
	public DWORD_PTR dwActiveProcessorMask;
	/**
	 * Number of processors in the system.
	 */
	public int dwNumberOfProcessors;
	/**
	 * An obsolete member that is retained for compatibility with Windows NT 3.5 and Windows Me/98/95. Use the wProcessorArchitecture,
	 * wProcessorLevel, and wProcessorRevision members to determine the type of processor. PROCESSOR_INTEL_386 PROCESSOR_INTEL_486
	 * PROCESSOR_INTEL_PENTIUM
	 */
	public int dwProcessorType;
	/**
	 * Granularity for the starting address at which virtual memory can be allocated.
	 */
	public int dwAllocationGranularity;
	/**
	 * System's architecture-dependent processor level. It should be used only for display purposes. To determine the feature set of a processor, use
	 * the IsProcessorFeaturePresent function. If wProcessorArchitecture is PROCESSOR_ARCHITECTURE_INTEL, wProcessorLevel is defined by the CPU
	 * vendor. If wProcessorArchitecture is PROCESSOR_ARCHITECTURE_IA64, wProcessorLevel is set to 1.
	 */
	public short wProcessorLevel;
	/**
	 * Architecture-dependent processor revision.
	 */
	public short wProcessorRevision;


	public static class DWORD_PTR extends IntegerType
	{

		public DWORD_PTR()
		{
			this(0);
		}


		public DWORD_PTR(long value)
		{
			super(Native.POINTER_SIZE, value);
		}
	}

}
