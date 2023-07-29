package vn.name.hoanhtuan.csdlsuport.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public enum EnumResultCode {
    SUCCESS("000", "Success"),
    INVALID_REQUEST("001", "Invalid resquest"),
    SYSTEM_ERROR("999", "System Error"),

    ;
    private final String code;
    private final String message;
}
