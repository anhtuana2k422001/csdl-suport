package vn.name.hoanhtuan.csdlsuport.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import vn.name.hoanhtuan.csdlsuport.common.Constant;
import vn.name.hoanhtuan.csdlsuport.common.EnumResultCode;
import vn.name.hoanhtuan.csdlsuport.model.ResponseBase;
import vn.name.hoanhtuan.csdlsuport.model.csdl.request.RequestBaoDong;
import vn.name.hoanhtuan.csdlsuport.model.csdl.response.ResponseCSDLSupport;
import vn.name.hoanhtuan.csdlsuport.service.CSDLService;
import vn.name.hoanhtuan.csdlsuport.service.Handle;
import vn.name.hoanhtuan.csdlsuport.validate.ValidationCSDL;

import java.util.List;

@Service
@Slf4j
public class CSDLServiceImpl implements CSDLService {

    @Override
    public ResponseBase timBaoDong(RequestBaoDong request) {

        List<String> listPTH; // danh sách phụ thuộc hàm
        String baoDong = Handle.BaoDong(request.getAttributeSet());
        String tapPTH = request.getDependencyChain();

        EnumResultCode resultValidatePTH = ValidationCSDL.validatePTH(tapPTH);
        if (!Constant.SUCCESS_CODE.equals(resultValidatePTH.getCode())) {
            return new ResponseBase(resultValidatePTH);
        }

        listPTH = Handle.XuLyPhuThuocHam(tapPTH);
        if(listPTH == null) {
            return new ResponseBase(EnumResultCode.SYSTEM_ERROR);
        }

        EnumResultCode validateValid =  ValidationCSDL.validateCLSD(baoDong, listPTH);
        if (!Constant.SUCCESS_CODE.equals(validateValid.getCode())) {
            return new ResponseBase(validateValid);
        }

        // Gán lại PTH đã loại bỏ thuộc tính xác định dư thừa
        listPTH = Handle.BoThuocTinhPTHThua(listPTH);

        // Handle code
        String properties = Handle.uniqueKyTuSapXep(request.getProperties());

        if(StringUtils.isEmpty(properties)){
            return new ResponseBase(EnumResultCode.SYSTEM_ERROR);
        }

        for (int i = 0; i < properties.length(); i++) {
            String index = String.valueOf(properties.charAt(i));
            if (!baoDong.contains(index)) {
                LOGGER.info("Properties item {}: - "+ EnumResultCode.PROPERTIES_ERROR.getMessage(), index);
                return new ResponseBase(EnumResultCode.PROPERTIES_ERROR);
            }
        }

        String result = Handle.TimBaoDong(properties, listPTH);
        String dataResponse = "{" + properties + "}+ = " + result;

        return ResponseCSDLSupport.builder()
                .enumResultCode(EnumResultCode.SUCCESS)
                .data(dataResponse)
                .build();
    }
}
