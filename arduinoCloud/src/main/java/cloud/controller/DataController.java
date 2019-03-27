package cloud.controller;

import cloud.buffer.LightDataBuffer;
import cloud.buffer.TemperatureDataBuffer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

@Controller
public class DataController {

    @Resource
    private LightDataBuffer buffer;
    @Resource
    private TemperatureDataBuffer temperatureDataBuffer;

    @RequestMapping("/update")
    @ResponseBody
    public double update(@RequestParam String name){
        if(name.equals("l"))
            return buffer.getAverage();
        if(name.equals("t"))
            return temperatureDataBuffer.getAverage();
        return 0;
    }

    @RequestMapping("/temperature")
    @ResponseBody
    public boolean temperature(@RequestParam double t){
        System.out.println("temperature->" + t);
        temperatureDataBuffer.updateBuffer(t);
        return true;
    }

    @RequestMapping("/chart")
    public String chart(){
        return "chart";
    }

}
