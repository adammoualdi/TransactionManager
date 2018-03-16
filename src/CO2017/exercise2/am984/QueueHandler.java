package CO2017.exercise2.am984;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.NoSuchFileException;
import java.util.Scanner;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.HashSet;
/*****************************************************
 * 
 * @author adammoualdi
 *
 * Reader class that reads a file which has all the transactions; this file gives the transactions to 
 * the enQueue method in TransactionManager.
 *
 */
public class QueueHandler implements Runnable {
  
	private ThreadPoolExecutor ex;
	private TransactionManager tm;
	private String filename;
	
	public QueueHandler(ThreadPoolExecutor ex, TransactionManager tm, String f) {
		this.ex = ex;
		this.tm = tm;
		this.filename = f;
	}

	// SimController thread
	public void run() {
	    Path fpath = Paths.get(filename);
	    try (Scanner file = new Scanner(fpath)) {
	    	int runtime;
	    	char pid;
	    	
	    	// read all the details from the file we input and creates new transaction object t.
	    	while (file.hasNextLine()) {
	    		Scanner line = new Scanner(file.nextLine());
		        HashSet<Resource> resources = new HashSet<Resource> ();
		        line.useDelimiter(":");
		        pid     = line.next().charAt(0);
		        runtime = line.nextInt();
		        // adding r to resources HashMap and then adding it to new Transaction object t. 
		        while(line.hasNext()) {
		        	Resource r = tm.getResource(line.next().charAt(0));
		        	resources.add(r);
		        }
		        Transaction t = new Transaction(tm, pid, runtime, resources);
		        try {
		        	// add new Transaction t to enQueue method.
					tm.enQueue(t);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		        line.close();
	    	}
	    	file.close();
	    	
	    } catch (NoSuchFileException e) {
	    	System.err.println("File not found: "+filename);
	    	System.exit(1);
	    } catch (IOException e) {
	    	System.err.println(e);
	    	System.err.println("IO exception");
	    	System.exit(1);
	    }
	}

}