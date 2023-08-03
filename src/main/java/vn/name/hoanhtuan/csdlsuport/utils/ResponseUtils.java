package vn.name.hoanhtuan.csdlsuport.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import vn.name.hoanhtuan.csdlsuport.common.EnumResultCode;
import vn.name.hoanhtuan.csdlsuport.model.ResponseBase;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ResponseUtils {
    public static ResponseEntity<ResponseBase> response(ResponseBase body){
        if(ObjectUtils.isEmpty(body)){
            body = new ResponseBase(EnumResultCode.SYSTEM_ERROR);
        }

        MDC.clear();
        return ResponseEntity.ok().body(body);
    }

}
