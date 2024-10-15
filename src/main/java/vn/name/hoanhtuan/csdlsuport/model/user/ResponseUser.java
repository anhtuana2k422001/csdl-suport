package vn.name.hoanhtuan.csdlsuport.model.user;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import vn.name.hoanhtuan.csdlsuport.common.EnumResultCode;
import vn.name.hoanhtuan.csdlsuport.model.ResponseBase;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUser extends ResponseBase {
    private UserMobile userMobile;

    @Builder
    public ResponseUser(EnumResultCode enumResultCode, UserMobile userMobile) {
        super(enumResultCode);
        this.userMobile = userMobile;
    }
}
