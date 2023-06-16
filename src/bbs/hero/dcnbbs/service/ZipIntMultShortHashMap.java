package hero.dcnbbs.service;

public class ZipIntMultShortHashMap
{
	
//	/** The default capacity is 16 */
//	public static final int DEFAULT_INITIAL_CAPACITY = 16;
	/** The default load factor is 75 (=75%), so the HashMap is increased when 75% of it's capacity is reached */ 
	public static final int DEFAULT_LOAD_FACTOR = 75;
	
	public static final int SUB_ELEMENT_SIZE =3;
	
	private final int loadFactor;	
	private Element[] buckets;
	private final boolean isPowerOfTwo;
	private int size;

	/*/**
	 * Creates a new HashMap with the default initial capacity 16 and a load factor of 75%. 
	 */
	/*public ZipIntMultShortHashMap() {
		this( DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR );
	}*/
	
	/**
	 * Creates a new HashMap with the specified initial capacity.
	 * 
	 * @param initialCapacity the initial number of elements that this map can hold without needing to 
	 *        increase it's internal size.
	 */
	public ZipIntMultShortHashMap(int initialCapacity ) {
		this( initialCapacity, DEFAULT_LOAD_FACTOR );
	}

	/**
	 * Creates a new HashMap with the specified initial capacity and the specified load factor.
	 * 
	 * @param initialCapacity the initial number of elements that this map can hold without needing to 
	 *        increase it's internal size.
	 * @param loadFactor the loadfactor in percent, a number between 0 and 100. When the loadfactor is 100,
	 *        the size of this map is only increased after all slots have been filled. 
	 */
	public ZipIntMultShortHashMap(int initialCapacity, int loadFactor) {
		initialCapacity = (initialCapacity * 100) / loadFactor;
		// check if initial capacity is a power of 2:
		int capacity = 1;
		while (initialCapacity > capacity) {
			capacity <<= 1;
		}
		this.isPowerOfTwo = (capacity == initialCapacity);
		//System.out.println("isPowerOfTwo: " + this.isPowerOfTwo );
		this.buckets = new Element[ initialCapacity ];
		this.loadFactor = loadFactor;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public boolean put( int key, short value ) {

		if ( (this.size * 100) / this.buckets.length > this.loadFactor ) {
			increaseSize();
		}
		
		int index;
		if (this.isPowerOfTwo) {
			index = (key & 0x7FFFFFFF) & (this.buckets.length - 1);
		} else {
			index = (key & 0x7FFFFFFF) % this.buckets.length;
		}
		Element element = this.buckets[ index ];
		if (element == null) {
			// new List
			// new element
			element = new Element( key, new short[SUB_ELEMENT_SIZE] );
			element.values[0]=value;
			element.size++; //==1
			
			this.buckets[index] = element;
			this.size++;
			return true;
		} else {
			// add a value to the array
			Element lastElement = element;
			do {
				if (element.key == key ) {
					// element found!
					element.size++;
					// add data
					if(element.size==element.values.length){
						// increase the value array
						short[] newValues = new short[element.values.length*2];//TODO constant
						System.arraycopy(element.values, 0, newValues, 0, element.values.length);
						element.values=newValues;
					}
					element.values[element.size-1]=value;
					
					return true;
				}
				lastElement = element;
				element = element.next;
			} while ( element != null );
			
			// insert new element at the end since no other was found
			
			// new element
			element = new Element( key, new short[SUB_ELEMENT_SIZE] );
			element.values[0]=value;
			element.size++; //==1
			
			this.buckets[index] = element;
			this.size++;
			lastElement.next = element;
			return true;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#get(java.lang.Object)
	 */
	public Element get( int key ) {
		int index;
		if (this.isPowerOfTwo) {
			index = (key & 0x7FFFFFFF) & (this.buckets.length - 1);
		} else {
			index = (key & 0x7FFFFFFF) % this.buckets.length;
		}
		Element element = this.buckets[ index ];
		if (element == null) {
			return null;
		}
		do {
			if (element.key == key ) {
				return element;
			}
			element = element.next;
		} while (element != null);
		return null;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#remove(java.lang.Object)
	 */
	public short remove( int key ) {
		int index;
		if (this.isPowerOfTwo) {
			index = (key & 0x7FFFFFFF) & (this.buckets.length - 1);
		} else {
			index = (key & 0x7FFFFFFF) % this.buckets.length;
		}
		Element element = this.buckets[ index ];
		if (element == null) {
			//System.out.println("remove: No bucket found for key " + key + ", containsKey()=" + containsKey(key));
			return -1;
		}
		Element lastElement = null;
		do {
			if (element.key == key ) {
				if (lastElement == null) {
					this.buckets[ index ] = element.next;
				} else {
					lastElement.next = element.next;
				}
				this.size--;
				return 1;//element.values;
			}
			lastElement = element;
			element = element.next;
		} while (element != null);
		//System.out.println("No element found for key " + key + ", containsKey()=" + containsKey(key));
		return -1;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		return (this.size == 0);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#size()
	 */
	public int size() {
		return this.size;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey( int key ) {
		return get( key ) != null;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#containsValue(java.lang.Object)
	 */
	/*public boolean containsValue( short value ) {
		for (int i = 0; i < this.buckets.length; i++) {
			Element element = this.buckets[i];
			while (element != null) {
				if (element.values == value ) {
					return true;
				}
				element = element.next;
			}
		}
		return false;
	}*/
	
	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#clear()
	 */
	public void clear() {
		for (int i = 0; i < this.buckets.length; i++) {
			this.buckets[i] = null;
		}
		this.size = 0;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#values()
	 */
	/*public short[] values() {
		return values( new short[ this.size ] );
	}*/

	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#values(java.lang.Object[])
	 */
	/*public short[] values(short[] objects) {
		int index = 0;
		for (int i = 0; i < this.buckets.length; i++) {
			Element element = this.buckets[i];
			while (element != null) {
				objects[index] = element.values;
				index++;
				element = element.next;
			}
		}
		return objects;
	}
*/
	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#keys()
	 */
	public int[] keys() {
		int[] keys = new int[ this.size ];
		int index = 0;
		for (int i = 0; i < this.buckets.length; i++) {
			Element element = this.buckets[i];
			while (element != null) {
				keys[index] = element.key;
				index++;
				element = element.next;
			}
		}
		return keys;
	}
	
	/**
	 * Returns String containing the String representations of all objects of this map.
	 * 
	 * @return the stored elements in a String representation.
	 */
	/*public String toString() {
		StringBuffer buffer = new StringBuffer( this.size * 23 );
		buffer.append( super.toString() ).append( "{\n" );
		short[] values = values();
		for (int i = 0; i < values.length; i++) {
			buffer.append( values[i] );
			buffer.append('\n');
		}
		buffer.append('}');
		return buffer.toString();
	}*/

	
	/**
	 * Increaases the internal capacity of this map.
	 */
	private void increaseSize() {
		int newCapacity;
		if (this.isPowerOfTwo) {
			newCapacity = this.buckets.length << 1; // * 2
		} else {
			newCapacity = (this.buckets.length << 1) - 1; // * 2 - 1 
		}
		Element[] newBuckets = new Element[ newCapacity ];
		for (int i = 0; i < this.buckets.length; i++) {
			Element element = this.buckets[i];
			while (element != null) {				
				int index;
				if (this.isPowerOfTwo) {
					index = (element.key & 0x7FFFFFFF) & (newCapacity - 1);
				} else {
					index = (element.key & 0x7FFFFFFF) % newCapacity;
				}
				Element newElement = newBuckets[ index ];
				if (newElement == null ) {
					newBuckets[ index ] = element;
				} else {
					// add element at the end of the bucket:
					while (newElement.next != null) {
						newElement = newElement.next;
					}
					newElement.next = element;
					
				}
				Element lastElement = element;
				element = element.next;
				lastElement.next = null;
			}
		}
		this.buckets = newBuckets;
	}
	
	public static final class Element {
		public final int key;
		public short[] values;
		public short size; // TODO undefined == value<0
		public Element next;
		public Element ( int key, short[] value ) {
			this.key = key;
			this.values = value;
		}
	}

}
