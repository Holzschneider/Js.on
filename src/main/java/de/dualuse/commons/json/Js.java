package de.dualuse.commons.json;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;


public abstract class Js implements Iterable<Object>, Cloneable, Comparable<Js>, JsProxy {
	public static class UndefinedException extends ClassCastException {
		private static final long serialVersionUID = 1L;
	}
	
	@Retention(RetentionPolicy.RUNTIME) public static
	@interface Key {
		String value();
	}
	
	@Retention(RetentionPolicy.RUNTIME) public static
	@interface Keys {
	}
	
	public static final JsBasicType UNDEFINED = new JsBasicType() {
		@Override public int toInt() { return 0; }
		@Override public double toDouble() { return Double.NaN; }
		@Override public String toString() { return "undefined"; }
		@Override public boolean toBoolean() { return false; }
		public StringBuilder stringify(StringBuilder sb) { return sb.append("undefined"); };
		@Override public Js to(JsonWriter w) throws IOException { return this; }
		public Js clone() { return this; };
	};
	
	public static final JsBasicType NULL = new JsBasicType() {
		@Override public int toInt() { return 0; }
		@Override public double toDouble() { return 0; }
		@Override public String toString() { return "null"; }
		@Override public boolean toBoolean() { return false; }
		public StringBuilder stringify(StringBuilder sb) { return sb.append("null"); };
//		public String toString() { return asString(); };
		public Js clone() { return this; };
		@Override public Js to(JsonWriter w) throws IOException {
			w.nullValue();
			return this;
		}
	};
	
	public static Js on(Object o) {
		if (o == null)
			return NULL;
		
		if (o instanceof Js) return (Js)o;
		if (o instanceof JsProxy) return ((JsProxy)o).asJs(); 
		if (o instanceof String) return on((String)o);
		if (o instanceof Number) return on(((Number)o).doubleValue());
		if (o instanceof Boolean) return on(((Boolean)o).booleanValue());
		if (o instanceof Object[]) return on((Object[])o);
		if (o instanceof byte[]) return on((byte[])o);
		if (o instanceof short[]) return on((short[])o);
		if (o instanceof char[]) return on((char[])o);
		if (o instanceof int[]) return on((int[])o);
		if (o instanceof double[]) return on((double[])o);
		if (o instanceof long[]) return on((long[])o);
		if (o instanceof boolean[]) return on((boolean[])o);
		if (o instanceof float[]) return on((float[])o);
			
		return new JsObjectWrapper(o);
	}
	
	public static Js on(Js j) { return j; }
	public static JsBasicType on(String s) { return new JsString(s); }
	public static JsBasicType on(double d) { return new JsNumber(d); }
	public static JsBasicType on(boolean b) { return new JsBoolean(b); }
	
	public static Js on() { return new JsObject(); }
	
	public static Js on(Object... array) { return new JsArrayWrapper.Generic(array,0,array.length); }
	public static Js on(byte[] array) { return new JsArrayWrapper.Byte(array,0,array.length); }
	public static Js on(short[] array) { return new JsArrayWrapper.Short(array,0,array.length); }
	public static Js on(char[] array) { return new JsArrayWrapper.Char(array,0,array.length); }
	public static Js on(int[] array) { return new JsArrayWrapper.Int(array,0,array.length); }
	public static Js on(double[] array) { return new JsArrayWrapper.Double(array,0,array.length); }
	public static Js on(long[] array) { return new JsArrayWrapper.Long(array,0,array.length); }
	public static Js on(float[] array) { return new JsArrayWrapper.Float(array,0,array.length); }
	public static Js on(boolean[] array) { return new JsArrayWrapper.Boolean(array,0,array.length); }
	public static Js on(String[] array) { return new JsArrayWrapper.Generic(array,0,array.length); }
	
	// DEEP COPY
	public static Js from(Js j) {
		Js k = null;
		if (j instanceof JsArray) k = new JsArray();
		else if (j instanceof JsObject) k = new JsObject();
		else if (j instanceof JsNumber) return new JsNumber( (JsNumber) j );
		else if (j instanceof JsBoolean) return new JsBoolean( (JsBoolean) j );
		else if (j instanceof JsString) return new JsString( (JsString) j );
		
		for (Object key: j) 
			k.put(key, from(j.get(key)));
			
		return k; 
	} 
	
	public static JsBasicType from(String s) { return on(s); }
	public static JsBasicType from(double d) { return on(d); }
	public static JsBasicType from(boolean b) { return on(b); }

