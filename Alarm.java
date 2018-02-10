package nachos.threads;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import nachos.machine.*;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
	/**
	 * Allocate a new Alarm. Set the machine's timer interrupt handler to this
	 * alarm's callback.
	 * 
	 * <p>
	 * <b>Note</b>: Nachos will not function correctly with more than one alarm.
	 */
	public Alarm() {

		Machine.timer().setInterruptHandler(new Runnable() {
			public void run() {
				timerInterrupt();
			}
		});
		
	}

	/**
	 * The timer interrupt handler. This is called by the machine's timer
	 * periodically (approximately every 500 clock ticks). Causes the current
	 * thread to yield, forcing a context switch if there is another thread that
	 * should be run.
	 */
	public void timerInterrupt() {
		
		if (waitlist.isEmpty()) return;
		Map.Entry<KThread, Long> p = waitlist.getFirst();
		if(p != null){
			if(p.getValue() != null  ) 
				waitlist.removeFirst();
				p.getKey().ready();
		}
		
	}

	/**
	 * Put the current thread to sleep for at least <i>x</i> ticks, waking it up
	 * in the timer interrupt handler. The thread must be woken up (placed in
	 * the scheduler ready set) during the first timer interrupt where
	 * 
	 * <p>
	 * <blockquote> (current time) >= (WaitUntil called time)+(x) </blockquote>
	 * 
	 * @param x the minimum number of clock ticks to wait.
	 * 
	 * @see nachos.machine.Timer#getTime()
	 */
	public void waitUntil(long x) {
		// for now, cheat just to get something working (busy waiting is bad)
		long wakeTime = Machine.timer().getTime() + x;

		boolean intStatus = Machine.interrupt().disable();
		waitmap.put(KThread.currentThread(), wakeTime);
		waitlist.sort( new Comparator<Map.Entry<KThread, Long>>() {

			@Override
			public int compare(Entry<KThread, Long> o1, Entry<KThread, Long> o2) {
				// TODO Auto-generated method stub
				return (o1.getValue()).compareTo( o2.getValue() ) ;
			}
	    });
		KThread.sleep();

		Machine.interrupt().restore(intStatus);
		
	}
	
	
	private Map<KThread, Long> waitmap = new HashMap<KThread, Long >(); 
	private LinkedList<Map.Entry<KThread, Long>> waitlist = new LinkedList<Map.Entry<KThread, Long>>();
}
