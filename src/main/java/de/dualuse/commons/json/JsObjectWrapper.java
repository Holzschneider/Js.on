package de.dualuse.commons.json;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class JsObjectWrapper extends JsObject {
	static interface Getter { Object get(Object instance) throws Exception; }
	static interface Setter { 
		void set(Object instance, Js value) throws Exception;
		void set(Object instance, Object value) throws Exception;
	}
	
	static class FieldManipulator implements JsObjectWrapper.Getter, JsObjectWrapper.Setter {
		final Field f;
		final Class<?> type;
		
		public FieldManipulator(Field f) { 
			this.f = f; this.type = f.getType(); 
		}
		@Override public Object get(Object instance) throws Exception { 
			return f.get(instance); 
		}
		@Override public void set(Object instance, Js value) throws Exception { 
			f.set(instance, value.to(type)); // evtl nicht .as() ? 
		}
		@Override public void set(Object instance, Object value) throws Exception {
			 if (type.isAssignableFrom(value.getClass())) 
				 f.set(instance, value);
			 else 
				 set(instance,Js.on(value));
		}
	}
	
	static class MethodGetter implements JsObjectWrapper.Getter {
		final Method m;
		public MethodGetter(Method m) { this.m = m; }
		@Override public Object get(Object instance) throws Exception { return m.invoke(instance); }
	}
	
	static class MethodSetter implements JsObjectWrapper.Setter {
		final Method m;
		final Class<?>[] t;
		public MethodSetter(Method m) { this.m = m; this.t = m.getParameterTypes(); }
		@Override public void set(Object instance, Js value) throws Exception { m.invoke(instance, value.to(t[0])); }
		@Override public void set(Object instance, Object value) throws Exception {
			if (t[0].isAssignableFrom(value.getClass())) m.invoke(instance, value);
			else set(instance, Js.on(value));
		}
	}
	
	
	static Map<Class<?>,Map<String, JsObjectWrapper.Getter>> gettersCache = new Hashtable<Class<?>,Map<String,JsObjectWrapper.Getter>>();
	static Map<Class<?>,Map<String, JsObjectWrapper.Setter>> settersCache = new Hashtable<Class<?>,Map<String,JsObjectWrapper.Setter>>();
	
	private Object value;
	private Map<String,JsObjectWrapper.Getter> getters;
	private Map<String,JsObjectWrapper.Setter> setters;
	
	public Class<?> valueType() { return value.getClass(); }

	
	JsObjectWrapper() { this((Object)null); }
	JsObjectWrapper(JsObjectWrapper copy) { 
		super(copy);
		this.setters = copy.setters;
		this.getters = copy.getters;
		this.value = copy.value;
	}
	
	public Js clone() { return new JsObjectWrapper(this); };
	
	JsObjectWrapper(Object value) {
		if (value == null) this.value = value = this;
		else this.value = value;
		
		Class<?> clazz = value.getClass();
		
		boolean anonymous = clazz.isAnonymousClass();
		
		getters = gettersCache.get(clazz);
		setters = settersCache.get(clazz);
		
		if (getters==null || setters==null) {
			Map<String,JsObjectWrapper.Getter> g = getters = new HashMap<String,JsObjectWrapper.Getter>(); 
			Map<String,JsObjectWrapper.Setter> s = setters = new HashMap<String,JsObjectWrapper.Setter>(); 

			if (anonymous) 
				for (Field f: clazz.getDeclaredFields())
					if (!f.isSynthetic() && !Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers())) {
						f.setAccessible(true);
						String fn = !f.isAnnotationPresent(Key.class)?f.getName():f.getAnnotation(Key.class).value();
						JsObjectWrapper.FieldManipulator fm = new FieldManipulator(f);
						g.put(fn, fm);
						if (!Modifier.isFinal(f.getModifiers()) )
							s.put(fn, fm);
					}
					 
//				for (Class<?> current=clazz;current != Object.class;current = current.getSuperclass())
//					for (Field f: clazz.getDeclaredFields()) 
//						if (f.isAccessible())
			
			for (Class<?> current=clazz;current != Object.class && current != JsObjectWrapper.class;current = current.getSuperclass())
				for (Field f: current.getDeclaredFields())
					if (!f.isSynthetic() && !Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers()) && Modifier.isPublic(f.getModifiers()) || f.isAnnotationPresent(Key.class)) {
						String fn = !f.isAnnotationPresent(Key.class)?f.getName():f.getAnnotation(Key.class).value();
						JsObjectWrapper.FieldManipulator fm = new FieldManipulator(f);
						g.put(fn, fm);
						s.put(fn, fm);
					}
			
			
			for (Class<?> current=clazz;current != Object.class && current != JsObjectWrapper.class;current = current.getSuperclass())
				for (Method m: current.getDeclaredMethods())
					if (Modifier.isPublic(m.getModifiers()) || current==clazz && anonymous)
						if (m.getParameterTypes().length==0)
							if (!m.isAnnotationPresent(Key.class)) {
								m.setAccessible(true);
								String n = m.getName();
								if (n.startsWith("get") && n.length()>3)
									g.put(Character.toLowerCase(n.charAt(3))+n.substring(4), new MethodGetter(m));
							} else {
								m.setAccessible(true);
								g.put(m.getAnnotation(Key.class).value(), new MethodGetter(m));
							}
			
			
			for (Class<?> current=clazz;current != Object.class;current = current.getSuperclass()) 
				for (Method m: current.getDeclaredMethods()) 
					if (Modifier.isPublic(m.getModifiers()) ||  current==clazz && anonymous)
						if (m.getParameterTypes().length==1)
							if (!m.isAnnotationPresent(Key.class)) {
								m.setAccessible(true);
								String n = m.getName();
								if (n.startsWith("set") && n.length()>3) {
									n = Character.toLowerCase(n.charAt(3))+n.substring(4);
									if (getters.containsKey(n))
										s.put(n, new MethodSetter(m));
								}
							} else {
								m.setAccessible(true);
								s.put(m.getAnnotation(Key.class).value(), new MethodSetter(m));
							}
			
			////////////

			gettersCache.put(clazz, getters);
			settersCache.put(clazz, setters);
		}
		