	public static Js from(byte[] array) { return new JsArray(array); }
	public static Js from(short[] array) { return new JsArray(array); }
	public static Js from(char[] array) { return new JsArray(array); }
	public static Js from(int[] array) { return new JsArray(array); }
	public static Js from(double[] array) { return new JsArray(array); }
	public static Js from(long[] array) { return new JsArray(array); }
	public static Js from(float[] array) { return new JsArray(array); }
	public static Js from(boolean[] array) { return new JsArray(array); }
	public static Js from(String[] array) { return new JsArray(array); }
	public static Js from(Object... array) { return new JsArray(array); }
	
	public static Js from(Object o) {
		if (o == null)
			return NULL;
		
		if (o instanceof Js) return (Js)o;
		if (o instanceof JsProxy) return ((JsProxy)o).asJs(); 
		if (o instanceof String) return from((String)o);
		if (o instanceof Number) return from(((Number)o).doubleValue());
		if (o instanceof Boolean) return from(((Boolean)o).booleanValue());
		if (o instanceof Object[]) return from((Object[])o);
		if (o instanceof byte[]) return from((byte[])o);
		if (o instanceof short[]) return from((short[])o);
		if (o instanceof char[]) return from((char[])o);
		if (o instanceof int[]) return from((int[])o);
		if (o instanceof double[]) return from((double[])o);
		if (o instanceof long[]) return from((long[])o);
		if (o instanceof boolean[]) return from((boolean[])o);
		if (o instanceof float[]) return from((float[])o);
			
		return from(Js.on(o)); //Deep! Copy!
	}
	//////////////////////////
	
	public static Js from(JsonReader jr) throws IOException {
		switch (jr.peek()) {
		case NULL: jr.skipValue(); return Js.NULL; 
		case STRING: return new JsString(jr.nextString());
		case NUMBER: return new JsNumber(jr.nextDouble());
		case BOOLEAN: return new JsBoolean(jr.nextBoolean());
		
		case BEGIN_ARRAY:
			jr.beginArray();
			JsArray a = new JsArray();
			for (int i=0;(jr.peek()!=JsonToken.END_ARRAY);i++)
				a.put(i, from(jr) );
			
			jr.endArray();
			return a;

		case BEGIN_OBJECT:
			jr.beginObject();
			JsObject o = new JsObject();
			while (jr.peek()!=JsonToken.END_OBJECT)
				o.put( jr.nextName(), from(jr) );
			
			jr.endObject();
			return o;

		default:
			return UNDEFINED;
		}
	}
	
	
	abstract public Js to(JsonWriter w) throws IOException;
	
	//////////////////////////
	
	public Js get(JsBasicType key) { return UNDEFINED; }
	public Js get(Object key) { return get(Js.on(key).asKey()); } 
	public Js get(String key) { return get(Js.on(key).asKey()); }
	public Js get(double key) { return get(Js.on(key).asKey()); }
	public Js get(boolean key) { return get(Js.on(key).asKey()); }
	
	
	public Js put(JsBasicType key, Js value) { return this; }

	public Js put(Object key, Object value) { return put(Js.on(key).asKey(), Js.on(value)); }
	public Js put(String key, Object value) { return put(Js.on(key).asKey(), Js.on(value)); }
	public Js put(double key, Object value) { return put(Js.on(key).asKey(), Js.on(value)); }
	public Js put(boolean key, Object value) { return put(Js.on(key).asKey(), Js.on(value)); }
	
	public Js put(Object key, double value) { return put(Js.on(key).asKey(), Js.on(value)); }
	public Js put(String key, double value) { return put(Js.on(key).asKey(), Js.on(value)); }
	public Js put(double key, double value) { return put(Js.on(key).asKey(), Js.on(value)); }
	public Js put(boolean key, double value) { return put(Js.on(key).asKey(), Js.on(value)); }
	
	public Js put(Object key, boolean value) { return put(Js.on(key).asKey(), Js.on(value)); }
	public Js put(double key, boolean value) { return put(Js.on(key).asKey(), Js.on(value)); }
	public Js put(boolean key, boolean value) { return put(Js.on(key).asKey(), Js.on(value)); }
	public Js put(String key, boolean value) { return put(Js.on(key).asKey(), Js.on(value)); }
	
