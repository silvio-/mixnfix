package mixnfix;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SG extends Thread {
    InputStream is;
    String type;
    public SG(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = br.readLine();

            while(line != null) {
                String str = type + line + "\n";
                System.out.print(str);

                line = br.readLine();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
