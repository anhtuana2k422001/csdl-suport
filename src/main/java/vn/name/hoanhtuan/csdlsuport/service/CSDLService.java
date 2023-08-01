package vn.name.hoanhtuan.csdlsuport.service;

import vn.name.hoanhtuan.csdlsuport.model.ResponseBase;
import vn.name.hoanhtuan.csdlsuport.model.csdl.request.RequestBaoDong;

public interface CSDLService {
    ResponseBase timBaoDong(RequestBaoDong request);
}