	public Js put(Object key, String value) { return put(Js.on(key).asKey(), Js.on(value)); }
	public Js put(double key, String value) { return put(Js.on(key).asKey(), Js.on(value)); }
	public Js put(boolean key, String value) { return put(Js.on(key).asKey(), Js.on(value)); }
	public Js put(String key, String value) { return put(Js.on(key).asKey(), Js.on(value)); }
	
	public Js delete(JsBasicType key) { return this; }
	public Js delete(Object key) { return delete(Js.on(key).asKey()); } 
	public Js delete(String key) { return delete(Js.on(key).asKey()); }
	public Js delete(double key) { return delete(Js.on(key).asKey()); }
	public Js delete(boolean key) { return delete(Js.on(key).asKey()); }
	
	public Js clear() { return this; }
	
	abstract public Iterator<Object> iterator();
//	abstract public Js keys(); // MUSS REIN!
	
	public Js shift() { throw new UnsupportedOperationException(); }
	public Js unshift(Js value) { throw new UnsupportedOperationException(); }
	public Js unshift(Object value) { return unshift(Js.on(value)); }
	public Js unshift(String value) { return unshift(Js.on(value)); }
	public Js unshift(double value) { return unshift(Js.on(value)); }
	public Js unshift(boolean value) { return unshift(Js.on(value)); }
	
	public Js pop() { throw new UnsupportedOperationException(); }
	public Js push(Js value) { throw new UnsupportedOperationException(); }
	public Js push(Object value) { return push(Js.on(value)); }
	public Js push(String value) { return push(Js.on(value)); }
	public Js push(double value) { return push(Js.on(value)); }
	public Js push(boolean value) { return push(Js.on(value)); }
	

	public int indexOf(Js value) { throw new UnsupportedOperationException(); }
	public int indexOf(Object value) { return indexOf(Js.on(value)); }
	public int indexOf(String value) { return indexOf(Js.on(value)); }
	public int indexOf(double value) { return indexOf(Js.on(value)); }
	public int indexOf(boolean value) { return indexOf(Js.on(value)); }

	public int lastIndexOf(Js value) { throw new UnsupportedOperationException(); }
	public int lastIndexOf(Object value) { return lastIndexOf(Js.on(value)); }
	public int lastIndexOf(String value) { return lastIndexOf(Js.on(value)); }
	public int lastIndexOf(double value) { return lastIndexOf(Js.on(value)); }
	public int lastIndexOf(boolean value) { return lastIndexOf(Js.on(value)); }
	
	
	public String join(String delim) { throw new UnsupportedOperationException(); }
	public Js concat(Js other) { throw new UnsupportedOperationException(); }
	public Js concat(Object... other) { throw new UnsupportedOperationException(); }
	
	public Js splice(int offset, int length) { throw new UnsupportedOperationException(); }
	public Js splice(int offset, int length, double... elements) { throw new UnsupportedOperationException(); }
	public Js splice(int offset, int length, boolean... elements) { throw new UnsupportedOperationException(); }
	public Js splice(int offset, int length, String... elements) { throw new UnsupportedOperationException(); }
	public Js splice(int offset, int length, Object... elements) { throw new UnsupportedOperationException(); }
	public Js splice(int offset, int length, Js... elements) { throw new UnsupportedOperationException(); }
	
	public Js slice(int start, int end) { throw new UnsupportedOperationException(); }
	public Js slice(int start) { return slice(start, length()); }
	
	public Js sort() { throw new UnsupportedOperationException(); }
	public Js sort(Comparator<Js> c) { throw new UnsupportedOperationException(); }
	
	public Js reverse() { throw new UnsupportedOperationException(); }
	
	abstract public Js clone();
	
	public int length() { return 0; }
	
	public Class<? extends Js> typeof() { return Js.class; }
	
	
	protected JsBasicType asKey() { return new JsString(toString()); }
	public int toInt() { throw new UnsupportedOperationException(); }
	public double toLong() { return (long) toDouble(); }
	public double toFloat() { return (float) toDouble(); }
	public double toDouble() { throw new UnsupportedOperationException(); }
	public String toString() { throw new UnsupportedOperationException(); }
	public boolean toBoolean() { throw new UnsupportedOperationException(); }
	
	public<T> T to(Class<T> type) {
		if (type==String.class) return type.cast( toString() );
		if (this==UNDEFINED) throw new UndefinedException();
		throw new ClassCastException("Unable to convert "+this.getClass()+" to "+type);
	}
	
