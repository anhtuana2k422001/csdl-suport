package vn.name.hoanhtuan.csdlsuport.model.csdl.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import vn.name.hoanhtuan.csdlsuport.common.EnumResultCode;
import vn.name.hoanhtuan.csdlsuport.model.ResponseBase;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseBaoDong extends ResponseBase {
    private String data;

    @Builder
    public ResponseBaoDong(EnumResultCode enumResultCode, String data) {
        super(enumResultCode);
        this.data = data;
    }
}
