package com.gradeCalculator.server.controller;

import com.gradeCalculator.server.Entities.ModuleEntity;
import com.gradeCalculator.server.repositories.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path="/main")
public class MainController {
    @Autowired
    private ModuleRepository moduleRepository;

    @PostMapping(path="/addModule")
    public @ResponseBody String addNewModule(@RequestParam String name){
        ModuleEntity m = new ModuleEntity();
        m.setName(name);
        moduleRepository.save(m);
        return "saved";
    }

    @GetMapping(path="/allModules")
    public @ResponseBody Iterable<ModuleEntity> getAllModules(){
        return moduleRepository.findAll();
    }
}
