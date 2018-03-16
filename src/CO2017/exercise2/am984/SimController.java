package CO2017.exercise2.am984;

/***********************************
 * 
 * @author adammoualdi
 * 
 * Simulation class where we initialise new classes and create threads.
 * This class consists of a watcher thread which oversees transactions and finishes execution when
 * program.  
 * 
 */

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SimController extends Object implements Runnable {
	
	public static ThreadPoolExecutor ex;
	public static TransactionManager tm;
	public static int interval;
	
	public SimController() {}
	
	// watcher thread that checks throughout if the executor has been terminated; when it's still active, it will keep printing
	// the toString of TransactionManager. When tm is empty (all the queues), we then shut down the executor.
	public void run() {
		try {
		while (!ex.isTerminated()) {
			Thread.sleep(1000);
			// if the boolean variable _changed is false then print out the tm toString method every 1 second.
			if (tm.isChanged() == false) {
				System.out.println(tm.toString());
			}
			// if all the queues are empty, then shutdown the executor.
			if (tm.isEmpty()) {
				ex.shutdown();
			}
	
		} 
			
		}catch (Exception e) {}
	}
	
	// main class were we initialise the classes and start the threads
	public static void main(String[] args) {
		// ARGUMENTS: 10 5 input.txt X Y Z
		// javac CO2017/exercise2/am984/SimController.java
		
		int queueSize = Integer.parseInt(args[0]);
		interval = Integer.parseInt(args[1]);
		String filename = args[2];
		
		HashMap<Character,Resource> rMap = new HashMap<Character, Resource>();
		for (int i = 3; i < args.length; i++) {
			Resource res = new Resource(args[i].charAt(0));
			rMap.put(args[i].charAt(0), res);
		}
		
		// creating executor and instances of classes for simulation.
		ex = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		tm = new TransactionManager(ex, queueSize, rMap , interval);
		// create transaction manager thread.
		Thread t1 = new Thread(tm);
		t1.start();
		// print the queues at the start of the simulation.
		System.out.println(tm.toString());
		SimController sc = new SimController();
		// create simulationController thread.
		Thread t3 = new Thread(sc);
		t3.start();
		QueueHandler qh = new QueueHandler(ex, tm, filename);
		// create queue handler thread
		Thread t2 = new Thread(qh);
		t2.start();

		
		try {
			// when threads have finished, join them.
			t2.join();
			t3.join();
			
		
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		// if executor is terminated then stop the transactionManager thread.
		if (ex.isTerminated()) {
			tm.stopQueue();
		}
		
		System.out.println("All threads have terminated");
		System.exit(1);

	}
}
