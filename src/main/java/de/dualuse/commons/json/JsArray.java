package de.dualuse.commons.json;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import com.google.gson.stream.JsonWriter;

class JsArray extends JsObject {
	private int[] keys = new int[0];
	private Js[] values = new Js[0]; 
	private int size = 0;
	
	public Class<? extends Js> typeof() { return JsArray.class; }
	
	JsArray() { this(Js.idealIntArraySize(0)); }
	JsArray(JsArray copy) {
		super(copy);
		this.keys = copy.keys.clone();
		this.values = copy.values.clone();
		this.size = copy.size;
	}
	
	
	private JsArray(int cap) { keys = new int[cap]; values = new Js[cap]; }
	public JsArray(byte[] array) { this(array,0,array.length); }

	public JsArray(short[] array) { this(array,0,array.length); }
	public JsArray(char[] array) { this(array,0,array.length); }
	public JsArray(int[] array) { this(array,0,array.length); }
	public JsArray(long[] array) { this(array,0,array.length); }
	public JsArray(float[] array) { this(array,0,array.length); }
	public JsArray(double[] array) { this(array,0,array.length); }
	public JsArray(boolean[] array) { this(array,0,array.length); }
	public JsArray(Object[] array) { this(array,0,array.length); }
	
	public JsArray(byte[] array, int offset, int length) { this(length); for (int i=0,I=length;i<I;i++) put(i, Js.on(array[offset+i])); }
	public JsArray(short[] array, int offset, int length) { this(length); for (int i=0,I=length;i<I;i++) put(i, Js.on(array[offset+i])); }
	public JsArray(char[] array, int offset, int length) { this(length); for (int i=0,I=length;i<I;i++) put(i, Js.on(array[offset+i])); }
	public JsArray(int[] array, int offset, int length) { this(length); for (int i=0,I=length;i<I;i++) put(i, Js.on(array[offset+i])); }
	public JsArray(long[] array, int offset, int length) { this(length); for (int i=0,I=length;i<I;i++) put(i, Js.on(array[offset+i])); }
	public JsArray(float[] array, int offset, int length) { this(length); for (int i=0,I=length;i<I;i++) put(i, Js.on(array[offset+i])); }
	public JsArray(double[] array, int offset, int length) { this(length); for (int i=0,I=length;i<I;i++) put(i, Js.on(array[offset+i])); }
	public JsArray(boolean[] array, int offset, int length) { this(length); for (int i=0,I=length;i<I;i++) put(i, Js.on(array[offset+i])); }
	public JsArray(Object[] array, int offset, int length) { this(length); for (int i=0,I=length;i<I;i++) put(i, Js.on(array[offset+i])); }
	
	@Override public int toInt() { return 0; }
	@Override public double toDouble() { return Double.NaN;	}
	@Override public boolean toBoolean() { return true; }
	
	@Override public <T> T to(Class<T> type) {
		if (type == byte[].class) { byte[] data = new byte[this.length()]; for (int i=0,I=length();i<I;i++) data[i] = (byte)get(i).toInt(); return type.cast(data);}
		if (type == short[].class) { short[] data = new short[this.length()]; for (int i=0,I=length();i<I;i++) data[i] = (short)get(i).toInt();  return type.cast(data);}
		if (type == char[].class) { char[] data = new char[this.length()]; for (int i=0,I=length();i<I;i++) data[i] = (char)get(i).toInt(); return type.cast(data); }
		if (type == int[].class) { int[] data = new int[this.length()]; for (int i=0,I=length();i<I;i++) data[i] = (int)get(i).toInt(); return type.cast(data); }
		if (type == long[].class) { long[] data = new long[this.length()]; for (int i=0,I=length();i<I;i++) data[i] = (long)get(i).toDouble(); return type.cast(data); }
		if (type == float[].class) { float[] data = new float[this.length()]; for (int i=0,I=length();i<I;i++) data[i] = (float)get(i).toDouble(); return type.cast(data); }
		if (type == double[].class) { double[] data = new double[this.length()]; for (int i=0,I=length();i<I;i++) data[i] = (double)get(i).toDouble(); return type.cast(data); }
		if (type == boolean[].class) { boolean[] data = new boolean[this.length()]; for (int i=0,I=length();i<I;i++) data[i] = (boolean)get(i).toBoolean(); return type.cast(data); }
		
		if (type.isArray()) {
			Class<?> t = type.getComponentType();
			Object[] array = (Object[]) Array.newInstance(t, length());
			if (Js.class.isAssignableFrom(t))
				for (int i=0,I=array.length;i<I;i++)
					array[i] = get(i); 
			else
				for (int i=0,I=array.length;i<I;i++)
					array[i] = get(i).as(t);
					
			return type.cast(array);
		}

		return super.to(type);
	}
	
