package sweetdream;
 
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.InputStream;
import java.io.OutputStream;
import processing.app.Preferences;
 
public class Arduino {
    static InputStream input;
    static OutputStream output;
 
    public Arduino() throws Exception{
     //   Preferences.init();
        System.out.println("Using port: " + Preferences.get("serial.port"));
        CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(
                Preferences.get("serial.port"));
 
        SerialPort port = (SerialPort)portId.open("serial talk", 4000);
        input = port.getInputStream();
        output = port.getOutputStream();
        port.setSerialPortParams(Preferences.getInteger("serial.debug_rate"),
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);
        while(true){
            while(input.available()>0) {
                System.out.print((char)(input.read()));
            }
        }
    }
}
