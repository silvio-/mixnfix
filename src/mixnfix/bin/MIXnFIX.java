package mixnfix.bin;

import java.io.File;

import mixnfix.gui.App;
import mixnfix.gui.ConfiguracaoMIXnFIX;
import mixnfix.gui.MainFrame;

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
public class MIXnFIX {
    public MIXnFIX() {
    }
    
    public static void main(String[] args) {
    	System.out.println("Arguments [ <dbname> <datadir> ]");
    	if (args.length == 2) {
    		File dbname = new File(args[0]);
    		File datadir = new File(args[1]);
    		if (dbname.exists() && datadir.exists()) {
    			App.getConfiguracao().setProperty(ConfiguracaoMIXnFIX.dbname,args[0]);
    			App.getConfiguracao().setProperty(ConfiguracaoMIXnFIX.datadir,args[1]);
    		}
    	}
    	MainFrame.start();    	
	}
}