	@Override public <T> T[] to(T[] array) {
		Class<?> t = array.getClass().getComponentType();
		if (array.length<length())
			array = Arrays.copyOf(array, length());
		
		Object[] objectArray = array;
		
		if (Js.class.isAssignableFrom(t))
			for (int i=0,I=array.length;i<I;i++)
				objectArray[i] = get(i); 
		else
			for (int i=0,I=array.length;i<I;i++)
				objectArray[i] = get(i).as(t);
				
		return array;
	}
	

	
	public Js clear() { for (int i=0;i<size;i++) values[i] = null; size = 0; return this; }
	
	@Override public <T> T as(Class<T> type) {
		if (type.isArray()) 
			return to(type);
		return super.as(type);
	}
	
	
	@Override public Iterator<Object> iterator() {
		final Iterator<Object> second = super.iterator();
		return new Iterator<Object>() {
			int i=0;
			
			@Override public void remove() {}
			@Override public boolean hasNext() {
				return i<size || second.hasNext();
			}

			@Override public Object next() {
				return i<size?keys[i++]:second.next();
			}
		};
	}
	
	
	@Override public Js get(JsBasicType key) {
		if (key.typeof()==JsNumber.class) {
			double d = key.toDouble();
			if ((int)d == d)
				return get((int)d);
		} else
		if (key.typeof()==JsString.class && isPositiveInteger(key.toString())) 
			return get(Integer.parseInt(key.toString()));
		
		
		return super.get(key);
	}
	
	@Override public Js get(double key) {
		double d = key;
		if ((int)d == d)
			return get((int)d);

//		a[3.00000001] = 11
//		a
//		[1, 2, 10]
//		a[3.000000000000000000001] = 11
//		11
//		a
//		[1, 2, 10, 11]
		
		return super.get(key);
	}

	@Override public Js put(double key, boolean value) {
		if ((int)key != key) return super.put(key,value);
		else return this.put((int)key, Js.on(value));
	}

	@Override public Js put(double key, double value) {
		if ((int)key != key) return super.put(key,value);
		else return this.put((int)key, Js.on(value));
	}

	@Override public Js put(double key, String value) {
		if ((int)key != key) return super.put(key,value);
		else return this.put((int)key, Js.on(value));
	}

	@Override public Js put(double key, Object value) {
		if ((int)key != key) return super.put(key,value);
		else return this.put((int)key, Js.on(value));
	}

	@Override public Js delete(double key) {
		if ((int)key != key) return super.delete(key);
		else return this.delete((int)key);
	}
	
	@Override public Js put(JsBasicType key, Js value) {
		if (key.typeof()==JsNumber.class) {
			double d = key.toDouble();
			if ((int)d == d)
				return put((int)d, value);
			
		} else
			if (key.typeof()==JsString.class && isPositiveInteger(key.toString())) 
				return put(Integer.parseInt(key.toString()), value);	
		
		return super.put(key, value);
	}
	
	
	//////////////////
	
	protected Js get(int key) {
		int index = Arrays.binarySearch(keys, 0, size, key);
		if (index>=0 && index<size) 
			return values[index];
		else
			return Js.UNDEFINED;
	}
	
	protected Js put(int key, Js value) {
		int index = Arrays.binarySearch(keys, 0, size, key);
		if (index>=0 && index<size) {
			Js old = values[index];
			keys[ index ] = key;
			values[ index ] = value;
			return old;
		} else {
			if (size==keys.length) {
				int newLength = idealIntArraySize(size+1);
				keys = Arrays.copyOf(keys, newLength);
				values = Arrays.copyOf(values, newLength);
			}
			
			for (int i=size-1,I=-index-1;i>=I;i--) {
				keys[i+1] = keys[i];
				values[i+1] = values[i];
			}
			
			keys[ -index-1 ] = key;
			values[ -index-1 ] = value;
			size++;
		}
		
		return this;
	}
	