//		if (setters.isEmpty() && getters.isEmpty()) {
//			setters = null;
//			getters = null;
//		}
		
	}
	
	@Override public int toInt() { return Integer.parseInt(value.toString()); }
	@Override public double toDouble() { return Double.parseDouble(value.toString()); }
	@Override public boolean toBoolean() { return value.toString().isEmpty(); }
	
	@Override public Js get(JsBasicType key) {
		try {
			Js r = super.get(key);
			if (r==UNDEFINED && getters.containsKey(key.toString())) {
				return Js.on(getters.get(key).get(value));
			} else
				return r;
			
//			if (setters==null || !setters.containsKey(key)) { 
//				Js r = super.get(key);
//				if (!(r == UNDEFINED && getters!=null && getters.containsKey(key))) return r;
//				else return Js.on(getters.get(key).get(value));
//			} else 
//				if (getters==null || !getters.containsKey(key)) return super.get(key);
//				else return Js.on(getters.get(key).get(value));
			
		} catch (Exception e) {
			throw new RuntimeException("Key: "+key,e);
		}
	}
	
	@Override public Js get(String key) {
		try {
			Js r = super.get(Js.on(key));
			if (r==UNDEFINED && getters.containsKey(key))
				return Js.on(getters.get(key).get(value));
			else
				return r;
			
//			if (setters==null || !setters.containsKey(key)) { 
//				Js r = super.get(Js.on(key));
//				if (!(r == UNDEFINED && getters.containsKey(key))) return r;
//				else return Js.on(getters.get(key).get(value));
//			} else 
//				if (getters==null || !getters.containsKey(key)) return super.get(Js.on(key));
//				else return Js.on(getters.get(key).get(value));
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override public Js put(JsBasicType key, Js v) {
		try {
			if (key.typeof()!=JsString.class || setters==null || !setters.containsKey(key)) 
				return super.put(key, v);
			else 
				setters.get(key.toString()).set(value, v);
			return this;
		} catch (Exception e) { 
			throw new RuntimeException(e);
		}
	}
	
	@Override public Js put(String key, Object v) {
		try {
			if (setters!=null && setters.containsKey(key)) 
				setters
				.get(key)
				.set(value, v); 
			else 
				return super.put(Js.on(key), Js.on(v)); 
			return this;
		} catch (Exception e) { 
			throw new RuntimeException(e);
		}
	}
	
	@Override public Js put(String key, String value) { return this.put(key, (Object)value); }
	@Override public Js put(String key, double value) { return this.put(key, (Object)value); }
	@Override public Js put(String key, boolean value) { return this.put(key, (Object)value); }
	
	@Override public Js delete(JsBasicType key) {
		if (getters!=null && getters.containsKey(key.toString()))
			return super.put(key, UNDEFINED);
		else
			return super.delete(key);
	}
	
	
	
	@Override public StringBuilder stringify(StringBuilder sb) {
		int j=0;
		sb.append("{");
		for ( Object o: this ) 
			 sb
			 .append(j++==0?"":",")
			 .append( Js.on(o).stringify() )
			 .append(":")
			 .append( get(o).stringify() );
		
		sb.append("}");
		
		return sb;
	}
	
	
	@Override public Iterator<Object> iterator() {
		@SuppressWarnings("unchecked") 
		final Set<String> getterSet = getters==null?Collections.EMPTY_SET:getters.keySet();
		final Iterator<String> first = getterSet.iterator();
		final Iterator<Object> second = super.iterator();
		return new Iterator<Object>() {
			Object secondNext = null;
			
			@Override public boolean hasNext() {
				if (first.hasNext()) 
					return true;
				
				if (secondNext!=null) 
					return true;
				
				while (second.hasNext()) {
					secondNext = second.next();
					if (getters==null || !getters.keySet().contains(secondNext) )
						return true;
				}
				
				secondNext = null;
				return false;
				
			}
			@Override public Object next() {
				if (first.hasNext()) return first.next();
				
				if (this.hasNext()) {
					Object o = secondNext;
					secondNext = null;
					return o;
				}
				
				return null;		
			}
			@Override public void remove() {}
		};
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof JsProxy) 
			obj = ((JsProxy)obj).asJs();
		if (!(obj instanceof JsObject)) 
			return false;
		
		int i=0, k =0  ;
		for (Object key: (Js)obj ) {
			i++;
			Js a = this.get(key);
			Js b = ((Js)obj).get(key);
			
			if (!a.equals( b ))
				return false;
			
			// count-out keys that appear in getters while overridden by entries in map
			if (this.getters!=null && this.getters.containsKey(key) && this.map!=null && this.map.containsKey(key))
				k++;
		}
		
		int j = (this.getters==null?0:this.getters.size())+ (map==null?0:map.size())-k;
		
		return i == j;
	}

	@Override public Class<? extends Js> typeof() {
		return JsObjectWrapper.class;
	}

	
	
}













