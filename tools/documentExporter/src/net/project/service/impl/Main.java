package net.project.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

	//http://www.infosniper.net/index.php?ip_address=93.83.225.242&map_source=3&overview_map=1&lang=2&map_type=1&zoom_level=7
	//private static final String URL_LINK = "http://www.ip-adress.com/ip_lokalisieren/";

	//private static final String URL_LINK = "http://www.infosniper.net/index.php?ip_address=93.83.225.242&map_source=3&overview_map=1&lang=2&map_type=1&zoom_level=7";
	private static final String URL_LINK = "http://www.ip-adress.com/ip_lokalisieren/";

	public static void main(String[] args) {
		try {
			String html = getHTML("93.83.225.242");
			System.out.println(getCountry(html));
			System.out.println(getArea(html));
			System.out.println(getCity(html));
			System.out.println(getLatitude(html));
			System.out.println(getLongitude(html));
			System.out.println(getOrganization(html));
			// System.out.println(html);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String getHTML(String ipAddress) {
		StringBuffer html = new StringBuffer();
		String divHTMLStart = "<div id=\"ipinfo\">";
		String divHTMLEnd = "</div>";
		try {
			String data = URLEncoder.encode("QRY", "UTF-8") + "=" + URLEncoder.encode(ipAddress, "UTF-8");
			URL url = new URL(URL_LINK);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data.toString());
			wr.flush();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			boolean divStart = false;
			boolean divEnd = false;
			while ((line = rd.readLine()) != null) {
				if (line.indexOf(divHTMLStart) != -1 && !divStart) {
					divStart = true;
				}
				if (divStart && !divEnd) {
					if (line.indexOf(divHTMLEnd) == -1) {
						html.append(line).append("\n");
					} else {
						html.append(line.substring(0, line.indexOf(divHTMLEnd)) + divHTMLEnd.length()).append("\n");
						divEnd = true;
					}
				}
			}
			html.append(divHTMLEnd);
			wr.close();
			rd.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return html.toString();
	}

	private static String getLatitude(String htmlText) {
		String latitude = "";
		String str;
		BufferedReader reader = new BufferedReader(new StringReader(htmlText));
		try {
			while ((str = reader.readLine()) != null) {
				if (str.length() > 0) {
					if (str.indexOf("Breitengrad") != -1) {
						reader.readLine();
						latitude = reader.readLine().trim();
						break;
					}
				}
			}
			if (latitude != "" && latitude.length() > 10) {
				latitude = latitude.substring(0, 10).trim();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return latitude;
	}

	private static String getLongitude(String htmlText) {
		String longitude = "";
		String str;
		BufferedReader reader = new BufferedReader(new StringReader(htmlText));
		try {
			while ((str = reader.readLine()) != null) {
				if (str.length() > 0) {
					if (str.indexOf("L&auml;ngengrad") != -1) {
						reader.readLine();
						longitude = reader.readLine().trim();
						break;
					}
				}
			}
			if (longitude != "" && longitude.length() > 10) {
				longitude = longitude.substring(0, 10).trim();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return longitude;
	}

	private static String getArea(String htmlText) {
		String area = "";
		String str;
		BufferedReader reader = new BufferedReader(new StringReader(htmlText));
		try {
			while ((str = reader.readLine()) != null) {
				if (str.length() > 0) {
					if (str.indexOf("Bundesland der IP") != -1) {
						reader.readLine();
						area = reader.readLine().trim();
						break;
					}
				}
			}
			if (area != "" && area.length() > 10) {
				area = area.substring(0, 10).trim();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return area;
	}

	private static String getCity(String htmlText) {
		String city = "";
		String str;
		BufferedReader reader = new BufferedReader(new StringReader(htmlText));
		try {
			while ((str = reader.readLine()) != null) {
				if (str.length() > 0) {
					if (str.indexOf("Stadt der IP") != -1) {
						reader.readLine();
						city = reader.readLine().trim();
						break;
					}
				}
			}
			if (city != "" && city.length() > 10) {
				city = city.substring(0, 10).trim();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return city;
	}

	private static String getCountry(String htmlText) {
		String country = "";
		String str;
		BufferedReader reader = new BufferedReader(new StringReader(htmlText));
		try {
			while ((str = reader.readLine()) != null) {
				if (str.length() > 0) {
					if (str.indexOf("Land der IP") != -1) {
						reader.readLine();
						country = reader.readLine().trim();
						break;
					}
				}
			}
			String patternStr = "/flags";
			String replaceStr = "http://www.ip-adress.com/flags";
			Pattern pattern = Pattern.compile(patternStr);

			// Replace all (\w+) with <$1>
			CharSequence inputStr = country;
			Matcher matcher = pattern.matcher(inputStr);
			country = matcher.replaceAll(replaceStr);

			country = country.trim().substring(0, country.length() - 5).trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return country;
	}

	private static String getOrganization(String htmlText) {
		String organization = "";
		String str;
		BufferedReader reader = new BufferedReader(new StringReader(htmlText));
		try {
			while ((str = reader.readLine()) != null) {
				if (str.length() > 0) {
					if (str.indexOf("Organisation:") != -1) {
						reader.readLine();
						organization = reader.readLine().trim();
						break;
					}
				}
			}
			if (organization != "" && organization.length() > 10) {
				organization = organization.substring(0, 30).trim();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return organization;
	}

}
