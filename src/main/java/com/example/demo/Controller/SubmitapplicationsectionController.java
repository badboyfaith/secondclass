package com.example.demo.Controller;
import com.example.demo.Service.SubmitapplicationsectionService;
import com.example.demo.model.ActivityScore;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/submitapplication/excel")
public class SubmitapplicationsectionController {

    @Autowired
    private SubmitapplicationsectionService submitapplicationsectionService;

    @PostMapping("/import")
    public String importExcel(@RequestParam("file") MultipartFile file) {
        List<ActivityScore> scores = submitapplicationsectionService.processExcel(file);
        submitapplicationsectionService.exportExcel(scores);
        return "Excel processed and exported successfully";
    }
}

