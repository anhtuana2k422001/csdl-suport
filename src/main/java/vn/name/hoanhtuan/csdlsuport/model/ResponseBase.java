package vn.name.hoanhtuan.csdlsuport.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import vn.name.hoanhtuan.csdlsuport.common.EnumResultCode;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseBase {
    private String resultCode;
    private String resultMessage;

    public ResponseBase() {
        resultCode = EnumResultCode.SUCCESS.getCode();
        resultMessage = EnumResultCode.SUCCESS.getMessage();
    }

    public ResponseBase(EnumResultCode enumResultCode){
        this.resultCode = enumResultCode.getCode();
        this.resultMessage = enumResultCode.getMessage();
    }

    public ResponseBase(ErrorMessage errorMessage) {
        this.resultCode = errorMessage.getCode();
        this.resultMessage = errorMessage.getMessage();
    }

    public ResponseBase(String resultCode, String resultMessage){
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }
}
