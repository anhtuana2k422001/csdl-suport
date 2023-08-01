package vn.name.hoanhtuan.csdlsuport.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import vn.name.hoanhtuan.csdlsuport.common.EnumResultCode;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestBase {
    protected String requestId;

    public ErrorMessage validateInput(){
        if(StringUtils.isBlank(this.requestId)){
            return new ErrorMessage(EnumResultCode.REQUIRED_REQUEST_ID);
        }
        return null;
    }
}







