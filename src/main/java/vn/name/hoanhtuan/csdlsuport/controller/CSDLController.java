package vn.name.hoanhtuan.csdlsuport.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.name.hoanhtuan.csdlsuport.common.Constant;
import vn.name.hoanhtuan.csdlsuport.common.EnumResultCode;
import vn.name.hoanhtuan.csdlsuport.model.ErrorMessage;
import vn.name.hoanhtuan.csdlsuport.model.ResponseBase;
import vn.name.hoanhtuan.csdlsuport.model.csdl.request.RequestBaoDong;
import vn.name.hoanhtuan.csdlsuport.model.csdl.request.RequestCSDLSupport;
import vn.name.hoanhtuan.csdlsuport.service.CSDLService;
import vn.name.hoanhtuan.csdlsuport.utils.CommonUtil;
import vn.name.hoanhtuan.csdlsuport.utils.JacksonUtils;
import vn.name.hoanhtuan.csdlsuport.utils.ResponseUtils;

import java.util.UUID;


@Slf4j
@RestController
@RequestMapping(path = "api/v1")
@AllArgsConstructor
public class CSDLController {

    private CSDLService csdlService;

    @PostMapping("tim-bao-dong")
    public ResponseEntity<ResponseBase> timBaoDong(@RequestBody RequestBaoDong request){
        CommonUtil.writeLogRequestId(request.getRequestId());
        LOGGER.info(Constant.REQUEST_BODY, JacksonUtils.toJSonString(request));
        ResponseBase response = new ResponseBase(EnumResultCode.SYSTEM_ERROR);

        try{
             // Check request null
            if(ObjectUtils.isEmpty(request)){
                return ResponseUtils.response(new ResponseBase(EnumResultCode.INVALID_PARAM_REQUEST));
            }

            // Validate request
            ErrorMessage errorMessage = request.validateInput();
            if(ObjectUtils.isNotEmpty(errorMessage)){
                return ResponseUtils.response(new ResponseBase(errorMessage));
            }

             response = csdlService.timBaoDong(request);

        }catch (Exception ex){
           CommonUtil.handleException(ex);
        }

        LOGGER.info(Constant.RESPONSE_BODY, response);
        return ResponseUtils.response(response);
    }

    @PostMapping("phan-tich-csdl")
    public ResponseEntity<ResponseBase> phanTichCSDL(@RequestBody RequestCSDLSupport request){
        CommonUtil.writeLogRequestId(request.getRequestId());
        LOGGER.info(Constant.REQUEST_BODY, JacksonUtils.toJSonString(request));
        ResponseBase response = new ResponseBase(EnumResultCode.SYSTEM_ERROR);

        try{
            // Check request null
            if(ObjectUtils.isEmpty(request)){
                return ResponseUtils.response(new ResponseBase(EnumResultCode.INVALID_PARAM_REQUEST));
            }

            // Validate request
            ErrorMessage errorMessage = request.validateInput();
            if(ObjectUtils.isNotEmpty(errorMessage)){
                return ResponseUtils.response(new ResponseBase(errorMessage));
            }

             response = csdlService.phanTichCSDL(request);

        }catch (Exception ex){
            CommonUtil.handleException(ex);
        }

        LOGGER.info(Constant.RESPONSE_BODY, response);
        return ResponseUtils.response(response);
    }

    @GetMapping("danh-sach-luoc-do-mau")
    public ResponseEntity<ResponseBase> phanTichCSDL(){
        ResponseBase response = new ResponseBase(EnumResultCode.SYSTEM_ERROR);
        String requestId = UUID.randomUUID().toString();
        CommonUtil.writeLogRequestId(requestId);

        try {
            response = csdlService.listExampleCSDL();
        } catch (Exception ex) {
            CommonUtil.handleException(ex);
        }

        LOGGER.info(Constant.RESPONSE_BODY, response);
        return ResponseUtils.response(response);
    }

}
