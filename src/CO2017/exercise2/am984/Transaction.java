package CO2017.exercise2.am984;
/******************************
 * 
 * @author adammoualdi
 * 
 * Transaction class where we give the transactionManager instance,
 * the character ID, the length of the resource (the amount of time 
 * it takes to complete the resource) and the resources the transaction 
 * needs.
 * 
 */
import java.util.HashSet;

public class Transaction extends java.lang.Object implements Runnable{

	private final char id;
	private final int length;
	private final HashSet<Resource> resources;
	private final TransactionManager tm;
	
	public Transaction(TransactionManager tm, char id, int length, HashSet<Resource> resources) {
		this.tm = tm;
		this.id = id;
		this.length = length;
		this.resources = resources;
	}
	
	public int getID() {
		return id;
	}
	
	public HashSet<Resource> getDependencies() {
		return resources;
	}
	
	public void run() {
		try {
			// use obtainLock to get resources needed for transaction.
			tm.obtainLock(this);
			System.out.println(this + " is running");
			// Sleep for the length of the transaction * 100.
			Thread.sleep(length*100);
			System.out.println(this + " is finished");
			// When the transaction is finished, free up the resources so they can be used. 
			tm.freeUp(this);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public String toString() {
		return id + resources.toString() + ":" + length;
	}
	
//	public static void main (String[] args) {
//		Resource r = new Resource('s');
//		HashSet<Resource> h = new HashSet<Resource>();
//		h.add(r);   
//		Transaction t = new Transaction('B', 100, h);
//		System.out.println(t.toString());
//	}
}