	public Js asJs() {
		return this;
	}
	
	public<T> T[] keys(T[] array) { return Arrays.copyOf(array, 0); }
	
	public<T> T[] to(T[] array) { throw new UnsupportedOperationException(); }
	
	public<T> T to(Object target) {
		//store elements contained in this Js object to target object
		throw new UnsupportedOperationException();
	}

	public<S,T> java.util.Map<S, T> asMap(final Class<T> keyType, final Class<T> valueType) {
		throw new UnsupportedOperationException();
	}
	
	public<T> java.util.List<T> asList(final Class<T> elementType) {
		return new List<T>() {
				@Override public boolean add(T a) { Js.this.push(a); return true; }
				@Override public void add(int i, T a) { Js.this.splice(i, 0, Js.on(a)); }
				@Override public boolean addAll(Collection<? extends T> a) { for (T e: a)  this.add(e); return true; }
				@Override public boolean addAll(int i, Collection<? extends T> as) { for (T e: as) this.add(i++,e); return false; }
				@Override public void clear() { Js.this.clear(); }
				@Override public boolean contains(Object a) { return indexOf(a)>=0; }
				@Override public boolean containsAll(Collection<?> c) { for (Object e: c) if (!contains(e)) return false; return true; }
				@Override public T get(int i) { return Js.this.get(i).as(elementType); }
				@Override public int indexOf(Object o) { return Js.this.indexOf(o); }
				@Override public boolean isEmpty() { return size()==0; }

				@Override public int lastIndexOf(Object o) { return Js.this.lastIndexOf(o); }
				
				@Override public Iterator<T> iterator() { return null; }
				@Override public ListIterator<T> listIterator() { return null; }
				@Override public ListIterator<T> listIterator(int i) { return null; }
				
				@Override public boolean remove(Object o) { int i = indexOf(o); if (i>=0 && i<=size()) Js.this.delete(i);  return true; }
				@Override public T remove(int i) { T o = Js.this.get(i).as(elementType); Js.this.delete(i); return o; }
				
				@Override public boolean removeAll(Collection<?> c) { boolean removed = false; for (Object o: c) removed = remove(o) | removed; return removed; }
				@Override public boolean retainAll(Collection<?> c) 
				{ boolean removed = false; for (int i=0,I=Js.this.length();i<I;i++) 	if (!c.contains(Js.this.get(i))) { remove(i--); removed = true; } return removed; }
				
				@Override public T set(int i, T o) { T t = Js.this.get(i).as(elementType); Js.this.put(i, o); return t; }
				@Override public int size() { return Js.this.length(); }

				@Override public List<T> subList(int arg0, int arg1) {
					return null;
				}
				
				@Override public Object[] toArray() { return Js.this.as(Object[].class); }
				
				@SuppressWarnings("unchecked") 
				@Override public <Q> Q[] toArray(Q[] a) {
					if (a.length!=Js.this.length())
						return toArray(Arrays.copyOf(a, Js.this.length()));
					
					for (int i=0;i<a.length;i++)
						a[i] = (Q)this.get(i);
					
					return a;
				}
			};
	};
	
	
	@SuppressWarnings("unchecked") 
	public<T> T as(final Class<T> type) {
		return (T) java.lang.reflect.Proxy.newProxyInstance(Js.class.getClassLoader(), new Class<?>[] { type, JsProxy.class }, new InvocationHandler() {
//		return (T) java.lang.reflect.Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type, JsProxy.class }, new InvocationHandler() {
			
			Class<?> iterableType = null; {
				int i=0;
				for (Class<?> c: type.getInterfaces())
					if (!c.equals(Iterable.class)) i++;
					else iterableType = (Class<?>)(((ParameterizedType)type.getGenericInterfaces()[i]).getActualTypeArguments()[0]);
			}
			
			@Override public Object invoke(Object proxy, final Method method, Object[] args) throws Throwable {
				Class<?>[] p = method.getParameterTypes();
				String key = method.isAnnotationPresent(Key.class)?method.getAnnotation(Key.class).value():method.getName();

				final Class<?> rtype = method.getReturnType();
				if (p.length==0) {
					if (method.getName().equals("iterator") && Iterable.class.isAssignableFrom(type))
						return new Iterator<Object>() {
							Iterator<Object> keyIterator = iterator();
							@Override public boolean hasNext() { return keyIterator.hasNext(); }
							@Override public Object next() { return ((JsBasicType)keyIterator.next()).as(iterableType); }
							@Override public void remove() { keyIterator.remove(); }
						};
					
					if (key.startsWith("get"))
						key = Character.toLowerCase(key.charAt(3))+key.substring(4);
					
					Js value = get(key);
					
					if (value!=UNDEFINED) 
						if (rtype.isAssignableFrom(Js.class)) return value;
						else return value.as(rtype);
					else
						if (method.getDeclaringClass()==Object.class || method.getDeclaringClass()==JsProxy.class) 
							return method.invoke( Js.this, args );
						else
							if (Js.class.equals(rtype)) return value;
					
				} else
				if (p.length==1)
					if (key.equals("get")) {
						Js value = get(args[0]); 
						if (value!=UNDEFINED) 
							if (rtype.isAssignableFrom(Js.class)) return value;
							else return value.as(rtype);
					} else {
						if (key.startsWith("set"))
							key = Character.toLowerCase(key.charAt(3))+key.substring(4);
						
						put(key, args[0]);
						return null;
					}
				else
				if (p.length == 2) 
					if (key.equals("put")) {
						put(args[0],args[1]);
						return null;
					}
				
				if (method.getDeclaringClass()==Object.class || method.getDeclaringClass()==JsProxy.class) 
					return method.invoke( Js.this, args );
				else 
					if (!rtype.equals(Void.class))
						return Js.UNDEFINED.as(rtype);

				
				
				throw new IllegalAccessError();
			}
		});
	}
	
	
	//////////////////////////////////////////////////////////////////////
	
