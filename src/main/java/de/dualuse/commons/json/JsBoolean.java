package de.dualuse.commons.json;

import java.io.IOException;

import com.google.gson.stream.JsonWriter;

class JsBoolean extends JsBasicType {
	final public static String TRUE = "true";
	final public static String FALSE = "false";
	
	
	
	public boolean value;
	
	JsBoolean(boolean value) { this.value = value; }
	JsBoolean(JsBoolean copy) { this.value = copy.value; }
	
	@Override public int toInt() { return value?1:0; }
	@Override public double toDouble() { return toInt(); }
	@Override public String toString() { return value?TRUE:FALSE; }
	@Override public boolean toBoolean() { return value; }

	@Override public boolean equals(Object obj) {
		if (obj==this) return true;
		if (obj instanceof JsBoolean) return ((JsBoolean)obj).value == value;
		if (obj instanceof Boolean) return ((Boolean)obj).booleanValue()==value;
		if (obj instanceof JsString) try { return value?TRUE.equals(((JsString)obj).value):FALSE.equals(((JsString)obj).value); } catch (NumberFormatException nfe) { return false; }
		if (obj instanceof String) try { return value?TRUE.equals((String)obj):FALSE.equals((String)obj); } catch (NumberFormatException nfe) { return false; }
		return false;
	}
	
	@Override public int hashCode() { return value==Boolean.TRUE ? HASHCODE_TRUE : HASHCODE_FALSE; }

	@Override public StringBuilder stringify(StringBuilder sb) { return sb.append(value); }
	
	public Class<? extends Js> typeof() { return JsBoolean.class; }
	
	private final static int HASHCODE_TRUE = TRUE.hashCode();
	private final static int HASHCODE_FALSE = FALSE.hashCode();

	@Override public Js to(JsonWriter w) throws IOException {
		w.value(value);
		return this;
	}

	@Override public Js clone() { return new JsBoolean(this); }
	
	public Class<?> valueType() { return Boolean.class; }

}