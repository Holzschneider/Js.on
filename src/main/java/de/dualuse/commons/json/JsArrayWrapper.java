package de.dualuse.commons.json;

import java.util.Arrays;

//TODO: Alle array operationen an das native-Array Durchreichen!

public abstract class JsArrayWrapper extends JsArray {
	protected final Object array;
	protected final int offset, length; 
	
	protected JsArrayWrapper(byte[] array, int offset, int length) { super(array,offset,length); this.array = array; this.offset = offset; this.length = length; }
	protected JsArrayWrapper(short[] array, int offset, int length) { super(array,offset,length); this.array = array; this.offset = offset; this.length = length; }
	protected JsArrayWrapper(char[] array, int offset, int length) { super(array,offset,length); this.array = array; this.offset = offset; this.length = length; }
	protected JsArrayWrapper(int[] array, int offset, int length) { super(array,offset,length); this.array = array; this.offset = offset; this.length = length; }
	protected JsArrayWrapper(float[] array, int offset, int length) { super(array,offset,length); this.array = array; this.offset = offset; this.length = length; }
	protected JsArrayWrapper(double[] array, int offset, int length) { super(array,offset,length); this.array = array; this.offset = offset; this.length = length; }
	protected JsArrayWrapper(long[] array, int offset, int length) { super(array,offset,length); this.array = array; this.offset = offset; this.length = length; }
	protected JsArrayWrapper(boolean[] array, int offset, int length) { super(array,offset,length); this.array = array; this.offset = offset; this.length = length; }
	protected JsArrayWrapper(Object[] array, int offset, int length) { super(array,offset,length); this.array = array; this.offset = offset; this.length = length; }
	
	//////////////////////
	public Class<?> valueType() { return array.getClass(); }

	abstract protected void set(int index, Js value);

	protected void cut(int index, int num) { 
		System.arraycopy(array, index+num, array, index, length-(index+num));
		for (int i=length-num;i<length;i++)
			set(i,UNDEFINED); 
	};

	protected void paste(int index, int num) {
		System.arraycopy(array, index, array, index+num, length-(index+num));
		for (int i=index;i<num;i++)
			set(i,UNDEFINED);
	};

	//////////////////////
	
	@Override protected Js put(int key, Js value) {
		if (key<length) set(key, value);
		return super.put(key, value);
	}
	
	@Override protected Js delete(int key) {
		cut(key,1);
		return super.delete(key);
	}
	
	@Override public Js push(Js value) {
		if (length()<length) set(length()-1,value);
		return super.push(value);
	}	
	
	
	//////////////////////
	
	static class Byte extends JsArrayWrapper {
		public Byte(byte[] array, int offset, int length) { super(array,offset,length); }
		@Override protected void set(int index, Js value) { ((byte[])array)[index] = (byte)value.toInt(); }
	}
	
	static class Short extends JsArrayWrapper {
		public Short(short[] array, int offset, int length) { super(array,offset,length); }
		@Override protected void set(int index, Js value) { ((short[])array)[index] = (short)value.toInt(); }
	}
	
	static class Char extends JsArrayWrapper {
		public Char(char[] array, int offset, int length) { super(array,offset,length); }
		@Override protected void set(int index, Js value) { ((char[])array)[index] = (char)value.toInt(); }
	}
	
	static class Int extends JsArrayWrapper {
		public Int(int[] array, int offset, int length) { super(array,offset,length); }
		@Override protected void set(int index, Js value) { ((int[])array)[index] = (int)value.toInt(); }
	}
	
	static class Long extends JsArrayWrapper {
		public Long(long[] array, int offset, int length) { super(array,offset,length); }
		@Override protected void set(int index, Js value) { ((long[])array)[index] = (long)value.toLong(); }
	}
	
	static class Float extends JsArrayWrapper {
		public Float(float[] array, int offset, int length) { super(array,offset,length); }
		@Override protected void set(int index, Js value) { ((float[])array)[index] = (float)value.toFloat(); }
	}
	
	static class Double extends JsArrayWrapper {
		public Double(double[] array, int offset, int length) { super(array,offset,length); }
		@Override protected void set(int index, Js value) { ((double[])array)[index] = (double)value.toDouble(); }
	}

	static class Boolean extends JsArrayWrapper {
		public Boolean(boolean[] array, int offset, int length) { super(array,offset,length); }
		@Override protected void set(int index, Js value) { ((boolean[])array)[index] = (boolean)value.toBoolean(); }
	}
	
	static class Generic extends JsArrayWrapper {
		Class<?> componentType;
		public Generic(Object[] array, int offset, int length) { super(array,offset,length); this.componentType = array.getClass().getComponentType(); }
		@Override protected void set(int index, Js value) { ((Object[])array)[index] = value.to(componentType); }
	}
	
	
	public static void main(String[] args) {
		
		byte[] ba = new byte[] { 1,2,3,4,5,6,7,8,9,10 };
		
		System.out.println(Arrays.toString(ba));
		
		JsArray b = new JsArrayWrapper.Byte(ba, 0, 10);
//		JsArray b = new JsArray(new byte[] { 1,2,3,4,5,6,7,8,9,10 });
		
//		b.push(11);
//		b.delete(8);
		b.splice(1,1);
		
		System.out.println( b.toString() );
		System.out.println(Arrays.toString(ba));
		
	}

}