	static boolean isPositiveInteger(String s) {
		int digits = 0;
		for (int i=s.charAt(0)=='+'?1:0,I=s.length();i<I;i++,digits++)
			if ("0123456789".indexOf(s.charAt(i))==-1)
				return false;
		
		return digits>0;
	}
	
	
	public static int idealByteArraySize(int need) {
		for (int i = 4; i < 32; i++)
			if (need <= (1 << i) - 12)
				return (1 << i) - 12;

		return need;

	}
	
	public static int idealIntArraySize(int need) {
		return idealByteArraySize(need * 4) / 4;
	}
	
	abstract public StringBuilder stringify(StringBuilder sb);// { return sb; }
	public String stringify() { return stringify(new StringBuilder()).toString(); }
	
	///////////
	
	static public class on extends JsObjectWrapper { } //Unnecessary Syntactic (poisoned) Sugar? time will tell! 
	
	
	@Override public int compareTo(Js o) {
		return 0;
	}
	
	static interface Test extends Iterable<String> {
		String get(String value);
		void put(Integer key, String value);
	}
	
	public static void main(String[] args) throws IOException {
		Js j =  new Js.on();
		Test t = j.as(Test.class);
		
		t.put(1234, "welt");
		t.put(1337, "dada");
		
		System.out.println(j.stringify());
		for (String o: t)
			System.out.println(o+" -> "+t.get(o));
		
		
//		new Js.on(){}.asList(elementType);
		
//		System.out.println( ((Class<?>)((ParameterizedType)StringIterator.class.getGenericInterfaces()[0]).getActualTypeArguments()[0]) );
//		for (Type t: Bla.class.getGenericInterfaces())
//			for (Type q: ((ParameterizedType)t).getActualTypeArguments())
//				System.out.println(q);
//		System.out.println( a.equals(b) );
//			Bla.class.getG
		
//		for (Class<?> c: Bla.class.getInterfaces()) {
//			System.out.println(c);
////			for (TypeVariable<?> tp: c.getTypeParameters())
////				System.out.println(Arrays.asList(tp.getBounds()));
//		}
		
		
		
		
//		System.out.println("JS-Test");
//		
////		Js j = new JsNumber(10);
////		j.put("hallo", 10);
////		System.out.println(j.get("hallo"));
////		Js k = j.get("hallo");
////		System.out.println(k.toDouble()+1);
////		System.out.println( Js.UNDEFINED == k ); 
//		
//		
//		Js l = new JsObject();
//		
//		l.put("true", "richtig");
//		l.put("10.3", "zehnkommadrei");
//		l.put(10.0, "zehn");
//		
//		System.out.println(l.stringify());
//		
//		System.out.println( l.get(true) );
//		System.out.println( l.get(10) );
//		System.out.println( l.get(10.3) );
//		
//		System.out.println("--------------------------");
//		
//		Js a = new JsArray();
//		
//		a.put(10, "hall\"{}\"o");
//		a.put("true", false);
//		
//		System.out.println(a.stringify());
//		for (int i=0;i<10;i++)
//			a.put(i,"i"+i);
//		
////		a = a.slice(1, -1).concat("bla",1,2,3).splice(10, 20,"hallo"); // BUG!! not implemented ? wtf?
//		
//		System.out.println( a.stringify() );
//		System.out.println( a.get(100).toInt()+10 );
//		
//		
//		////////////////
//
//		class Person {
//			public String vorname = "John";
//			
//			protected String secret = "Appleseed";
//			public void setName(String name) { this.secret = name; }
//			public String getName() { return this.secret; }
//			
//			public int alter = 1234;
//		}
//		
//		Js u = Js.from(new Person()); // geht das echt? oder benutzt er intern .as() ?
//		// kritisch weil, u soll ja keine Referenz mehr auf die neue Person haben, nachdem from() fertig ist!
//		
//		System.out.println( "deep copy: "+u.stringify() +" @ "+u.getClass());
//		
//		Js x = Js.from(1); // BUG!! sollte doch ne Number sein, oder?
//		Js y = Js.from(new boolean[] { true,true,false,true } );
//		
//		Js str = Js.from("hallo"); 
//		
//		System.out.println(x+" @ "+x.getClass());
//		System.out.println(str+" @ "+str.getClass());
//		
//		Js z = Js.from(new JsonReader(new StringReader("{\"hallo\":\"welt\"}")));
//		
//		
//		System.out.print("---");
//		JsonWriter jw = new JsonWriter(new OutputStreamWriter(System.out));
//		z.to(jw);
//		jw.flush();
//		System.out.println("---");
//
//		
//		////////////////////////////////
//		
//		
//
//		int[] values = new int[] { 1,2,3,4,5 };
//		Js r = Js.on(values);
//		
//		System.out.println( Arrays.toString(values) );
//		r.put(3, "1337");
//		System.out.println( Arrays.toString(values) );
//		
//		
//		
//		Person p = new Person();
//		Js t = Js.on(p);
//		
//		System.out.println(p.secret);
//		t.put("name", new String[] { "bla","blub" });
//		System.out.println("'"+p.secret+"'");
//		t.put("names", new String[] { "bla","blub" });
//		System.out.println(t.get("names"));
//		
//		
//		
//		Akte f = t.as(Akte.class);
//		
//		System.out.println( f.getVorname() );
//		double res = dot( Js.on(new Point2D.Double(1.5,10.2)).as(VectorF.class) , Js.on(new Point(30,10)).as(VectorF.class) );
//		System.out.println(res);
//		
//		
//		
//		class Gemeinschaft {
//			public String name = "Berliner LeetCoderz";
//			
//			public Person[] mitglieder = { new Person(), new Person() };
//		}
//		
//		
//		System.out.println( "Alter: "+Js.on(new Gemeinschaft()).get("mitglieder").get(0).get("alter") );
//		
//		
//		System.out.println( Js.from( new JsonReader(new StringReader("{\"name\":\"Theodor\"}"))).as(Akte.class).getName() );
//		
//		Js s = Js.on( new Object() {
//			String name="hallo";
//			
//			Object bla = new Object() {
//				String key = "value";
//				int[] values = { 1,2,3,4,5 };
//			};
//			
//		});
//		
//		
//		System.out.println(s.stringify());
//		
//		
//		
//		for (Object k: s) 
//			System.out.println(s.get(k).stringify());
//		
//		
//		
//		
//		
////		System.out.println( Arrays.toString(Arrays.asList(1,2,3,4,5).toArray(new Object[2])) );
//		Js j = new Js.on() {
//			float x = 10;
//			String y = "11";
//			
//			Person sibling = new Person();
//			Object o = new Object() {
//			};
//		};
//		
//		Js k = new Js.on() {
//			String x = "10";
//			float y = 11.0f;
//		};
		
//		System.out.println(j.stringify());
//		
//		System.out.println( k.equals(j) );
//		
//		Test s = j.as(Test.class);
//		Test t = k.as(Test.class);
		
//		System.out.println( ((JsProxy)t).asJs().getClass() );
		
//		s.equals(t);
//		System.out.println(s.equals(t));
////		System.out.println(s.stringify());
//
//		System.out.println(  Js.on(new Point2D.Double(20,21)).as(Vector.class).getY() );
		
		
//		Js j = new Js.on() {
//			float x = 10;
//			String y = "11";
//		};
//		
//		Js k = Js.on(1,2,3,"hallo",5);
//		
//		k.splice(2, 1);
//		
//		System.out.println( k );
		
		
		
		
//		System.out.println(Js.from("{\"hallo\":\"welt\", \"mah\":[1,2,3,4,5]}").stringify());
		
//		Js k = Js.from( new JsonReader(new StringReader("{\"a\":100,\"b\":\"hallo\"}")) {{ setLenient(true); }} );
//		
////		JsonReader jr = new JsonReader(new StringReader("10")) {{ setLenient(true); }};
////		System.out.println( jr.peek() );
//		
//		System.out.println(k.stringify());
		
	}
	
