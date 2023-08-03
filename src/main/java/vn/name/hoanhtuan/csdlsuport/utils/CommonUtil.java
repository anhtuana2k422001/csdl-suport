package vn.name.hoanhtuan.csdlsuport.utils;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import vn.name.hoanhtuan.csdlsuport.common.Constant;
import vn.name.hoanhtuan.csdlsuport.excepion.CSDLException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class CommonUtil {

    public static void  writeLogRequestId(String requestId){
        MDC.put(Constant.REQUEST_ID, requestId);
    }

    public static void handleException(Exception ex ){
        if (ex instanceof CSDLException exception && !StringUtils.isBlank(exception.getMessage()))
            LOGGER.info(Constant.EXCEPTION_MESSAGE, exception.getMessage());
        else
            LOGGER.error(Constant.EXCEPTION , ex.getMessage(), ex);
    }
}
