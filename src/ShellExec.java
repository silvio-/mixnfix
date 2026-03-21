public class ShellExec
{
     static
     {
          System.loadLibrary("ShellExec");
     }

     public native static void shellExecute(String cmd,String file,String parameters,String directory);
}
