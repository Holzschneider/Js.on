package de.dualuse.commons.json;

import java.io.IOException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.management.RuntimeErrorException;

import com.google.gson.stream.JsonWriter;

class JsObject extends Js {
	protected Map<Object,Js> map = null;
	
	JsObject() {}
	
	JsObject(JsObject copy) { this.map = copy.map; }
	@Override public Js clone() { return new JsObject(this); };
	
	@Override public int toInt() { return 0; }
	@Override public double toDouble() { return Double.NaN;	}
	@Override public boolean toBoolean() { return true; }
	
	@Override public Js delete(JsBasicType key) {
		if (map==null)
			super.delete(key);
		
		map.remove(key);
		
		return this;
	}
	
	@Override public Js get(JsBasicType key) {
		if (map==null)
			return super.get(key);
		
		if (map.containsKey(key))
			return map.get(key);
		
		return super.get(key);
	}
	
	@Override public Js put(JsBasicType key, Js value) {
		if (map==null) 
			map = new LinkedHashMap<Object,Js>();
		
		map.put(key, value);
		return this;
	}
	
	@Override
	public <T> T[] keys(T[] array) {
		Set<Object> keys = map.keySet();
		
		if (array.length<keys.size())
			array = Arrays.copyOf(array, map.keySet().size());
		
		int i = 0;
		for (Object o: keys)
			array[i++] = (T)o;
		
		if (array.length>i)
			array[i]=null;
			
		return array;
	}
	
	@Override public Iterator<Object> iterator() {
		if (map==null) 
			return new Iterator<Object>() {
				@Override public void remove() {}
				@Override public Object next() { return null; }
				@Override public boolean hasNext() { return false; }
			};
		else
			return map.keySet().iterator();
	}
	
	
	
	@Override public StringBuilder stringify(StringBuilder sb) {
		
		sb.append("{");
		int j=0;
		
		if (map!=null) 
			for ( Entry<Object,Js> e: map.entrySet() )
				e.getValue().stringify( 
					sb
					 .append(j++==0?"":",")
					 .append("\"")
					 .append( e.getKey() )
					 .append("\":")
				 );
			
		sb.append("}");
		
		return sb;
	}
	
	@Override public String toString() {
		return "[object Object]";
	}

	private static int BASE_OBJECT_HASHCODE = "[object Object]".hashCode();
	
	@Override public int hashCode() {
		int code = BASE_OBJECT_HASHCODE;
		
		for (Object s: this) 
			code += s.hashCode()+get(s).hashCode();
		
		return code;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof JsProxy) 
			obj = ((JsProxy)obj).asJs();
		
		if (!(obj instanceof JsObject)) 
			if (obj instanceof Js) return false;
			else return equals(Js.on(obj));
		
		int i=0;
		for (Object key: (Js)obj ) {
			i++;
			Js a = this.get(key);
			Js b = ((Js)obj).get(key);
			
			
			if (!a.equals( b ))
				return false;
		}
		
		return (map==null && i==0) || (i==map.size());
	}
	
	@Override public Js to(JsonWriter w) throws IOException {
		w.beginObject();
		
		for ( Entry<Object,Js> e: map.entrySet() )
			if (e.getValue()!=Js.UNDEFINED)
				e.getValue().to( w.name(e.getKey().toString()) );
		
		w.endObject();
		
		return this;
	}

	
	@Override
	public <S, T> Map<S, T> asMap(final Class<T> keyType, final Class<T> valueType) {
		return new java.util.Map<S,T>() {
			@Override public void clear() { JsObject.this.clear(); }
			@Override public boolean containsKey(Object key) { return map!=null && !map.containsKey(key); }
			@Override public boolean containsValue(Object value) { return map!=null && map.containsValue(value); }

			@Override public Set<java.util.Map.Entry<S, T>> entrySet() { throw new UnsupportedOperationException(); }
			@Override public Set<S> keySet() { throw new UnsupportedOperationException(); }

			@Override public T get(Object key) { return JsObject.this.get(key).as(valueType); }
			@Override public boolean isEmpty() { return map==null || map.size()==0; }
			@Override public T put(S key, T value) {
				Js old = JsObject.this.get(key);
				JsObject.this.put(key, value);
				if (old == null) return null;
				else  return old.as(valueType);
			}

			@Override
			public void putAll(Map<? extends S, ? extends T> m) {
				for (Map.Entry<? extends S, ? extends T> e: m.entrySet())
					JsObject.this.put(e.getValue(),e.getKey());
			}

			@Override
			public T remove(Object key) {
				Js old = JsObject.this.get(key);
				JsObject.this.delete(key);
				if (old == null) return null;
				else  return old.as(valueType);
			}

			@Override
			public int size() {
				return map==null?0:map.size();
			}

			@Override
			public Collection<T> values() {
				throw new UnsupportedOperationException();
			}
			
		};
	}
	
	
	
	
	
	
	
	
	public static void main(String[] args) {

		HashMap<Object, String> test = new HashMap<Object, String>();
		
		test.put("hallo", "welt");
		System.out.println(test);
		test.remove(Js.on("hallo"));
		System.out.println(test);
		
		
		System.out.println();test.clear();
		test.put("hallo", "welt");
		System.out.println(test);
		test.put(Js.on("hallo"),"möp");
		System.out.println(test);
		
		System.out.println();test.clear();
		test.put(true, "welt");  
		// Boolean true hat einen anderen .hashCode als Js.on("true").hashCode() == Js.on(true).hashCode == "true".hashCode()
		// -> plain String-Keys möglich, other basictypes nur Js.on(...) gekapselt 
		
		System.out.println(test);
		test.remove(Js.on("true"));
		System.out.println(test);
		
		
		
//		Js j = new Js.on();
//		
//		j = j.put(1.03, "hallo");
//		j = j.put("false","welt");
//		j = j.remove(1.03);
//		String k = j.stringify();
//		
//		System.out.println( k );
		
	}
}








