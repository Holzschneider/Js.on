package de.dualuse.commons.json;

/**
 * 
 * aus "as" wirklich das Gegenstück zu "on" machen
 * und copy-Varianten wie String, float , etc. mit to prefixen
 * Hazard: toString() hat in javascript immer [object Object] als result, und kein *json*
 * 
 * nicht vergessen
 * 
 * to(Object[] array)
 * to(int[] array)
 * to(Map<?,?> map)
 * T to(Class<T> asdf) <- data Konverter einbauen, vielleicht so ähnlich wie SPI/ServicesProviderInterfaces   
 * T to(Converter<T> asdf) <- expliziter Data-Converter    
 * 
 * Js j =  ....
 * 
 * j.to( k -> new Person(k.get("name")); }
 * 
 * j.forEach( key -> return get(key).toInt()*2 ); }
 * 
 * 
 * ohoh: 
 * as(interface -wrapper) vergessen zu implementieren, 
 * darf auch nur noch für interfaces funktionieren, weil to ja 
 * 
 * 
 * @author holzschneider
 */
interface JsProxy {
	Js asJs();
	String stringify();
}