package vn.name.hoanhtuan.csdlsuport.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public enum EnumResultCode {
    SUCCESS("000", "Success"),
    SYSTEM_ERROR("999", "System Error"),
    PROPERTIES_ERROR("002", "Ký tự của thuộc tính tìm bao đóng không nằm trong tập thuốc tính Q(U)"),
    DEPENDENCY_NUMBER_INVALID("003", "Phụ thuộc hàm không nhận ký tự số !"),
    DEPENDENCY_INVALID("004", "Phụ thuộc hàm không hợp lệ"),
    DEPENDENCY_ERROR("005", "Thuộc tính của phụ thuộc hàm không nằm trong với tập thuộc tính Q (U)"),
    DEPENDENCY_ERROR_LOGIC("006", "Tập phụ thuộc hàm mới nhập không hợp lệ. Chương trình chỉ phân biết được các ký tự trong bảng chữ cái tiếng anh và một số ký tự hỗ trợ phụ thuộc hàm."),
    ATTRIBUTE_SET_ERROR_REG("007", "Tập thuộc tính không hợp lệ! Chú ý: Không dùng các ký tự ký hiệu của lược đồ quan hệ sau (1) F là ký hiệu phụ thuộc hàm - (2) U, Q là ký hiệu tập thuộc tính => Giải quyết: Nếu đề bài yêu cầu dùng ký tự đó bạn có thể thay thế ký hiệu khác chưa dùng đến trong tập thuộc tính và tập thuốc tính với tập phụ thuộc hàm phải đồng bộ"),
    ATTRIBUTE_SET_ERROR_LOGIC("008", "Tập thuộc tính mới nhập không hợp lệ. Chương trình chỉ phân biết được các ký tự trong bảng chữ cái tiếng anh và một số ký tự sau: (1) dấu ngoặc mở: () - (2) dấu phẩy: , - (3) dấu chấm: . - (4) dấu chấm phẩy: ; - (5) dấu ngoặc ngọn: {} - (6) dấu bằng: ="),

    // INVALID REQUEST
    INVALID_PARAM_REQUEST("100", "Đầu vào không hợp lệ"),
    REQUIRED_REQUEST_ID("101", "RequestId là bắt buộc"),
    REQUIRED_ATTRIBUTE_SET("103", "AttributeSet là bắt buộc"),
    REQUIRED_DEPENDENCY_CHAIN("104", "DependencyChain là bắt buộc"),
    REQUIRED_PROPERTIES("105", "Properties là bắt buộc"),
    REQUIRED_DETAIL_EXPLANATION("106", "DetailExplanation là bắt buộc"),

    ;
    private final String code;
    private final String message;
}
