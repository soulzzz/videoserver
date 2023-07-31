package com.soul.videoserver.controller;

import com.soul.videoserver.common.APIResponse;
import com.soul.videoserver.common.ResponeCode;
import com.soul.videoserver.entity.ErrorLog;
import com.soul.videoserver.service.ErrorLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/errorLog")
public class ErrorLogController {

    private final static Logger logger = LoggerFactory.getLogger(ErrorLogController.class);

    @Autowired
    ErrorLogService service;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(){
        List<ErrorLog> list =  service.getErrorLogList();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(ErrorLog errorLog) throws IOException {
        service.createErrorLog(errorLog);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        System.out.println("ids===" + ids);
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteErrorLog(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(ErrorLog errorLog) throws IOException {
        service.updateErrorLog(errorLog);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

}
