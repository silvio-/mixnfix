import java.io.File;

public class PDFPrint
{
    public static void main(String[] args) throws Exception {

        File curdir = new File(args[0]);
        File file = new File(args[1]);

        try {
            // Open the file with associated viewer
            // ShellExec.shellExecute("open", filename, "", curpath);

            // Print to network printer
            // ShellExec.shellExecute("printto", filename, "\"\\\\myprintserver\\Xerox PS Printer\"", curpath);

            // Print to default printer
            ShellExec.shellExecute("print", args[1], "", curdir.getCanonicalPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