	protected Js delete(int key) {
		int index = Arrays.binarySearch(keys, 0, size, key);
		if (index>=0 && index<size) 
			values[index] = UNDEFINED;
		
		return this;
	}
	
	
	@Override public int length() {
		if (size==0) return 0;
		return keys[size-1]+1;
	}
	
	@Override public JsBasicType asKey() {
		return new JsString(this.toString());
	}
	
	@Override public StringBuilder stringify(StringBuilder sb) {
		sb.append("[");
		toStringBuilder(sb);
		sb.append("]");
		return sb;
	}
	
	
	@Override public Js to(JsonWriter w) throws IOException {
		w.beginArray();
		
		for (int i=0,j=0;i<size;i++,j++) {
			 for (int k=j;k<keys[i];k++,j++)
				 w.nullValue();
			 
			 values[i].to(w);
		}
		
		w.endArray();
		
		return this;
	}
	
	
	@Override public String toString() {
		return toStringBuilder(new StringBuilder()).toString();
	}

	
	protected StringBuilder toStringBuilder(StringBuilder sb) {
		for (int i=0,j=0;i<size;i++,j++) {
			 for (int k=j;k<keys[i];k++,j++)
				 sb.append(j==0?"":",").append(UNDEFINED);
			 
			 sb.append(j==0?"":",");
			 values[i].stringify(sb);
		}
		return sb;
	}
	
	
	
	private static int BASE_ARRAY_HASHCODE = "Array".hashCode(); 
	
	@Override public int hashCode() {
		int code = BASE_ARRAY_HASHCODE;
				
		for (Object key: this) 
			code += get(key).hashCode();
		
		return code;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof JsProxy) 
			obj = ((JsProxy)obj).asJs();
		
		if (!(obj instanceof JsArray)) 
			if (obj instanceof Js) return false;
			else return equals(Js.on(obj));

		int i=0, l=length();
		for (Object key: (Js)obj ) {
			i++;
			Js a = this.get(key);
			Js b = ((Js)obj).get(key);
			
			
			if (!a.equals( b ))
				return false;
		}
		
