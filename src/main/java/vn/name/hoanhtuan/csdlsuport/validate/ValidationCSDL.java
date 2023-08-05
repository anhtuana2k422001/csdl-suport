package vn.name.hoanhtuan.csdlsuport.validate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ValidationCSDL {

    private static final String VALIDATE_PTH = "Phụ thuộc hàm không hợp lệ";

    public boolean validatePTH(String tapPTH){

        // Không nhận ký ký tự số
        for (int i = 0; i < tapPTH.length(); i++) {
            if(tapPTH.charAt(i) <= 57 && tapPTH.charAt(i) >= 48){
                LOGGER.info("Phụ thuộc hàm không nhận ký tự số  !");
                return false;
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
                    LOGGER.info("PTH hop le: {}", tapPTH);
                }
            }else{
                LOGGER.info("Tập phụ thuộc hàm mới nhập không hợp lệ!. Chương trình chỉ phân biết được các ký tự trong bảng chữ cái " +
                        "tiếng anh và một số ký tự hỗ trợ phụ thuộc hàm." + item + " là ký tự không nhận diện được");
                LOGGER.info(String.valueOf(tapPTH.charAt(i)));
                return false;
            }
        }
        return true;
    }

    public boolean validateInput(String baodong, List<String> listPTH){

        // Không nhập chữ cái đặc biệt ký hiệu
        for (int i = 0; i < baodong.length(); i++) {
            if(baodong.charAt(i)== 85 || baodong.charAt(i) == 81 || baodong.charAt(i) == 70 ){
                LOGGER.info("Tập thuộc tính không hợp lệ " +
                        "Chú ý: Không dùng các ký tự ký hiệu của lược đồ quan hệ sau " +
                        "+) F là ký hiệu phụ thuộc hàm " +
                        "+) U, Q là ký hiệu tập thuộc tính " +
                        "=> Giải quyết: Nếu đề bài yêu cầu dùng ký tự đó bạn có thể thay thế ký hiệu khác chưa dùng đến " +
                        "trong tập thuộc tính và tập thuốc tính với tập phụ thuộc hàm phải đồng bộ");
                return false;
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
                    LOGGER.info("Bao dong hop le: {}", baodong);
                }
            }else{
                LOGGER.info("Tập thuộc tính mới nhập không hợp lệ! " +
                        "Chương trình chỉ phân biết được các ký tự trong bảng chữ cái tiếng anh và một số ký tự sau: " +
                        "dấu ngoặc mở: () " +
                        "+) dấu phẩy: , " +
                        "+) dấu chấm: . " +
                        "+) dấu chấm phẩy: ; " +
                        "+) dấu ngoặc ngọn: {} " +
                        "+) dấu bằng: = ");
                return false;
            }
        }

        if(baodong.isEmpty()){
            LOGGER.info("Vui lòng nhập tập thuộc tính !");
            return false;
        }

        for(String item : listPTH){
            item = item.replace("→", "");
            for(int i=0; i< item.length(); i++){
                String index = String.valueOf(item.charAt(i));
                if(!baodong.contains(index)){
                    LOGGER.info("Thuộc tính " + index +" của phụ thuộc không nằm trong với tập thuộc tính Q (U)");
                    return false;
                }
            }

        }

        for (String pth : listPTH) {
            if(!pth.contains("→")){
                LOGGER.info(VALIDATE_PTH);
                return false;
            }
            try{
                String veTr = pth.split("→")[0];
                String vePh = pth.split("→")[1];
                if(veTr.isEmpty() || vePh.isEmpty()){
                    LOGGER.info(VALIDATE_PTH);
                    return false;
                }
            }catch(Exception e){
                LOGGER.info(VALIDATE_PTH);
                return false;
            }

        }
        return true;
    }

}
