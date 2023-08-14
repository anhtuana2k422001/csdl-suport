package vn.name.hoanhtuan.csdlsuport.model.csdl.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataCSDL {
    private String primaryKey; // Khóa chính
    private String minimalCove; // Phủ tối thiểu
    private String normalForm; // Dạng chuẩn
}
