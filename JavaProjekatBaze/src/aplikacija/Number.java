package aplikacija;

public class Number extends Result {
	private int vrednost;
	
	public Number(String por) {
		super("Trazena vrednost: " + por);
		this.vrednost=Integer.parseInt(por);	
	}
	
	public int getVrednost() {
        return vrednost;
    }

}
