package serp.util;


import java.util.*;


/**
 *	<p>Map that keeps items in order from most to least recently used.
 *	Performance is similar to that of a {@link TreeMap}.</p>
 *
 *	<p>Operations on the map's entry and key sets do not affect 
 *	ordering.</p>
 *
 *	<p>This implementation is not synchronized.</p>
 *
 *	@author		Abe White
 */
public class LRUMap
	implements SortedMap
{
	private Map 	_orders	= new HashMap ();
	private TreeMap	_values	= new TreeMap ();
	private int 	_order	= Integer.MAX_VALUE;


	@Override
    public Comparator comparator ()
	{
		return null;
	}


	@Override
    public Object firstKey ()
	{
		return ((OrderKey) _values.firstKey ()).key;
	}


	@Override
    public Object lastKey ()
	{
		return ((OrderKey) _values.lastKey ()).key;
	}


	/**
	 *	Not supported.
	 */
	@Override
    public SortedMap headMap (Object toKey)
	{
		throw new UnsupportedOperationException ();
	}


	/**
	 *	Not supported.
	 */
	@Override
    public SortedMap subMap (Object fromKey, Object toKey)
	{
		throw new UnsupportedOperationException ();
	}


	/**
	 *	Not supported.
	 */
	@Override
    public SortedMap tailMap (Object fromKey)
	{
		throw new UnsupportedOperationException ();
	}


	@Override
    public void clear ()
	{
		_orders.clear ();
		_values.clear ();
	}


	@Override
    public boolean containsKey (Object key)
	{
		return _orders.containsKey (key);
	}


	@Override
    public boolean containsValue (Object value)
	{
		return _values.containsValue (value);
	}


	@Override
    public Set entrySet ()
	{
		return new EntrySet ();
	}


	@Override
    public boolean equals (Object other)
	{
		if (other == this)
			return true;
		if (!(other instanceof Map))
			return false;

		// easy way to compare on mappings
		return new HashMap (this).equals (other);
	}


	@Override
    public Object get (Object key)
	{
		Object order = _orders.remove (key);
		if (order == null)
			return null;

		// get old value and re-cache as lowest order
		Object value = _values.remove (order); 
		order = nextOrderKey (key);
		_orders.put (key, order);
		_values.put (order, value);

		return value;
	}


	@Override
    public boolean isEmpty ()
	{
		return _orders.isEmpty ();
	}


	@Override
    public Set keySet ()
	{
		return new KeySet ();
	}


	@Override
    public Object put (Object key, Object value)
	{
		Object order = nextOrderKey (key);
		Object oldOrder = _orders.put (key, order);

		Object rem = null;
		if (oldOrder != null)
			rem = _values.remove (oldOrder);

		_values.put (order, value);
		return rem;
	}


	@Override
    public void putAll (Map map)
	{
		Map.Entry entry;
		for (Iterator itr = map.entrySet ().iterator (); itr.hasNext ();)
		{
			entry = (Map.Entry) itr.next ();
			put (entry.getKey (), entry.getValue ());
		}		
	}


	@Override
    public Object remove (Object key)
	{
		Object order = _orders.remove (key);
		if (order != null)
			return _values.remove (order);
		return null;
	}


	@Override
    public int size ()
	{
		return _orders.size ();
	}


	@Override
    public Collection values ()
	{
		return new ValueCollection ();
	}


	@Override
    public String toString ()
	{
		return entrySet ().toString ();
	}


	/**
 	 *	Return the next key to use in the values map.
 	 */
	private synchronized OrderKey nextOrderKey (Object key)
	{
		OrderKey ok = new OrderKey ();
		ok.key = key;
		ok.order = _order--;
		return ok;
	}


	/**
	 *	Keys used in the map from orders to values.
	 */
	private static final class OrderKey
		implements Comparable
	{
		public Object 	key		= null;
		public int		order	= 0;


		@Override
        public int compareTo (Object other)
		{
			return order - ((OrderKey) other).order;
		}
	}


	/**
	 *	View of a single map entry.
	 */
	private static final class MapEntry
		implements Map.Entry
	{
		private Map.Entry _valuesEntry = null;


		public MapEntry (Map.Entry valuesEntry)
		{
			_valuesEntry = valuesEntry;
		}


		@Override
        public Object getKey ()
		{
			OrderKey ok = (OrderKey) _valuesEntry.getKey ();
			return ok.key;
		}


		@Override
        public Object getValue ()
		{
			return _valuesEntry.getValue ();
		}


		@Override
        public Object setValue (Object value)
		{
			return _valuesEntry.setValue (value);
		}


		@Override
        public boolean equals (Object other)
		{
			if (other == this)
				return true;
			if (!(other instanceof Map.Entry))
				return false;

			Object key = getKey ();
			Object key2 = ((Map.Entry) other).getKey ();
			if ((key == null && key2 != null)
				|| (key != null && !key.equals (key2)))
				return false;

			Object val = getValue ();
			Object val2 = ((Map.Entry) other).getValue ();
			return (val == null && val2 == null)
				|| (val != null && val2.equals (val2));
		}
	}


	/**
	 *	View of the entry set.
	 */
	private class EntrySet
		extends AbstractSet
	{
		@Override
        public int size ()
		{
			return LRUMap.this.size ();
		}
	
	
		@Override
        public boolean add (Object o)
		{
			Map.Entry entry = (Map.Entry) o;
			put (entry.getKey (), entry.getValue ());
			return true;
		}
	
	
		@Override
        public Iterator iterator ()
		{
			final Iterator valuesItr = _values.entrySet ().iterator ();

			return new Iterator ()
			{
				private MapEntry _last = null;


				@Override
                public boolean hasNext ()
				{
					return valuesItr.hasNext ();
				}

	
				@Override
                public Object next ()
				{
					_last = new MapEntry ((Map.Entry) valuesItr.next ());
					return _last;
				}


				@Override
                public void remove ()
				{
					valuesItr.remove ();
					_orders.remove (_last.getKey ());
				}
			};
		}
	}


	/**
	 *	View of the key set.
	 */
	private class KeySet
		extends AbstractSet
	{
		@Override
        public int size ()
		{
			return LRUMap.this.size ();
		}
	
	
		@Override
        public Iterator iterator ()
		{
			final Iterator keysItr = _values.keySet ().iterator ();

			return new Iterator ()
			{
				private Object _last = null;


				@Override
                public boolean hasNext ()
				{
					return keysItr.hasNext ();
				}

				
				@Override
                public Object next ()
				{
					_last = ((OrderKey) keysItr.next ()).key;
					return _last;
				} 


				@Override
                public void remove ()
				{
					keysItr.remove ();
					_orders.remove (_last);
				}
			};
		}
	}


	/**
	 *	View of the value collection.
	 */
	private class ValueCollection
		extends AbstractCollection
	{
		@Override
        public int size ()
		{
			return LRUMap.this.size ();
		}
	
	
		@Override
        public Iterator iterator ()
		{
			final Iterator valuesItr = _values.entrySet ().iterator ();

			return new Iterator ()
			{
				private Object _last = null;


				@Override
                public boolean hasNext ()
				{
					return valuesItr.hasNext ();
				}


				@Override
                public Object next ()
				{
					Map.Entry entry = (Map.Entry) valuesItr.next ();
					_last = ((OrderKey) entry.getKey ()).key;
					return entry.getValue ();
				}

	
				@Override
                public void remove ()
				{
					valuesItr.remove ();
					_orders.remove (_last);
				}
			};
		}
	}
}
