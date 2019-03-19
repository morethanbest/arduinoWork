package cloud.buffer;

import org.springframework.stereotype.Component;

import java.nio.DoubleBuffer;
import java.util.Arrays;

@Component
public class LightDataBuffer {

    private DoubleBuffer buffer;

    public LightDataBuffer() {
        buffer = DoubleBuffer.allocate(20);
    }

    public void updateBuffer(double lightData){
        buffer.put(lightData);
        if(!buffer.hasRemaining()){
            buffer.clear();
        }
    }

    public double getAverage(){
        return Arrays.stream(buffer.array()).sum()/ buffer.capacity();
    }

}
