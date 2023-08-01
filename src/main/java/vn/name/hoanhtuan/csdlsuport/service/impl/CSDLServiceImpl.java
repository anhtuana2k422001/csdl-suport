package vn.name.hoanhtuan.csdlsuport.service.impl;

import org.springframework.stereotype.Service;
import vn.name.hoanhtuan.csdlsuport.common.EnumResultCode;
import vn.name.hoanhtuan.csdlsuport.model.ResponseBase;
import vn.name.hoanhtuan.csdlsuport.model.csdl.request.RequestBaoDong;
import vn.name.hoanhtuan.csdlsuport.model.csdl.response.ResponseCSDLSupport;
import vn.name.hoanhtuan.csdlsuport.service.CSDLService;

@Service
public class CSDLServiceImpl implements CSDLService {
    @Override
    public ResponseBase timBaoDong(RequestBaoDong request) {
        // Handle code

        // Xử lý code
        String data = "{ AC }+ = ACDEGB";
        return ResponseCSDLSupport.builder()
                .enumResultCode(EnumResultCode.SUCCESS)
                .data(data)
                .build();
    }
}
