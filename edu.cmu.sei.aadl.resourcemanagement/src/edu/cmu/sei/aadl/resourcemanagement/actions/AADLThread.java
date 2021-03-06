/* Created on Oct 18, 2004
 */
package edu.cmu.sei.aadl.resourcemanagement.actions;

import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.properties.PropertyNotPresentException;
import org.osate.xtext.aadl2.properties.GetProperties;

import EAnalysis.BinPacking.SoftwareNode;

/**
 * SoftwareNode representing an AADL thread instance. Instances are created
 * using the <code>static</code> factory method
 * {@link #createInstance(ComponentInstance)}.
 *
 * @author aarong
 */
public final class AADLThread extends SoftwareNode {
	public static final long DEFAULT_PERIOD_NANOSECOND = 1L;

	private AADLThread(final ComponentInstance thr, final long cycles,
			final long period, final long deadline) {
		super(cycles, period, deadline, thr.getName());
		setSemanticObject(thr);
	}
	
	public static AADLThread createInstance(final ComponentInstance thread) {
		final long period = (long)GetProperties.getPeriodinNS(thread);

		long deadline;
		try
		{
			deadline = (long)GetProperties.getDeadlineinNS(thread);
		}
		catch (PropertyNotPresentException e)
		{
			deadline = 0;
		}
		
		double maxComputeTime;
		try
		{
			maxComputeTime = GetProperties.getComputeExecutionTimeinSec(thread);
		}
		catch (PropertyNotPresentException e)
		{
			maxComputeTime = 0.0;
		}
		if (maxComputeTime == 0){ // Choose deadline as WCET
			try
			{
				maxComputeTime = GetProperties.getDeadlineinSec(thread);
			}
			catch (PropertyNotPresentException e)
			{
				maxComputeTime = 0.0;
			}
		}
	
		final long cycles = (long) (maxComputeTime / GetProperties.getReferenceCycleTimeinSec(thread));
	
		return new AADLThread(thread, cycles, period, deadline);
	}

	/** Get the AADL thread component instance represented by this object. */
	public ComponentInstance getThread() {
		return (ComponentInstance) getSemanticObject();
	}
	
}