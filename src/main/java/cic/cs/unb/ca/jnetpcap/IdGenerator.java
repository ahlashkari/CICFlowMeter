package cic.cs.unb.ca.jnetpcap;

public class IdGenerator {
	
	private long id = 0L;

	public IdGenerator(long id) {
		super();
		this.id = id;
	}

	public IdGenerator() {
		super();
		this.id = 0L;
	}
	
	public synchronized long nextId(){
		this.id++;
		return this.id;
	}

}
