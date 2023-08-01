package vn.name.hoanhtuan.csdlsuport.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import vn.name.hoanhtuan.csdlsuport.common.Constant;
import vn.name.hoanhtuan.csdlsuport.common.EnumResultCode;
import vn.name.hoanhtuan.csdlsuport.model.ResponseBase;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ResponseUtils {
    public static ResponseEntity<ResponseBase> response(ResponseBase body){

        if(ObjectUtils.isEmpty(body)){
            body = new ResponseBase(EnumResultCode.SYSTEM_ERROR);
        }

        writeLogBody(JacksonUtils.toJSonString(body));

        //String requestId = MDC.get(Constant.REQUEST_ID);
        MDC.clear();

       // return ResponseEntity.ok().header("{}", requestId).body(body);
        return ResponseEntity.ok().body(body);
    }

    private static void writeLogBody(String body){
        LOGGER.info("Response body: {}", body);
    }

}
