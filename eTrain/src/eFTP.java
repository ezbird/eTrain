package sweetdream;

import ftp.*;
import java.io.PrintStream;
import javax.swing.JLabel;

// Referenced classes of package sweetdream:
//            Sweetdream

public class eFTP extends Thread
    implements FtpObserver
{

    FtpBean ftp;
    long num_of_bytes;
    String folder;
    String download;
    String save;

    public eFTP(String ftpFolder, String fileToDownload, String fileToWrite)
    {
        num_of_bytes = 0L;
        folder = ftpFolder;
        download = fileToDownload;
        save = fileToWrite;
        ftp = new FtpBean();
    }

    public void run()
    {
        start();
    }

    public void connect()
    {
        try
        {
            ftp.ftpConnect("etrainhub.com", "ezra", "ezraezra1!");
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    public void close()
    {
        try
        {
            ftp.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    public void listDirectory()
    {
        FtpListResult ftplrs = null;
        try
        {
            ftp.setDirectory(folder);
            ftplrs = ftp.getDirectoryContent();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        for(; ftplrs.next(); System.out.println(ftplrs.getName()))
        {
            int type = ftplrs.getType();
            if(type == 1)
            {
                System.out.print("DIR\t");
                continue;
            }
            if(type == 2)
            {
                System.out.print("FILE\t");
                continue;
            }
            if(type == 3)
            {
                System.out.print("LINK\t");
                continue;
            }
            if(type == 6)
            {
                System.out.print("OTHER\t");
            }
        }

    }

    public void getFile()
    {
        try
        {
            ftp.getBinaryFile(download, save, this);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    public void byteRead(int bytes)
    {
        num_of_bytes += bytes;
        Sweetdream.statusDisplay.setText("downloaded " + num_of_bytes / 1000L + " Kb");
        System.out.println(num_of_bytes + " of bytes read already.");
    }

    public void byteWrite(int i)
    {
    }
}

