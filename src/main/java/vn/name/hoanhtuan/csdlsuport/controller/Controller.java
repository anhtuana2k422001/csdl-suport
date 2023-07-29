package vn.name.hoanhtuan.csdlsuport.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.name.hoanhtuan.csdlsuport.common.Constant;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "api/v1")
public class Controller {
    @PostMapping("tim-bao-dong")
    public ResponseEntity<String> exampleController(){
        String requestId = UUID.randomUUID().toString();
        MDC.put(Constant.REQUEST_ID, requestId);
        LOGGER.info("Call api success");
        MDC.clear();
        return ResponseEntity.ok("Start build app");
    }
}
