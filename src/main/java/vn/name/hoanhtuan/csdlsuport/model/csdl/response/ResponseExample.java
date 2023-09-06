package vn.name.hoanhtuan.csdlsuport.model.csdl.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import vn.name.hoanhtuan.csdlsuport.common.EnumResultCode;
import vn.name.hoanhtuan.csdlsuport.model.ResponseBase;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseExample extends ResponseBase {
    private List<ExampleCSDL> data;

    @Builder
    public ResponseExample(EnumResultCode enumResultCode, List<ExampleCSDL> data) {
        super(enumResultCode);
        this.data = data;
    }
}
