package de.dualuse.commons.json;

import java.io.IOException;

import com.google.gson.stream.JsonWriter;

class JsNumber extends JsBasicType {
	public double value;
	
	JsNumber(double value) { this.value = value; }
	JsNumber(JsNumber copy) { this.value = copy.value; }
	@Override public Js clone() { return new JsNumber(this); }
	
	@Override public int toInt() { return (int)value; }
	@Override public double toDouble() { return value; }
	@Override public String toString() { String s = ""+value; return s.endsWith(".0")?s.substring(0, s.length()-2):s; }
	@Override public boolean toBoolean() { return value == 0.0; }
	
	@Override public boolean equals(Object obj) {
		if (obj==this) return true;
		if (obj instanceof JsNumber) return ((JsNumber)obj).value == value;
		if (obj instanceof Number) return ((Number)obj).doubleValue()==value;
		if (obj instanceof JsString) try { return value == Double.parseDouble(((JsString)obj).value); } catch (NumberFormatException nfe) { return false; }
		if (obj instanceof String) try { return value == Double.parseDouble((String)obj); } catch (NumberFormatException nfe) { return false; }
		return false;
	}

	@Override public StringBuilder stringify(StringBuilder sb) {
		if (((long)value)!=value) return sb.append(value);
		else return sb.append((long)value);
	}
	
	@Override public int hashCode() { return toString().hashCode(); }
	public Class<? extends Js> typeof() { return JsNumber.class; }

	@Override public Js to(JsonWriter w) throws IOException {
		if ((long)value!=value) w.value(value);
		else w.value((long)value);
		return this;
	}
	
	public Class<?> valueType() { return Double.class; }

}