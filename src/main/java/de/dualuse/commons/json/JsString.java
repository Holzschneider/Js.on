package de.dualuse.commons.json;

import java.io.IOException;

import com.google.gson.stream.JsonWriter;


class JsString extends JsBasicType {
	public static final char[] HEXADECIMAL = "0123456789ABCDEF".toCharArray(), H = HEXADECIMAL;

	public String value;
	
	JsString(String value) { this.value = value; }
	JsString(JsString copy) { this.value = copy.value; }
	
	@Override public Js clone() { return new JsString(this); }
	
	@Override public int toInt() { return Integer.parseInt(value); }
	@Override public double toDouble() { return Double.parseDouble(value); }
	/* (non-Javadoc)
	 * @see de.dualuse.commons.data.Js#toString()
	 */
	@Override public String toString() { return value; }
	@Override public boolean toBoolean() { return value.isEmpty()?false:JsBoolean.TRUE.equals(value)?true:(JsBoolean.FALSE.equals(value)?false:true); }
	
	@Override public boolean equals(Object obj) {
		if (obj==this) return true;
		if (obj instanceof JsBoolean) return ((JsBoolean)obj).value?JsBoolean.TRUE.equals(value):JsBoolean.FALSE.equals(value);
		if (obj instanceof Boolean) return ((Boolean)obj).booleanValue()?JsBoolean.TRUE.equals(value):JsBoolean.FALSE.equals(value);
		if (obj instanceof JsNumber) try { return ((JsNumber)obj).value == Double.parseDouble(value); } catch (NumberFormatException nfe) { return false; }
		if (obj instanceof Number) try { return ((Number)obj).doubleValue() == Double.parseDouble(value); } catch (NumberFormatException nfe) { return false; }
		if (obj instanceof JsString) return ((JsString)obj).value.equals(value);
		if (obj instanceof String) return ((String)obj).equals(value);
		return false;
	}
	
	@Override public int hashCode() { return value.hashCode(); }
	public Class<? extends Js> typeof() { return JsString.class; }
	
	@Override public StringBuilder stringify(StringBuilder sb) {
		sb.append('"');

		int i = 0;
		for (int c=0,j=0,J=value.length();j<J;j++) 
			switch (c=value.charAt(j)) {
			case '"': sb.append(value,i,j).append("\\\""); i=j+1; break;
			case '\n': sb.append(value,i,j).append("\\n"); i=j+1; break;
			default:
				if (c>=32 && c<=127) break;
				sb.append(value,i,j).append("\\u"+H[(c>>12)&0xF]+H[(c>>8)&0xF]+H[(c>>4)&0xF]+H[c&0xF]); 
				i=j+1; 
			break;
			}

		sb.append(value,i, value.length());
		
		sb.append('"');
		
		return sb;
	}
	
	@Override public Js to(JsonWriter w) throws IOException {
		w.value(value);
		return this;
	}
	
	public Class<?> valueType() { return String.class; }
	
}