package tyRuBa.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author cburns
 *
 * Special Iterator class that iterates over a pair of iterators.
 * I'm sure you can see the possibilities this opens, beyond just adding
 * two Iterators together.
 */
public class IteratorPair implements Iterator {
	Iterator iterators[];
	int which;
	
	public IteratorPair(Iterator carIt, Iterator cdrIt) {
		iterators = new Iterator[] { carIt, cdrIt };
		
		which = 0;
	}

	@Override
    public boolean hasNext() {
		if(which < 2) {
			if(iterators[which] != null && iterators[which].hasNext()) {
				return true;
			} else {
				which++;
				return hasNext();
			}
		} else {
			return false;
		}
	}

	@Override
    public Object next() {
		if(hasNext()) {
			return iterators[which].next();
		} else {
			throw new NoSuchElementException();
		}
	}

	@Override
    public void remove() {
		throw new UnsupportedOperationException(); //maybe implement this later
	}
}
