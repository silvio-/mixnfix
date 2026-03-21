package mixnfix.prova;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;

import mixnfix.Model;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public abstract class No extends Model {
    private Properties _properties = new Properties();
    public void addProperties(Properties p) {
        _properties.putAll(p);
    }
    public void setProperty(String name, String value) {
        _properties.put(name, value);
    }
    public String getProperty(String name) {
        return _properties.getProperty(name);
    }
    public Properties getProperties() {
        return _properties;
    }

    public static final String TAG = "tag";
    public void setTag(String value) {
        setProperty(TAG, value);
        this.fireModelUpdate();
    }

    public String getTag() {
        return getProperty(TAG);
    }

    public void printPropertiesInXML(PrintWriter s) {
        boolean first = true;
        for (Map.Entry e: _properties.entrySet()) {
            if (!first) {
                s.print(" ");
            }
            String key = (String) e.getKey();
            String value = (String) e.getValue();
            s.print(key +"=\""+value+"\"");
            first = false;
        }
    }
    public abstract void printXML(PrintWriter s);
}
