import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

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
public class FileLockTest {
    public FileLockTest() {
    }

    public static void main(String[] args) {
        try {
            // Get a file channel for the file
            File file = new File("c:/prova1024-0.pdf");
            FileChannel channel = new RandomAccessFile(file, "rw").getChannel();

            // Use the file channel to create a lock on the file.
            // This method blocks until it can retrieve the lock.
            FileLock lock = channel.lock();

            // Try acquiring the lock without blocking. This method returns
            // null or throws an exception if the file is already locked.
            try {
                lock = channel.tryLock();
            }
            catch (OverlappingFileLockException e) {
                // File is already locked in this thread or virtual machine
                e.printStackTrace();
            }

            // Release the lock
            lock.release();

            // Close the file
            channel.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
