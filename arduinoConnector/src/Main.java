import control.DataCenter;
import serial.SimpleRead;

import javax.comm.CommPortIdentifier;
import java.util.Enumeration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    private static CommPortIdentifier portId;

    private static Enumeration portList;//枚举类

    public static void main(String[] args) {

        portList = CommPortIdentifier.getPortIdentifiers();/*不带参数的getPortIdentifiers方法获得一个枚举对象，该对象又包含了系统中管理每个端口的CommPortIdentifier对象。注意这里的端口不仅仅是指串口，也包括并口。这个方法还可以带参数。getPortIdentifiers(CommPort)获得与已经被应用程序打开的端口相对应的CommPortIdentifier对象。getPortIdentifier(String portName)获取指定端口名（比如“COM1”）的CommPortIdentifier对象。*/

        while (portList.hasMoreElements()) {
            portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL)/*getPortType方法返回端口类型*/ {
                if (portId.getName().equals("COM4"))/* 找Windows下的第一个串口*/ {
                    //if (portId.getName().equals("/dev/term/a"))/*找Unix-like系统下的第一个串口*/ {
                    BlockingQueue publicBoxQueue = new LinkedBlockingQueue(5);
                    SimpleRead reader = new SimpleRead(portId, publicBoxQueue);
                    DataCenter dataCenter = new DataCenter(publicBoxQueue);
                }
            }
        }
    }
}
