package vn.name.hoanhtuan.csdlsuport.validate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import vn.name.hoanhtuan.csdlsuport.common.Constant;
import vn.name.hoanhtuan.csdlsuport.common.EnumResultCode;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ValidationCSDL {

    public static EnumResultCode validatePTH(String tapPTH){
        LOGGER.info("TapPTH: {}", tapPTH);

        // Không nhận ký ký tự số
        for (int i = 0; i < tapPTH.length(); i++) {
            int itemChar = tapPTH.charAt(i);
            if(itemChar <= 57 && itemChar >= 48){
                LOGGER.info(Constant.LOG_ITEM_PTH + EnumResultCode.DEPENDENCY_NUMBER_INVALID.getMessage(), itemChar);
                return EnumResultCode.DEPENDENCY_NUMBER_INVALID;
            }
        }

        for (int i = 0; i < tapPTH.length(); i++) {
            String item =   String.valueOf(tapPTH.charAt(i));
            int itemChar = tapPTH.charAt(i);
            if(itemChar <=90 && itemChar >=65
                    || itemChar <= 122 && itemChar >= 97
                    || itemChar == 29 || itemChar == 28
                    || itemChar == 123 || itemChar == 125
                    || itemChar == 45 || itemChar == 62
                    || StringUtils.equalsAny(item, "", "", "?", "->", "-->", ".", ";", ",",
                    "→", "{", "}", " ", "(", ")", "=")){
                if(i == tapPTH.length()-1){
                    LOGGER.info(Constant.PHU_THUOC_HAM_VALID, tapPTH);
                }
            }else{
                LOGGER.info(Constant.LOG_ITEM_PTH + EnumResultCode.DEPENDENCY_ERROR_LOGIC.getMessage(), itemChar);
                return EnumResultCode.DEPENDENCY_ERROR_LOGIC;
            }
        }
        return EnumResultCode.SUCCESS;
    }

    public static EnumResultCode validateCLSD(String baodong, List<String> listPTH){
        LOGGER.info("Baodong: {}, ListPTH: {}", baodong, listPTH);

        // Không nhập chữ cái đặc biệt ký hiệu
        for (int i = 0; i < baodong.length(); i++) {
            int itemChar = baodong.charAt(i);
            if(itemChar == 85 || itemChar == 81 || itemChar == 70 ){
                LOGGER.info(Constant.LOG_ITEM_BAO_DONG + EnumResultCode.ATTRIBUTE_SET_ERROR_REG.getMessage(), itemChar);
                return EnumResultCode.ATTRIBUTE_SET_ERROR_REG;
            }
        }


        for (int i = 0; i < baodong.length(); i++) {
            int itemCharAt = baodong.charAt(i);
            if(itemCharAt <=90 && itemCharAt >=65
                    || itemCharAt <= 122 && itemCharAt >= 97
                    || itemCharAt == 29 || itemCharAt == 28
                    || itemCharAt == 123 || itemCharAt == 125
                    || itemCharAt == 61)
            {
                if(i == baodong.length()-1){
                    LOGGER.info(Constant.BAO_DONG_VALID, baodong);
                }
            }else{
                LOGGER.info(Constant.LOG_ITEM_BAO_DONG + EnumResultCode.ATTRIBUTE_SET_ERROR_LOGIC.getMessage(), itemCharAt);
                return EnumResultCode.ATTRIBUTE_SET_ERROR_LOGIC;
            }
        }


        for(String item : listPTH){
            item = item.replace("→", "");
            for(int i=0; i< item.length(); i++){
                String index = String.valueOf(item.charAt(i));
                if(!baodong.contains(index)){
                    LOGGER.info("index PTH: {} - "+ EnumResultCode.DEPENDENCY_ERROR.getMessage(), index);
                    return EnumResultCode.DEPENDENCY_ERROR;
                }
            }

        }

        for (String pth : listPTH) {
            if(!pth.contains("→")){
                LOGGER.info("PTH: {} - " + EnumResultCode.DEPENDENCY_INVALID.getMessage(), pth);
                return EnumResultCode.DEPENDENCY_INVALID;
            }
            try{
                String veTr = pth.split("→")[0];
                String vePh = pth.split("→")[1];
                if(veTr.isEmpty() || vePh.isEmpty()){
                    LOGGER.info(EnumResultCode.DEPENDENCY_INVALID.getMessage());
                    return EnumResultCode.DEPENDENCY_INVALID;
                }
            }catch(Exception e){
                LOGGER.info(EnumResultCode.DEPENDENCY_INVALID.getMessage());
                return EnumResultCode.DEPENDENCY_INVALID;
            }

        }
        return EnumResultCode.SUCCESS;
    }

}
