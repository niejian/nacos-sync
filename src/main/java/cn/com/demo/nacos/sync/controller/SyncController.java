package cn.com.demo.nacos.sync.controller;

import cn.com.demo.nacos.sync.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: nj
 * @date: 2019-09-03:13:29
 */
@RestController
public class SyncController {
    @Autowired
    private TaskService taskService;

    @GetMapping(value = "sync")
    public String sync() throws Exception{
        this.taskService.getDubboService();
        return "success";
    }
}
