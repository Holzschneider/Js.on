package de.dualuse.commons.json;

import java.util.Iterator;

public abstract class JsBasicType extends Js {
	public Class<? extends Js> typeof() { return JsBasicType.class; }
	
	public Class<?> valueType() { return Js.class; }
	
	@Override public JsBasicType asKey() { return this; }
	
	public java.util.Iterator<Object> iterator() {
		return new Iterator<Object>() {
			@Override public boolean hasNext() { return false; }
			@Override public Object next() { return null; }
			@Override public void remove() {}
		};
	};
		
	public<T> T as(final Class<T> type) {
		return to(type);
	}
		
	@SuppressWarnings("unchecked") 
	@Override public <T> T to(Class<T> type) {
		if (type==Byte.class || type==byte.class) return (T)( (Byte) (byte)toInt() );
		if (type==Short.class || type==short.class) return (T)( (Short)(short)toInt() );
		if (type==Character.class || type==char.class) return (T)( (Character) (char)toInt() );
		if (type==Integer.class || type==int.class) return (T)( (Integer)(int)toInt() );
		if (type==Float.class || type==float.class) return (T)( (Float)(float)toDouble() );
		if (type==Double.class || type==double.class) return (T)( (Double) (double)toDouble() );
		if (type==Boolean.class || type==boolean.class) return (T)( (Boolean) (boolean)toBoolean() );
		if (type==Number.class) return type.cast( Double.valueOf(toDouble()) );
		if (type.isAssignableFrom(String.class)) return type.cast(this);
		
		return super.to(type);
	}
	
}


