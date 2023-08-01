package vn.name.hoanhtuan.csdlsuport.model.csdl.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import vn.name.hoanhtuan.csdlsuport.common.EnumResultCode;
import vn.name.hoanhtuan.csdlsuport.model.ErrorMessage;
import vn.name.hoanhtuan.csdlsuport.model.RequestBase;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RequestCSDLSupport extends RequestBase {
    private String attributeSet;
    private String dependencyChain;

    @Override
    public ErrorMessage validateInput(){
        ErrorMessage errorMessage = super.validateInput();
        if(null != errorMessage){
            return errorMessage;
        }

        if(StringUtils.isBlank(attributeSet)){
            return new ErrorMessage(EnumResultCode.REQUIRED_ATTRIBUTE_SET);
        }

        if(StringUtils.isBlank(dependencyChain)){
            return new ErrorMessage(EnumResultCode.REQUIRED_DEPENDENCY_CHAIN);
        }

        return null;
    }
}
