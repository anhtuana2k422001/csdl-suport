package vn.name.hoanhtuan.csdlsuport.excepion;

import lombok.Getter;
import vn.name.hoanhtuan.csdlsuport.common.EnumResultCode;
import vn.name.hoanhtuan.csdlsuport.model.ErrorMessage;

public class CSDLException extends RuntimeException{
    @Getter
    private final ErrorMessage errorMessage;

    public CSDLException(ErrorMessage errorBean) {
        super();
        this.errorMessage = errorBean;
    }

    public CSDLException(){
        this(new ErrorMessage(EnumResultCode.SYSTEM_ERROR));
    }

    public CSDLException(ErrorMessage errorBean, String message){
        super(message);
        this.errorMessage = errorBean;
    }

    public CSDLException(ErrorMessage errorBean, String message, Throwable throwable) {
        super(message, (throwable.getCause() != null ? throwable.getCause() : throwable));
        this.errorMessage = errorBean;
    }

    public CSDLException(ErrorMessage errorBean , Throwable cause) {
        this(errorBean, null, cause);
    }

}
