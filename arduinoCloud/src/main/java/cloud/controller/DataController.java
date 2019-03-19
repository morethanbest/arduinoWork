package cloud.controller;

import cloud.buffer.LightDataBuffer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class DataController {

    @Resource
    private LightDataBuffer buffer;

    @RequestMapping("/update")
    @ResponseBody
    public double update(){
        return buffer.getAverage();
    }
}
