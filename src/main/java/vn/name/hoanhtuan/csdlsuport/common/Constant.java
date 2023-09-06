package vn.name.hoanhtuan.csdlsuport.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constant {
    public static final String SUCCESS_CODE = "000";
    public static final String REQUEST_ID = "requestId";
    public static final String REQUEST_BODY = "Request body: {}";
    public static final String RESPONSE_BODY = "Response body: {}";
    public static final String EXCEPTION = "Exception: {}";
    public static final String EXCEPTION_MESSAGE = "Error message: {}";
    public static final String PHU_THUOC_HAM_VALID = "PTH hop le: {}";
    public static final String BAO_DONG_VALID = "Bao dong hop le: {}";
    public static final String LOG_ITEM_PTH = "Item PTH: {} - ";
    public static final String LOG_ITEM_BAO_DONG = "ItemChar Baodong: {} - ";
}