		return i==l;
	}
	
	
	/////////////////////////////////////////////////////////////////
	
	
	public Js shift() { 
		Js element = values[0];
		
		for (int i=0;i<size-1;i++) {
			keys[i] = keys[i+1]-1;
			values[i] = values[i+1];
		}
		
		values[--size] = UNDEFINED;
	
		return element;
	}
	
	
	public Js unshift(Js value) {
		if (size==keys.length) {
			int newLength = idealIntArraySize(size+1);
			keys = Arrays.copyOf(keys, newLength);
			values = Arrays.copyOf(values, newLength);
		}
		
		for (int i=size-1,I=0;i>=I;i--) {
			keys[i+1] = keys[i]+1;
			values[i+1] = values[i];
		}
		
		keys[0] = 0;
		values[0] = value;
	
		size++;
		
		return this;
	}
	
	
	public Js pop() { 
		Js v = values[--size];
		values[size] = null;
		
		return v;
	}
	
	public Js push(Js value) { 
		if (size==keys.length) {
			int newLength = idealIntArraySize(size+1);
			keys = Arrays.copyOf(keys, newLength);
			values = Arrays.copyOf(values, newLength);
		}
		
		keys[size] = size==0?0:keys[size-1]+1; 
		values[size] = value;
		
		size++;
		
		return this;
	}
	

	@Override public String join(String delim) {
		StringBuilder sb = new StringBuilder();
		for (int i=0,I=length();i<I;i++) {
			if (i>0) sb.append(delim);
			sb.append(get(i));
		}
		return sb.toString();
	}
	
	@Override public Js concat(Js other) {
		JsArray n = new JsArray(this);
		
		for (int i=0,I=other.length(),j=n.length();i<I;i++,j++)
			n.put(j, other.get(i));
		
		return n;
	}
	
	@Override public Js concat(Object... others) {
		JsArray n = new JsArray(this);
		
		for (Object o: others) 
			if (!(o instanceof JsArray)) n.put(n.length(), o);
			else for (int i=0,I=((JsArray)o).length(),j=n.length();i<I;i++,j++)
					n.put(j, ((JsArray)o).get(i));
		
		return n;
	}

	
	public Js splice(int offset, int length) {
		int index = Arrays.binarySearch(keys, 0, size, offset);
		
		index = index<0?-index:index;
		
		JsArray cutout = new JsArray(length); 
				
		if (index<size) {
			
			int i=index, start = offset, end = start+length;
			for (int j=0; i<size && keys[i]<end;i++,j++)
				cutout.put(j, values[i]);
			
			int cut = i-index;
			for (int j=i;j<size-cut;j++) {
				keys[j] = keys[j+cut]-length;
				values[j] = values[j+cut];
			}
			
			size-=cut;
		}
			
		return cutout;
	}
	
	public Js splice(int offset, int length, double... doubles) { 
		Js elements[] = new Js[doubles.length];
		for (int i=0,I=elements.length;i<I;i++) elements[i] = Js.from(doubles[i]);
		return splice(offset, length, elements);
	}

	public Js splice(int offset, int length, boolean... booleans) {
		Js elements[] = new Js[booleans.length];
		for (int i=0,I=elements.length;i<I;i++) elements[i] = Js.from(booleans[i]);
		return splice(offset, length, elements);
	}

	public Js splice(int offset, int length, String... strings) {
		Js elements[] = new Js[strings.length];
		for (int i=0,I=elements.length;i<I;i++) elements[i] = Js.from(strings[i]);
		return splice(offset, length, elements);
	}
	
	public Js splice(int offset, int length, Object... objects) {
		Js elements[] = new Js[objects.length];
		for (int i=0,I=elements.length;i<I;i++) elements[i] = Js.from(objects[i]);
		return splice(offset, length, elements);
	}

	public Js splice(int start, int length, Js... elements) {
		
		int end = start+length;
		
		JsArray a = new JsArray();
		
		if (start<0) start = length()+start;
		if (end<0) end = length()+end;
		
		int indexStart = Arrays.binarySearch(keys, 0, size, start);
		int indexEnd = Arrays.binarySearch(keys, 0, size, end);
		
		if (indexStart<0) indexStart = -indexStart-1;
		if (indexEnd<0) indexEnd = -indexEnd-1;
		
		for (int i=indexStart,offset=keys[i];i<indexEnd;i++)
			a.put( keys[i]-offset, values[i] );
		
		
		int required = size-(indexEnd-indexStart)+elements.length, hole = indexEnd-indexStart;
		if (required>keys.length) {
			int newLength = idealIntArraySize(required);
			keys = Arrays.copyOf(keys, newLength);
			values = Arrays.copyOf(values, newLength);
		}
		
		if (hole<elements.length)
			for (int i=required-1,delta = (elements.length-hole),j = i-delta;j>=indexEnd;i--,j--) {
				keys[i] = keys[j]+delta;
				values[i] = values[j];
			}
		else
		if (hole>elements.length) {
			for (int i=indexStart+elements.length,delta = (hole-elements.length),j = i+delta;j<size;i++,j++) {
				keys[i] = keys[j]-delta;
				values[i] = values[j];
			}
			for (int i=required;i<size;i++)
				values[i] = null;
		}
		
		for (int i=indexStart,j=0,J=elements.length,k=keys[i];j<J;i++,j++,k++) {
			keys[i] = k;
			values[i] = elements[j];
		}
		
		size += elements.length-hole; 
		
		return a;
		
		/*
		int index = Arrays.binarySearch(keys, 0, size, offset);
		index = index<0?-index:index;

		JsArray cutout = new JsArray(length); 
				
		if (index<size) {
			
			int i=index, start = offset, end = start+length;
			for (int j=0; i<size && keys[i]<end;i++,j++)
				cutout.put(j, values[i]);

			if (length<elements.length) {
				int newLength = idealIntArraySize(size+elements.length-length);
				keys = Arrays.copyOf(keys, newLength);
				values = Arrays.copyOf(values, newLength);
			}
			
			int cut = i-index, paste = elements.length;
			if (paste<cut) //XXX garantiert buggie
				for (int j=i,k=j+paste-cut,l=j+cut;j<size-cut;j++,k++,l++) {
					keys[k] = keys[l]-length+paste;
					values[k] = values[l];
				}
			else
				for (int j=size-cut-1,k=j+paste-cut+(size-cut-i),l=j+cut+(size-cut-i);j>=i;j--,k--,l--) {
					keys[k] = keys[l]-length+paste;
					values[k] = values[l];
				}
			
			for (int j=i,k=0;k<paste;k++,j++) {
				keys[j] = offset+k;
				values[j] = elements[k];
			}
			
			size-=cut;
			size+=paste;
		}
		
			
		return cutout;
		*/
		
	}
	
	
	public Js slice(int start, int end) { 
		JsArray a = new JsArray();
		
		if (start<0) start = length()+start;
		if (end<0) end = length()+end;
		
		int indexStart = Arrays.binarySearch(keys, 0, size, start);
		int indexEnd = Arrays.binarySearch(keys, 0, size, end);
		
		if (indexStart<0) indexStart = -indexStart-1;
		if (indexEnd<0) indexEnd = -indexEnd-1;
		
		for (int i=indexStart,offset=keys[i];i<indexEnd;i++)
			a.put( keys[i]-offset, values[i] );
		
		return a;
	}
	
	
	public Js sort() { 
		int length = length();
		
		quicksort(values,0, size-1);
		for (int i=0;i<size;i++)
			keys[i] = i;
		
		put(length-1, UNDEFINED);
		
		return this;
	}
	
	
	public Js sort(Comparator<Js> c) { 
		int length = length();
		
		quicksort(values, 0, size-1, c);
		for (int i=0;i<size;i++)
			keys[i] = i;
		
		put(length-1, UNDEFINED);
		
		return this;
	}
	
	private void reverse(int i, int key, Js value) {
		if (i==size) size = 0; 
		else reverse(i+1, keys[size-1]-keys[i], values[i]);
		
		put(key, value);
	}
	
	public Js reverse() { 
		reverse(0, keys[size-1]-keys[0], values[0]);
		return this; 
	}
	
	private static <T> void quicksort(T[] elements, int low, int high, Comparator<? super T> c) {
		int i = low, j = high;
		T pivot = elements[(low + high) / 2];

		while (i <= j) {
			while (c.compare(elements[i],pivot)<0) i++;
			while (c.compare(elements[j],pivot)>0) j--;
			if (!(i <= j)) continue;
			T t = elements[i];
			elements[i]=elements[j];
			elements[j]=t;
			i++;
			j--;
		}
		if (low < j) quicksort(elements,low,j,c);
		if (i < high) quicksort(elements,i,high,c);
	}
	
	public static <T extends Comparable<? super T>> void quicksort(T[] elements, int low, int high) {
		int i = low, j = high;
		T pivot = elements[(low + high) / 2];

		while (i <= j) {
			while (elements[i].compareTo(pivot)<0) i++;
			while (elements[j].compareTo(pivot)>0) j--;

			if (!(i <= j)) continue;
			T t = elements[i];
			elements[i]=elements[j];
			elements[j]=t;
			i++;
			j--;
		}

		if (low < j) quicksort(elements,low,j);
		if (i < high) quicksort(elements,i,high);
	}
	
	
	
	
	public static void main(String[] args) {
		
		System.out.println( Arrays.toString( Js.on( 1,2,3).put(10, 10).to(Object[].class) ) );
		
		Js j = Js.on(1,2,3,4,5);
		j = j.concat( Js.on("x","y","z"), false  ); // works
		System.out.println(j.slice(4, 10).join(":"));

//		j = Js.on("x","y","z");
//		j.splice(1, 1,"hallo");
		
////		int I = j.length();
////		for (int i=0;i<I;i++)
////			System.out.println(j.get(i).toInt()+" vs "+(i+1));
////			Assert.assertTrue(j.get(i).asInt()==i+1);
//
//		System.out.println(j);
//		j.pop();
//		System.out.println(j);
//		j.push(6);
//		System.out.println(j);
//		j.shift();
//		System.out.println(j);
//		j.unshift(0);
//		System.out.println(j); 
//		j.unshift(-1);
//		System.out.println(j); 
//		
//		j.delete(j.length()-2);
//		System.out.println(j);
//		
//		j = j.concat( Js.on("a","b","c") );
//		System.out.println(j);
		
		j = j.concat( Js.on("x","y","z"), false  ); // works
		System.out.println(j.join(":"));

		j = j.slice(-10,-3);
		System.out.println(j.stringify());
		
		
//		j = Js.on(1,2,3).put(10, "ende");
		System.out.println(j);
		
		
		j.splice(2, 0, Js.on("hallo"), Js.on("welt")); // Works (?)
		System.out.println(j);
//		if (true)
//			return;
		
//		System.out.println(j);
//		j.sort();
//		j.concat( Js.on("x","y","z", Js.on(1,2,3), "Ende" ) ); //UNIMPLEMENTED
		
	}
}

