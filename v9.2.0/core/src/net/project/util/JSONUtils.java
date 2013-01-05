/**
 * 
 */
package net.project.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author
 */
public class JSONUtils {

	/**
	 * @param ja
	 * @param buff
	 * @return
	 * @throws JSONException
	 */
	public static String jsonArrayToString(JSONArray ja, StringBuffer buff) throws JSONException {
		if (buff == null)
			buff = new StringBuffer("[");
		else
			buff.append("[");
		for (int key = 0; (ja != null) && key < ja.length(); key++) {
			String value = null;
			if (ja.optJSONObject(key) != null) {
				jsonToObjectLibertal(ja.optJSONObject(key), buff);
			} else if (ja.optJSONArray(key) != null) {
				jsonArrayToString(ja.optJSONArray(key), buff);
			} else if (ja.optLong(key, -1) != -1) {
				value = ja.get(key) + "";
				buff.append(value);
			} else if (ja.optDouble(key, -1) != -1) {
				value = ja.get(key) + "";
				buff.append(value);
			} else if (ja.optBoolean(key)) {
				value = ja.getBoolean(key) + "";
				buff.append(value);
			} else if (ja.opt(key) != null) {
				Object obj = ja.opt(key);
				if (obj instanceof Boolean) {
					value = ja.getBoolean(key) + "";
				} else {
					value = "'" + ja.get(key) + "'";
				}
				buff.append(value);
			}
			if (key < ja.length() - 1)
				buff.append(",");
		}
		buff.append("]");
		return buff.toString();
	}
	
	/**
	 * @param jo
	 * @param buff
	 * @return
	 * @throws JSONException
	 */
	public static String jsonToObjectLibertal(JSONObject jo, StringBuffer buff) throws JSONException {
		if (buff == null)
			buff = new StringBuffer("{");
		else
			buff.append("{");
		JSONArray names = jo.names();
		for (int l = 0; (names != null) && l < names.length(); l++) {
			String key = names.getString(l);
			String value = null;
			if (jo.optJSONObject(key) != null) {
				value = key + ":";
				buff.append(value);
				jsonToObjectLibertal(jo.optJSONObject(key), buff);
			} else if (jo.optJSONArray(key) != null) {
				value = key + ":";
				buff.append(value);
				jsonArrayToString(jo.optJSONArray(key), buff);
			} else if (jo.optLong(key, -1) != -1) {
				value = key + ":" + jo.get(key) + "";
				buff.append(value);
			} else if (jo.optDouble(key, -1) != -1) {
				value = key + ":" + jo.get(key) + "";
				buff.append(value);
			} else if (jo.opt(key) != null) {
				Object obj = jo.opt(key);
				if (obj instanceof Boolean) {
					value = key + ":" + jo.getBoolean(key) + "";
				} else {
					value = key + ":" + "'" + jo.get(key) + "'";
				}
				buff.append(value);
			}
			if (l < names.length() - 1)
				buff.append(",");
		}
		buff.append("}");
		return buff.toString();
	}

}
