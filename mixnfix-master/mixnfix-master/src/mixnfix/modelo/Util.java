package mixnfix.modelo;


public class Util {
	public static String indent(String str) {
		java.io.BufferedReader r = new java.io.BufferedReader(new java.io.StringReader(str));
		StringBuffer b = new StringBuffer();
		try {
			String l = r.readLine();
			while(l != null) {
				b.append("    " + l + "\n");
				l = r.readLine();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return b.toString();
	}
}
