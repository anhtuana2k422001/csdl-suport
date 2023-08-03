package vn.name.hoanhtuan.csdlsuport.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public enum EnumResultCode {
    SUCCESS("000", "Success"),
    INVALID_REQUEST("001", "Invalid request"),
    SYSTEM_ERROR("999", "System Error"),
    INVALID_PARAM_REQUEST("REQUEST_001", "Invalid request"),
    REQUIRED_REQUEST_ID("REQUEST_002", "RequestId is required"),
    REQUIRED_ATTRIBUTE_SET("REQUEST_003", "AttributeSet is required"),
    REQUIRED_DEPENDENCY_CHAIN("REQUEST_004", "DependencyChain is required"),
    REQUIRED_SERVICE("REQUEST.005", "Service is required"),
    ;
    private final String code;
    private final String message;
}