	/*
	public static void main(String[] args) {
				
////		System.out.println( new Double(10).equals("10") );
//		
////		Js j = new JsArray();
//////		j.name(1).value("welt");
////		j.put(1, "hallo");
////		Js k = Js.of(1,2,3,4,5);
//		
//		Js j = new JsMap();
//		
//		j.put(9.1, "welt");
//		Js k = j.get("9.1");
//		j.put(10.0, "hallo");
//		System.out.println( k.asString() );
//		
//		System.out.println(j);
//		
//		System.out.println();
//		
//		
////		System.out.println( Js.of(true).hashCode()==Boolean.TRUE.hashCode() );
//		
//		Js m = Js.on(1,2,3,4,"hallo");
//		m.put(8, "welt");
//		m.put("möp","miep");
//		System.out.println( m );
//		
//		for (Object key: m)
//			System.out.println(key);
//		
//		j.put(1,m);
//		
//		System.out.println(j);

		
		
		
		Js e = Js.on( 
			new Object() {
				float x = 10;
				private float getY() { return 11 ; }
			}
		);
		
		Js f = Js.on( new Object[0] );
		
		f.put("4", "grr");
		System.out.println(f.stringify());
		
//		System.out.println(e);
//		e.put ("z", 30);
//		System.out.println(e);
		
		
		Js k = Js.on(new Object());
		k.put("hallo", 1);
		
		System.out.println(k.stringify());

		k.put("welt", new Object() {
			int x = 10;
			int y = 20;
		});
		
		System.out.println(k.stringify());
		
		
		Js r = Js.on("hallo");
		String v = r.toString();
		System.out.println(v);
		
//		Js u = Js.on(new Object() {
//			float x = 10;
//			float y = 20;
//		});
//		
//		u.put("z", 30);
//		System.out.println(u);
		
		
//		Object o = new Object() {
//			float x = 10;
//			float y = 10;
//		};
//		
//		Js m = Js.on( o );
//		
//		System.out.println(m);
//		m.put("x", 11);
//		System.out.println(m);
		
//		for (Object key: Js.on(new Person()))
//			System.out.println(key);
//
//		System.out.println( Js.on(new Person()) );
		
		
//		Js l =  Js.on( new Person() );
//		System.out.println( l.get("name") );
//		l.put("name", 123);
//		System.out.println( l.get("name") );
//		System.out.println( l );
//		
//		new Js() {
//			float x = 10;
//			float y = 20;
//		};

//		HashMap<String,String> dictionary = new HashMap<String, String>();
//		
//		dictionary.put("hallo", "welt");
//		
//		System.out.println( Js.on( dictionary ) );
		
		
		
		
	}

	
	
	

	/////
	interface Account {
		
		String getName();
		int getAlter();
		
	};
	
	
	
	
	
	
	
	
	static public class Kugel {
		public float radius = 100;
		public void setName(String name) {}
		public String getName() { return "wtf"; }
		
	}
	
	static public class Farbe {
		public int rgba = 0xFF00FFFF;
	}
	
	
	
	
	static public class Person {
		
		public int alter = 99;
		
		private String blabla = "bubu";
		public String getName() {
			return blabla;
		}
		
		public void setName(String name) { this.blabla = name; }
		
		Farbe getFarbe() { return new Farbe(); };
		public Kugel k = null;
		
	}
	
	
*/	
	
	
	
	static interface VectorF {
		float getX();
		float getY();
	}
	

	public static double dot( VectorF a, VectorF b ) {
		return a.getX()*b.getX()+a.getY()*b.getY();
	}
	
	
	
	
	
}

interface Akte {
	
	String getName();
	String getVorname();
	
	String getAlter();
	
}










