package CO2017.exercise2.am984;
/*********************************************
 * 
 * @author adammoualdi
 *
 * Resource class where we create a resource object.
 *
 */
public class Resource extends java.lang.Object {
	
	private Character id;
	private boolean available;
	
	public Resource(char id) {
		this.id = id;
		this.available = true;
	}
	
	public boolean isAvailable() {
		return available;
	}
	
	public char getId() {
		return id;
	}
	
	public void set() {
		available = true;
	}
	
	public void unset() {
		available = false;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return "" + id;
	}
	
//	public static void main (String[] args) {
//		Resource r = new Resource('c');
//		System.out.println(r.getId());
//	}
	
	
}
