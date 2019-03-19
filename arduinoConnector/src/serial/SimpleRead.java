package serial;

import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import javax.comm.*;

public class SimpleRead extends Observable implements Runnable, SerialPortEventListener {

    private InputStream inputStream;

    private static SerialPort serialPort;

    private SimpleRead readThread;

    private volatile String message;

    private final BlockingQueue proQueue;

    public SimpleRead(CommPortIdentifier portId,BlockingQueue publicBoxQueue) {
        this.proQueue = publicBoxQueue;
        try {
            serialPort = (SerialPort) portId.open("SimpleReadApp", 2000);/* open方法打开通讯端口，获得一个CommPort对象。它使程序独占端口。如果端口正被其他应用程序占用，将使用CommPortOwnershipListener事件机制，传递一个PORT_OWNERSHIP_REQUESTED事件。每个端口都关联一个InputStream 何一个OutputStream。如果端口是用open方法打开的，那么任何的getInputStream都将返回相同的数据流对象，除非有close被调用。有两个参数，第一个为应用程序名；第二个参数是在端口打开时阻塞等待的毫秒数。*/
        } catch (PortInUseException e) {
            e.printStackTrace();
        }
        try {
            inputStream = serialPort.getInputStream();/*获取端口的输入流对象*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            serialPort.addEventListener(this);/*注册一个SerialPortEventListener事件来监听串口事件*/
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        }

        serialPort.notifyOnDataAvailable(true);/*数据可用*/

        try {
            serialPort.setSerialPortParams(9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);/*设置串口初始化参数，依次是波特率，数据位，停止位和校验*/
        } catch (UnsupportedCommOperationException e) {
            e.printStackTrace();
        }

        this.run();
    }

    @Override
    public void run() {

    }

//串口事件


    @Override
    public void serialEvent(SerialPortEvent event) {

        switch (event.getEventType()) {
            case SerialPortEvent.BI:/*Break interrupt,通讯中断*/
            case SerialPortEvent.OE:/*Overrun error，溢位错误*/
            case SerialPortEvent.FE:/*Framing error，传帧错误*/
            case SerialPortEvent.PE:/*Parity error，校验错误*/
            case SerialPortEvent.CD:/*Carrier detect，载波检测*/
            case SerialPortEvent.CTS:/*Clear to send，清除发送*/
            case SerialPortEvent.DSR:/*Data set ready，数据设备就绪*/
            case SerialPortEvent.RI:/*Ring indicator，响铃指示*/
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:/*Output buffer is empty，输出缓冲区清空*/
                break;

            case SerialPortEvent.DATA_AVAILABLE:/*Data available at the serial port，端口有可用数据。读到缓冲数组，输出到终端*/
                byte[] readBuffer = new byte[50];

                try {
                    int len = 0 ;
                    int temp = 0 ;            // 接收每一个读取进来的数据
                    while((temp = inputStream.read())!= -1){
                        // 表示还有内容，文件没有读完
                        if(temp == 13) {
                            proQueue .put(new String(readBuffer).trim());
                            readBuffer = new byte[50];
                            len = 0;
                            continue;
                        }
                        readBuffer[len] = (byte)temp ;
                        len++ ;
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public static void sendComm(String data) {
        byte[] writerBuffer = data.getBytes();
        OutputStream outputStream;
        try {
            outputStream = serialPort.getOutputStream();
            outputStream.write(writerBuffer);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
