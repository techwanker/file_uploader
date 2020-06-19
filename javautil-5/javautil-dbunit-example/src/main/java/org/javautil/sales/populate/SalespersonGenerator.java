package org.javautil.sales.populate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.javautil.sales.Salesperson;

public class SalespersonGenerator {
	// todo Dennis Ritchie spelled Well

	private List<Salesperson> salesPeople = new ArrayList<Salesperson>();

	private static final String[] lastNames = new String[] { //
	"Adams", "Babbage", "Burleson-Sheisskopf", "Codd", "Date", "Ellison", "Fowler", "Fulton", "Gates", "Grant", "Hopper", "Hugh",
			"Issacson", "Joy", "Gates", "Kernighan", "Knuth", "Kyte", "Lewis", "Lovelace", "Millsap", "Moore", "Morrison",
			"Nooney", "Oscar", "Peters", "Potter", "Quayle", "Ritchie", "Rivest", "Schmidt", "Stevens", "Stroustrup", "Thompson",
			"Torvalis", "Wahl", "Ziv" };

	private static final String[] firstNames = new String[] { //
	"Ada", "Bill", "Bjarne", "Brian", "Cary", "Charles", "Chris", "Dennis", "Donald", "Edgar", "Eric", "Frank", "Grace", 
			"Jim", "Jonathon", "Kathy", "Larry", "Linus", "Martin", "Peter", "Steve", "Tanel", "Toon" };

	private long seed = 817481717376092L;

	private Random random = new Random();

	private boolean initted = false;

	private int quantityToGenerate = 50;

	public int getQuantityToGenerate() {
		return quantityToGenerate;
	}

	public void setQuantityToGenerate(int quantityToGenerate) {
		this.quantityToGenerate = quantityToGenerate;
	}

	public void init() {
		if (!initted) {
			random.setSeed(seed);
		}
	}

	public Salesperson getSalesperson() {
		Salesperson sp = new Salesperson();

		int lastNameIndex = random.nextInt(lastNames.length);
		sp.setLastName(lastNames[lastNameIndex]);

		int firstNameIndex = random.nextInt(firstNames.length);
		sp.setFirstName(firstNames[firstNameIndex]);
		sp.setDisplayName(sp.getLastName() + ", " + sp.getFirstName());

		salesPeople.add(sp);
		return sp;
	}

	public Salesperson getRandomSalesperson() {
		if (salesPeople.size() == 0) {
			throw new IllegalStateException("No salespersons have been generated");
		}
		int index = (int) (Math.random() * salesPeople.size());
		return salesPeople.get(index);
	}

	public List<Salesperson> getSalesPeople() {
		if (salesPeople.size() == 0) {
			populateSalespeople();
		}
		return salesPeople;
	}

	public List<Salesperson> populateSalespeople() {
		for (int i = 0; i < quantityToGenerate; i++) {
			getSalesperson();
		}
		return getSalesPeople();
	}
}
