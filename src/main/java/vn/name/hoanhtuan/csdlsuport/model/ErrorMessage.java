package vn.name.hoanhtuan.csdlsuport.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import vn.name.hoanhtuan.csdlsuport.common.EnumResultCode;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorMessage {
    protected String code;
    protected String message;

    public ErrorMessage(){
        this.code = EnumResultCode.SYSTEM_ERROR.getCode();
        this.message = EnumResultCode.SYSTEM_ERROR.getMessage();
    }

    public ErrorMessage(EnumResultCode enumResultCode){
        this.code = enumResultCode.getCode();
        this.message = enumResultCode.getMessage();
    }

}
