package vn.name.hoanhtuan.csdlsuport.model.csdl.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataCSDL {
    private InformationCSDL information; // Thông tin
    private Content primaryKey; // Khóa chính
    private Content minimalCove; // Phủ tối thiểu
    private Content normalForm; // Dạng chuẩn
}
