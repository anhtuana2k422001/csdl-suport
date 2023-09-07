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
    public static final String TIM_KHOA = "Dạng 1: Tìm Khóa";
    public static final String TIM_PHU_TOI_THIEU= "Dạng 2: Tìm PTH tối thiểu";
    public static final String TIM_DANG_CHUAN = "Dạng 3: Tìm Dạng chuẩn";
    public static final String RESULT_DANG_CHUAN_1 = "Kết luận: Vậy lược đồ quan hệ chỉ đạt dạng chuẩn 1NF";
    public static final String RESULT_DANG_CHUAN_2 = "Kết luận: Vậy Lược đồ quan hệ chỉ đạt dạng chuẩn 2NF";
    public static final String RESULT_DANG_CHUAN_3 = "Kết luận: Vậy lược đồ quan hệ chỉ đạt dạng chuẩn 3NF";
    public static final String RESULT_DANG_CHUAN_BC = "Kết luận: Đạt dạng chuẩn BCNF";
}
