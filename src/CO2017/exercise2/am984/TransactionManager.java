package CO2017.exercise2.am984;
/******************************************
 * 
 * @author adammoualdi
 * 
 * TransactionManager class which controls the transactions being passed
 * This class executes a Transaction thread thus checking whether that specific
 * transaction can be done or if it has to wait for specific resources to finish.
 * 
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class TransactionManager extends Object implements Runnable {

	private final BlockingQueue<Transaction> _arrivalQueue;
	private HashMap<Character,Resource> _resources;
	private final HashSet<Transaction> _blocked;
	private final HashSet<Transaction> _active;
	private int size;
	private ThreadPoolExecutor ex;
	private int interval;
	private boolean _changed;
	private boolean _live;
	
	public TransactionManager(ThreadPoolExecutor ex, int size, HashMap<Character,Resource> _resources, int interval) {
		this.ex = ex;
		this.size = size;
		this.interval = interval;
		this._resources = _resources;
		_changed = true;
		_live = true;
		_arrivalQueue = new ArrayBlockingQueue<Transaction>(size);
		_active= new HashSet<Transaction>();
		this._blocked= new HashSet<Transaction>(); 
	}
	
	public Resource getResource(char r) {
		return _resources.get(r);	
	}
	
	// loops through resources and sets the availability to true.
	public void setResources(HashSet<Resource> res) {
		for (Resource r : res) {
			r.set();
		}
	}
	
	// loops through resources and sets the availability to false.
	public void unsetResources(HashSet<Resource> res) {
		for (Resource r : res) {
			r.unset();
		}
	}
	
	// checks if all variables in hashset are true; if they are returns true, else returns false.
	// used for obtainLock method.
	public boolean checkResources(HashSet<Resource> res) {
		int count = 0;
		for (Resource r : res) {
			if (r.isAvailable()) {
				count++;
				// if resource is true add one to count
			}
			else {
				return false;
			}
		}
		// if all resources are available, then count should equal the amount of resources we have therefore returning true.
		if (count == res.size()) {
			return true;
		}
		return true;
	}
		
	// checking if the arrival queue, active queue and blocked queue
	public boolean isEmpty() {
		if (_arrivalQueue.isEmpty() && _active.isEmpty() && _blocked.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	// method to check if _changed is true or false; used in SimController when printing out transaction manager toString.
	public boolean isChanged() {
		if (_changed == true) {
			return true;
		}
		else {
			return false;
		}
	}
	
	// method to change _live variable to false so we can stop the TransactionManager thread in SimController.
	public void stopQueue() {
		_live = false;
	}
	
	// method that takes a transaction and puts it into the arrival queue.
	public void enQueue(Transaction t) throws InterruptedException {
		// The put method takes a transaction and puts it into the queue; if the queue is full, the method won't add it.
		_arrivalQueue.put(t);
		_changed = false;
	}
	
	// method is used to free all resources all the transaction has been completed.
	public synchronized void freeUp(Transaction t) {
		HashSet<Resource> resource = new HashSet<Resource>();
		resource = t.getDependencies();
		
		
		System.out.println(t.toString() + " released all resources");
		// set resources of transaction to true
		setResources(resource);
		// remove transaction from active queue as it has been completed.
		_active.remove(t);
		// notify all threads this has occurred so other transactions that are waiting on resources can now use them.
		notifyAll();
		
		_changed = false;
		
	}
	
	// method checks if resources can be used before starting transaction in the transaction run thread. If transaction not available then 
	// wait for the resources to become available. If they are available, make resources unavailable, remove transaction from blocked and add
	// the transaction to active queue.
	public synchronized void obtainLock(Transaction t) throws InterruptedException {
		// get transaction resources
		HashSet<Resource> resource = new HashSet<Resource>();
		resource = t.getDependencies();
		
		// use guards to wait for all resources to be available; if resource not available then wait until it is available. 
		while (!checkResources(resource)) {
			wait();	
		}
		
		// if resources are available: 
		System.out.println(t.toString() + " obtained all resources");
		unsetResources(resource);
		_blocked.remove(t);
		_active.add(t);
		_changed = false;
		notifyAll();
	}
	
	// TransactionManager thread run method which is always live until _live is changed to false; this is made false
	// when the executor not executing.
	public void run() {
		while(_live) {
			try {
				// take the head of the arrival queue list
				Thread.sleep(interval*100);
				Transaction head = _arrivalQueue.take();
				// execute the head of the arrival queue in Transaction
				ex.execute(head);
				// add the transaction to blocked; if the transaction resources are available it will be taken out of block.
				_blocked.add(head);
				System.out.println(head.toString() + " retrieved from the arrival queue");
				_changed = false;	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// ToString method which is called in the SimController thread
	@Override
	public String toString() {
		_changed = true;
		String arrivalQ = _arrivalQueue.toString();
		String activeQ = _active.toString();
		String blockQ = _blocked.toString();
		return
				("  "  + "Queue   | " + (_arrivalQueue.isEmpty() ? "[Empty]" : arrivalQ.substring(1, arrivalQ.length()-2)) + "\n") +
				("  "  + "Active  | " + (_active.isEmpty() ? "[Empty]" : activeQ.substring(1, _active.toString().length()-2)) + "\n") + 
				("  "  + "Blocked | " + (_blocked.isEmpty() ? "[Empty]" : blockQ.substring(1, _blocked.toString().length()-2)) + ""); 
	}
	
//	public static void main (String[] args) {
//		Resource r1 = new Resource('c');
//		Resource r2 = new Resource('d');
//		HashMap<Character, Resource> resources = new HashMap<Character, Resource>();
//		resources.put('A', r1);
//		resources.put('B', r2);
//		ThreadPoolExecutor ex = (ThreadPoolExecutor) Executors.newFixedThreadPool(5); 
//		TransactionManager tm = new TransactionManager(ex, 1, resources, 5);
//		HashSet<Resource> res = new HashSet<Resource>();
//		res.add(r1);
//		res.add(r2);
//		Transaction t = new Transaction(tm, 'c', 100, res);
//		try {
//			tm.obtainLock(t);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			tm.enQueue(t);
//		} catch (Exception e) {
//			System.out.println("error");
//			e.printStackTrace();
//		}
//		try {
//		//	System.out.println(tm.toString());
//		} catch (Exception e) {
//			System.out.println("error");
//			e.printStackTrace();
//		}
		
		
//	}

}


