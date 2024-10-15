package vn.name.hoanhtuan.csdlsuport.service;

import vn.name.hoanhtuan.csdlsuport.model.ResponseBase;
import vn.name.hoanhtuan.csdlsuport.model.csdl.request.RequestBaoDong;
import vn.name.hoanhtuan.csdlsuport.model.csdl.request.RequestCSDLSupport;

public interface CSDLService {
    ResponseBase timBaoDong(RequestBaoDong request);
    ResponseBase phanTichCSDL(RequestCSDLSupport request);
    ResponseBase listExampleCSDL();
    ResponseBase listUserMobile();
}
