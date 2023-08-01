package vn.name.hoanhtuan.csdlsuport.utils;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import vn.name.hoanhtuan.csdlsuport.common.Constant;
import vn.name.hoanhtuan.csdlsuport.excepion.CSDLException;
import vn.name.hoanhtuan.csdlsuport.model.ErrorMessage;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class CommonUtil {
    public static void  writeLogRequestId(String requestId){
        MDC.put(Constant.REQUEST_ID, requestId);
    }

    public static void handleException(Exception ex ) throws RuntimeException{
        if(ex instanceof CSDLException){
            CSDLException exception = new CSDLException(((CSDLException) ex).getErrorMessage(), ex.getMessage());
            if(StringUtils.isBlank(exception.getMessage())){
                LOGGER.info("Error message: {}", exception.getMessage());
            }
            throw exception;
        }

        LOGGER.error(Constant.ERROR, ex);
        ErrorMessage errorMessage = new ErrorMessage();
        throw new CSDLException(errorMessage, ex.getMessage(), ex);
    }
}
