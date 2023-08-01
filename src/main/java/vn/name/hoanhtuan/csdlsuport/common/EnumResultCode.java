package vn.name.hoanhtuan.csdlsuport.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public enum EnumResultCode {
    SUCCESS("000", "Success"),
    INVALID_REQUEST("001", "Invalid request"),
    SYSTEM_ERROR("999", "System Error"),
    INVALID_PARAM_REQUEST("REQUEST.001", "Invalid request"),
    REQUIRED_REQUEST_ID("REQUEST.002", "RequestId is required"),
    REQUIRED_ATTRIBUTE_SET("REQUEST.003", "AttributeSet is required"),
    REQUIRED_DEPENDENCY_CHAIN("REQUEST.004", "DependencyChain is required"),
    REQUIRED_SERVICE("REQUEST.005", "Service is required"),

    ;
    private final String code;
    private final String message;

    public static EnumResultCode findCode(String status){
        EnumResultCode[] var1 = values();

        for (EnumResultCode type : var1){
            if(type.code.equals(status)){
                return type;
            }
        }
        return SYSTEM_ERROR;
    }
}
