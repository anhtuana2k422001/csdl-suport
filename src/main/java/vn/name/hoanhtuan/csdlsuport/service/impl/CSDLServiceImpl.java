package vn.name.hoanhtuan.csdlsuport.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import vn.name.hoanhtuan.csdlsuport.common.Constant;
import vn.name.hoanhtuan.csdlsuport.common.EnumResultCode;
import vn.name.hoanhtuan.csdlsuport.model.ResponseBase;
import vn.name.hoanhtuan.csdlsuport.model.csdl.request.RequestBaoDong;
import vn.name.hoanhtuan.csdlsuport.model.csdl.request.RequestCSDLSupport;
import vn.name.hoanhtuan.csdlsuport.model.csdl.response.*;
import vn.name.hoanhtuan.csdlsuport.service.CSDLService;
import vn.name.hoanhtuan.csdlsuport.service.Handle;
import vn.name.hoanhtuan.csdlsuport.validate.ValidationCSDL;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CSDLServiceImpl implements CSDLService {
    private List<String> listToiThieuPTH = new ArrayList<>();// List khóa để xét DC
    private static final List<String> listTapKhoa = new ArrayList<>();// List khóa để xét DC
    private final InformationCSDL infoCSDL = new InformationCSDL();
    private boolean checkKhoaDuyNhat = false;


    @Override
    public ResponseBase timBaoDong(RequestBaoDong request) {

        List<String> listPTH; // danh sách phụ thuộc hàm
        String baoDong = Handle.baoDong(request.getAttributeSet());
        String tapPTH = request.getDependencyChain();

        EnumResultCode resultValidatePTH = ValidationCSDL.validatePTH(tapPTH);
        if (!Constant.SUCCESS_CODE.equals(resultValidatePTH.getCode())) {
            return new ResponseBase(resultValidatePTH);
        }

        listPTH = Handle.xuLyPhuThuocHam(tapPTH);
        if(listPTH.isEmpty()) {
            return new ResponseBase(EnumResultCode.ERROR_HANDLER_PTH);
        }

        EnumResultCode validateValid =  ValidationCSDL.validateCLSD(baoDong, listPTH);
        if (!Constant.SUCCESS_CODE.equals(validateValid.getCode())) {
            return new ResponseBase(validateValid);
        }

        // Gán lại PTH đã loại bỏ thuộc tính xác định dư thừa
        listPTH = Handle.boThuocTinhPTHThua(listPTH);

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

        String result = Handle.timBaoDong(properties, listPTH);
        String dataResponse = "{" + properties + "}+ = " + result;

        return ResponseBaoDong.builder()
                .enumResultCode(EnumResultCode.SUCCESS)
                .data(dataResponse)
                .build();
    }

    @Override
    public ResponseBase phanTichCSDL(RequestCSDLSupport request) {
        List<String> listPTH; // danh sách phụ thuộc hàm
        String tapPTH = request.getDependencyChain();

        String baoDong = Handle.baoDong(request.getAttributeSet());
        infoCSDL.setBaodongLDQH(baoDong);
        if(StringUtils.isEmpty(baoDong))
            infoCSDL.setBaodongLDQH("{Ø}");

        listPTH = Handle.xuLyPhuThuocHam(tapPTH);
        if(listPTH.isEmpty()) {
            return new ResponseBase(EnumResultCode.ERROR_HANDLER_PTH);
        }

        EnumResultCode validateValid =  ValidationCSDL.validateCLSD(baoDong, listPTH);
        if (!Constant.SUCCESS_CODE.equals(validateValid.getCode())) {
            return new ResponseBase(validateValid);
        }

        // Gán lại PTH đã loại bỏ thuộc tính xác định dư thừa
        listPTH = Handle.boThuocTinhPTHThua(listPTH);

        // Tìm khóa
        String tapVeTrai = Handle.veTraiPTH(listPTH);
        infoCSDL.setTapVeTrai(tapVeTrai);
        if(StringUtils.isEmpty(tapVeTrai))
            infoCSDL.setTapVeTrai("{Ø}");

        String tapVePhai = Handle.vePhaiPTH(listPTH);
        infoCSDL.setTapVePhai(tapVePhai);
        if(StringUtils.isEmpty(tapVePhai))
            infoCSDL.setTapVePhai("{Ø}");

        String nguonPTH =  Handle.tapNguon(baoDong, tapVePhai);
        infoCSDL.setTapThuocTinhNguonN(nguonPTH);
        if(StringUtils.isEmpty(nguonPTH))
            infoCSDL.setTapThuocTinhNguonN("{Ø}");

        String trungGianPTH =  Handle.tapTrungGian(tapVePhai, tapVeTrai);
        infoCSDL.setTapTrungGianM(trungGianPTH);
        if(StringUtils.isEmpty(trungGianPTH))
            infoCSDL.setTapTrungGianM("{Ø}");

        Content kqKhoaDuyNhat = timkhoa(nguonPTH, listPTH, baoDong, tapPTH, trungGianPTH);

        // Tìm phu toi thieu
        Content ketQuaFTT = phuToiThieu(listPTH);

        // Tìm dạng chuẩn
        Content ketQuaDangVhuan = timDangChuan(nguonPTH);

        DataCSDL dataResponse = DataCSDL.builder()
                .information(infoCSDL)
                .primaryKey(kqKhoaDuyNhat)
                .minimalCove(ketQuaFTT)
                .normalForm(ketQuaDangVhuan)
                .build();

        return ResponseCSDLSupport.builder()
                .enumResultCode(EnumResultCode.SUCCESS)
                .data(dataResponse)
                .build();
    }

    // Hàm tìm phụ thuộc hàm tối thiểu
    public Content phuToiThieu(List<String> listPTH) {
        Content normalFormContent = new Content();
        normalFormContent.setTitle(Constant.TIM_PHU_TOI_THIEU);
        List<Detail> details = new ArrayList<>();

        // Trường hợp không có phụ thuộc hàm
        if (listPTH.isEmpty()) {
            Detail detail = new Detail();
            List<String> text = new ArrayList<>();
            detail.setStep("- Xét phụ thuộc hàm:");

            text.add("Vì không có phụ thuộc hàm nào");
            text.add("=> phụ thuộc hàm tối thiểu là: Ftt = {Ø}");

            detail.setText(text);
            details.add(detail);

            normalFormContent.setValue(details);
            normalFormContent.setResult("Phụ thuộc hàm tối thiểu là: Ftt = {Ø}");

            return normalFormContent;
        }

        // trường hợp chỉ có 1 PTh mà không thể phân rã
        if (listPTH.size() == 1) {
            String vePh = listPTH.get(0).split("→")[1];
            if (vePh.length() == 1) {
                Detail detail = new Detail();
                List<String> text = new ArrayList<>();
                detail.setStep("- Xét phụ thuộc hàm");

                text.add("Vì chỉ có 1 phụ thuộc hàm => phụ thuộc hàm tối thiểu là: Ftt = {Ø}");
                text.add("Phụ thuộc hàm tối thiểu là: Ftt = { " + listPTH.get(0) + " }");

                detail.setText(text);
                details.add(detail);

                normalFormContent.setValue(details);
                normalFormContent.setResult("Phụ thuộc hàm tối thiểu là: Ftt = {Ø}");

                return normalFormContent;
            }
        }
        //=== Bước 1:  Phân rã phụ thuộc hàm ở vế phải
        List<String> listPTHPhanRa = new ArrayList<>(); // phân rã PTH ở vế phải
        for (String pth : listPTH) {
            String veTrai = pth.split("→")[0];
            String vePhai = pth.split("→")[1];
            String itemF = "";
            itemF = itemF.concat(veTrai).concat("→");
            for (int i = 0; i < vePhai.length(); i++) {
                String indexVP = String.valueOf(vePhai.charAt(i));
                itemF = itemF.concat(indexVP);
                listPTHPhanRa.add(itemF);
                itemF = itemF.replace(indexVP, "");
            }
        }
        String fToiThieu = "";
        for (int i = 0; i < listPTHPhanRa.size(); i++) {
            fToiThieu = fToiThieu.concat(listPTHPhanRa.get(i));
            if (i != listPTHPhanRa.size() - 1) {
                fToiThieu = fToiThieu.concat(", ");
            }
        }

        Detail detail1 = new Detail();
        List<String> text1 = new ArrayList<>();
        detail1.setStep("Bước 1: Phân rã phụ thuộc hàm");
        text1.add("Kết quả: F1 = {" + fToiThieu + "}");
        detail1.setText(text1);
        details.add(detail1);

        Detail detail2 = new Detail();
        List<String> text2 = new ArrayList<>();
        detail2.setStep("Bước 2: Loại bỏ vế trái dư thừa");

        //=== Bước 2:  Lược bỏ vế trái PTH
        String checkPTHUnique = "";
        List<String> listPTHBoVT = new ArrayList<>();
        List<String> listPTHBoVTUnique = new ArrayList<>();
        for (String pth : listPTHPhanRa) {
            String veTrai = pth.split("→")[0];
            String vePhai = pth.split("→")[1];
            String itemF = pth; // loại bỏ PTH vế trái
            if (veTrai.length() > 1) {
                text2.add(" - Xét: " + pth + ":");
                for (int i = 0; i < veTrai.length(); i++) {
                    String indexVT = String.valueOf(veTrai.charAt(i));
                    String conlaiVT = veTrai;
                    conlaiVT = conlaiVT.replace(indexVT, "");
                    String baodong = Handle.timBaoDong(conlaiVT, listPTHPhanRa);
                    if (baodong.contains(vePhai)) {
                        text2.add("Nếu bỏ " + indexVT + ": {" + conlaiVT + "}+ = " + baodong + " có chứa " + vePhai + " => " + indexVT + " dư thừa");
                        itemF = "";
                        itemF = itemF.concat(conlaiVT).concat("→").concat(vePhai);
                    } else {
                        text2.add("Nếu bỏ " + indexVT + ": {" + conlaiVT + "}+ = " + baodong + " không chứa " + vePhai + " => " + indexVT + " dư thừa");
                    }
                }
            }

            if (!checkPTHUnique.contains(itemF)) {
                checkPTHUnique = checkPTHUnique.concat(itemF + ",");
                listPTHBoVTUnique.add(itemF);
            }
            listPTHBoVT.add(itemF); // chưa tối ưu
        }


        fToiThieu = ""; // Gán lại bằng rỗng
        for (int i = 0; i < listPTHBoVTUnique.size(); i++) {
            fToiThieu = fToiThieu.concat(listPTHBoVTUnique.get(i));
            if (i != listPTHBoVTUnique.size() - 1) {
                fToiThieu = fToiThieu.concat(", ");
            }
        }

        if (listPTHBoVT.size() > listPTHBoVTUnique.size()) {
            String fPthBoVT = ""; // Gán lại bằng rỗng
            for (int i = 0; i < listPTHBoVT.size(); i++) {
                fPthBoVT = fPthBoVT.concat(listPTHBoVT.get(i));
                if (i != listPTHBoVT.size() - 1) {
                    fPthBoVT = fPthBoVT.concat(", ");
                }
            }
            if (fToiThieu.length() != fPthBoVT.length()) {
                text2.add("=> Sau khi loại bỏ dưa thừa VT là: F1 = {" + fPthBoVT + "}");
            }
        }

        // Xuất kết quả bước 2
        text2.add("Kết quả: F2 = {" + fToiThieu + "}");
        detail2.setText(text2);
        details.add(detail2);


        //=== Bước 3: Lược bỏ PTH dư thừa
        Detail detail3 = new Detail();
        List<String> text3 = new ArrayList<>();
        detail3.setStep("Bước 3: Lược bỏ PTH dư thừa");

        // Trường hợp chỉ có 1 PTH
        if (listPTHBoVTUnique.size() == 1) {
            listToiThieuPTH = listPTHBoVTUnique;
            text3.add("Chỉ còn lại có 1 phụ thuộc hàm");
            detail3.setText(text3);
            details.add(detail3);
            normalFormContent.setValue(details);
            normalFormContent.setResult("Phụ thuộc hàm tối thiểu là: Ftt = { " + fToiThieu + " }");

            return normalFormContent;
        }

        // Sao chép list
        List<String> listPTHBoPTH = new ArrayList<>(listPTHBoVTUnique);

        List<String> listPTHToiThieu = new ArrayList<>();
        // Xử lý loại bỏ
        int vitri = 0;
        String itemFtt = "";
        String itemCuoi = listPTHBoVTUnique.get(listPTHBoVTUnique.size() - 1); // Thay thế cho vị trí xóa
        for (String pth : listPTHBoVTUnique) {
            String veTrai = pth.split("→")[0];
            String vePhai = pth.split("→")[1];
            listPTHBoPTH.remove(vitri);
            if (pth.equals(itemCuoi)) { // Nếu xét PTH cuối
                listPTHBoPTH.remove(0); // Thay đổi PTH đầu tiên bằng PTh không dư thừa đã xác định
                listPTHBoPTH.add(0, itemFtt);
            }
            String baodong = Handle.timBaoDong(veTrai, listPTHBoPTH);
            if (baodong.contains(vePhai)) {
                if (itemFtt.isEmpty()) {
                    listPTHBoPTH.add(vitri, itemCuoi);
                } else {
                    listPTHBoPTH.add(vitri, itemFtt);
                }
                text3.add("Nếu xóa " + pth + " khỏi F2 thì: {" + veTrai + "}+ = " + baodong + " có chứa " + vePhai + " =>" + pth + " dư thừa");
            } else {
                listPTHBoPTH.add(vitri, pth);
                text3.add("Nếu xóa " + pth + " khỏi F2 thì: {" + veTrai + "}+ = " + baodong + " không chứa " + vePhai + " =>" + pth + " không dư thừa");
                listPTHToiThieu.add(pth);
                if (itemFtt.isEmpty()) {
                    itemFtt = pth; // Chưa có thì gán 1 lần đầu để thay thế
                }
            }
            vitri++;
        }

        fToiThieu = ""; // Gán lại bằng rỗng
        for (int i = 0; i < listPTHToiThieu.size(); i++) {
            fToiThieu = fToiThieu.concat(listPTHToiThieu.get(i));
            if (i != listPTHToiThieu.size() - 1) {
                fToiThieu = fToiThieu.concat(", ");
            }
        }

        listToiThieuPTH = listPTHToiThieu;// gán để dụng kế thừa tìm dạng chuẩn
        text3.add("Kết quả: F3 = { " + fToiThieu + " }");
        detail3.setText(text3);
        details.add(detail3);
        normalFormContent.setValue(details);
        normalFormContent.setResult("Kết luận: F tối thiểu là Ftt = { " + fToiThieu + " }");

        return normalFormContent;
    }

    private Content timkhoa(String nguonPTH, List<String> listPTH, String baoDong, String phuThuocHam, String trungGianPTH) {
        String tapKhoa = "";
        String thuoctinhM = "";

        Content normalFormContent = new Content();
        normalFormContent.setTitle(Constant.TIM_KHOA);
        List<Detail> details = new ArrayList<>();

        Detail detail = new Detail();
        List<String> text = new ArrayList<>();
        detail.setStep("Bước 1: Xét TH khóa duy nhất ");


        String baoDongNguon = Handle.timBaoDong(nguonPTH, listPTH); //Gọi
        infoCSDL.setTapThuocTinh("Q = (" + baoDong + ")");

        String tapPTH = Handle.tapPhuThuocHam(phuThuocHam);
        if (tapPTH.isEmpty()) {
            tapPTH = "Ø";
        }
        infoCSDL.setPhuThuocHam("F = " + tapPTH );

        String thuoctinh = "";
        if (listPTH.isEmpty()) {
            checkKhoaDuyNhat = true;

            text.add("Vì không có phụ thuộc hàm nên " + baoDong + " là siêu khóa");
            detail.setText(text);
            details.add(detail);
            normalFormContent.setValue(details);
            normalFormContent.setResult("Lược đồ có siêu khóa: " + baoDong);

            return normalFormContent;
        } else {
            if (!nguonPTH.isEmpty()) {
                for (int i = 0; i < nguonPTH.length(); i++) {
                    thuoctinh = thuoctinh.concat(String.valueOf(nguonPTH.charAt(i)));
                    if (i != nguonPTH.length() - 1) {
                        thuoctinh = thuoctinh.concat(", ");
                    }
                }
                String title = thuoctinh.concat(" là thuộc tính không xuất hiến bên vế phải");
                text.add(title);
                text.add("=> N = {" + thuoctinh + "}");

                if (baoDong.length() == baoDongNguon.length()) {
                    checkKhoaDuyNhat = true;
                    text.add("{" + nguonPTH + "}+ = " + baoDongNguon + " = Q+");
                    text.add("Vậy Q có khóa duy nhất là: K = N = {" + thuoctinh.replace(", ", "") + "}");
                    detail.setText(text);
                    details.add(detail);
                    normalFormContent.setValue(details);
                    normalFormContent.setResult("Lược đồ có 1 khóa duy nhất: " + thuoctinh.replace(", ", ""));

                    return normalFormContent;

                } else {
                    text.add("{" + nguonPTH + "}+ = " + baoDongNguon + " ≠ Q+");
                    detail.setText(text);
                    details.add(detail);
                    normalFormContent.setValue(details);
                }
            } else {
                thuoctinh = "Không có thuộc tính nào mà không xuất hiện bên vế phải";
                text.add(thuoctinh);
                text.add("=> N = {Ø}");
                detail.setText(text);
                details.add(detail);
                normalFormContent.setValue(details);
            }
        }


        // Trường hợp có tập khóa
        Detail detail1 = new Detail();
        List<String> text1 = new ArrayList<>();
        detail1.setStep("Bước 2: Xét TH có tập khóa");

        for (int i = 0; i < trungGianPTH.length(); i++) {
            thuoctinhM = thuoctinhM.concat(String.valueOf(trungGianPTH.charAt(i)));
            if (i != trungGianPTH.length() - 1) {
                thuoctinhM = thuoctinhM.concat(", ");
            }
        }

        text1.add("Ta có: M =  {" + thuoctinhM + "}" + " là thuộc tính xuất hiến cả hai vế ");
        text1.add("Bao đóng tập hội của Mi với N là: ");

        String tapConM = thuoctinhM.replace(", ", "");
        tapConM =  Handle.sapXepKyTu(tapConM);
        // Hội lại để tìm tất cả tập con của M
        List<String> listTapKhoaHienTai = new ArrayList<>(); // Khai báo

        List<String> listThuocTinhGhep = Handle.listTapTrungGianLienNhau(tapConM);
        List<String> listTapHoi = Handle.listTapTrungGianGhep(listThuocTinhGhep);
        List<String> listTapTrungGian = Handle.listTapTrungGian(listTapHoi);
        for (int i = 0; i < listTapTrungGian.size(); i++) {
            String temp = "";
            temp = temp.concat(nguonPTH).concat(listTapTrungGian.get(i));

            // Kiểm tra Mi hội N có chứa khóa trước đó hay không
            boolean checkSieuKhoa = false;
            for (String khoa : listTapKhoaHienTai) {
                int chieuDaiKhoa = 0;
                for (int j = 0; j < khoa.length(); j++) {
                    String itemKhoa = String.valueOf(khoa.charAt(j));
                    if (temp.contains(itemKhoa)) {
                        chieuDaiKhoa++;
                    }
                }
                if (chieuDaiKhoa == khoa.length()) {
                    //Tức là các Mi hội N này duyệt thấy đã chứa đủ số ký tự khóa
                    checkSieuKhoa = true;
                }
            }

            if (!checkSieuKhoa) {
                String baodongMi = Handle.timBaoDong(temp, listPTH);
                if (baodongMi.length() == baoDong.length()) {
                    text1.add("+) Xét " + temp + ": {" + temp + "}+ = " + baodongMi + " = Q+ => là khóa");
                    listTapKhoaHienTai.add(temp);
                } else {
                    text1.add("+) Xét " + temp + ": {" + temp + "}+ = " + baodongMi + " ≠ Q+");
                }
            }

            if (i == listTapTrungGian.size() - 1) {
                text1.add("=> Các thuộc tính Mi hội N còn lại chứa khóa ở trên nên là siêu khóa sẽ không cần xét");
            }
        }

        // Xuất ra các khoa của lược đồ Q
        String chuoiKhoa = "";
        for (int i = 0; i < listTapKhoaHienTai.size(); i++) {
            chuoiKhoa = chuoiKhoa.concat(listTapKhoaHienTai.get(i));
            if (i != listTapKhoaHienTai.size() - 1) {
                chuoiKhoa = chuoiKhoa.concat(", ");
            }
        }

        // Đừa vào list để xét dạng chuẩn
        for (String khoa : listTapKhoaHienTai) {
            listTapKhoa.clear();
            listTapKhoa.add(khoa);
        }

        text1.add("Kết quả: lược đồ Q có " + listTapKhoaHienTai.size() + " khóa là: " + chuoiKhoa);
        tapKhoa = tapKhoa.concat("Lược đồ trên có " + listTapKhoaHienTai.size() + " khóa là: " + chuoiKhoa);

        detail1.setText(text1);
        details.add(detail1);
        normalFormContent.setValue(details);
        normalFormContent.setResult("Kết luận: " + tapKhoa);

        return normalFormContent;
    }

    public Content timDangChuan(String nguonPTH) {
        Content normalFormContent = new Content();
        normalFormContent.setTitle(Constant.TIM_DANG_CHUAN);
        List<Detail> details = new ArrayList<>();

        boolean checkDC2 = true;
        boolean checkDC3 = true;
        boolean checkDCBC = true;

        if (listToiThieuPTH.isEmpty()) {
            Detail detail = new Detail();
            List<String> text = new ArrayList<>();
            detail.setStep("Xét dạng chuẩn: ");
            text.add("Vì lược đồ không có 0 phụ thuộc hàm nào");
            detail.setText(text);
            details.add(detail);
            normalFormContent.setValue(details);
            normalFormContent.setResult("Kết luận: Lược đồ đạt dạng chuẩn BCNF.");
            return normalFormContent;

        } else {
            Detail detail1 = new Detail();
            List<String> text1 = new ArrayList<>();
            detail1.setStep("Xét dạng chuẩn 1 ");
            text1.add("Vì mọi thuộc tính Q đều là thuộc tính đơn =>Đạt dạng chuẩn 1NF");
            detail1.setText(text1);
            details.add(detail1);
            if (checkKhoaDuyNhat) { // có 1 khóa
                listTapKhoa.clear();
                listTapKhoa.add(nguonPTH);
            }

            // Xét dạng chuẩn 2
            Detail detail2 = new Detail();
            List<String> text2 = new ArrayList<>();
            detail2.setStep("Xét dạng chuẩn 2:");
            for (String pth : listToiThieuPTH) {
                String veTrai = pth.split("→")[0];
                String vePhai = pth.split("→")[1];
                for (String khoa : listTapKhoa) {
                    if (khoa.length() > veTrai.length() && (khoa.contains(veTrai) && !khoa.contains(vePhai))) {
                        text2.add("+) Xét phụ thuộc hàm " + pth + " có:");
                        text2.add(vePhai + " là thuộc tính không khóa và " + veTrai + " là thuộc tính khóa của khóa " + khoa + " => Vi phạm dạng chuẩn 2");
                        checkDC2 = false; // Vi phạm
                    }
                }
            }

            if (!checkDC2) {
                detail2.setText(text2);
                details.add(detail2);
                normalFormContent.setValue(details);
                normalFormContent.setResult(Constant.RESULT_DANG_CHUAN_1);
                return normalFormContent;
            }

            text2.add("Vì mọi thuộc tính không khóa đều phụ thuộc đầy đủ vào khóa =>Đạt dạng chuẩn 2NF");
            detail2.setText(text2);
            details.add(detail2);

            // Xét dạng chuẩn 3
            Detail detail3 = new Detail();
            List<String> text3 = new ArrayList<>();
            detail3.setStep("Xét dạng chuẩn 3:");
            for (String pthtt : listToiThieuPTH) {
                String veTrai = pthtt.split("→")[0];
                String vePhai = pthtt.split("→")[1];
                if (!veTrai.contains(vePhai)) { // VP không thuộc Vt
                    for (String khoa : listTapKhoa) {
                        if (khoa.equals(veTrai) || khoa.contains(vePhai)) {
                            //checkDC3 = true;  Đúng
                        } else {
                            checkDC3 = false;
                            text3.add("+) Xét phụ thuộc hàm " + pthtt + ":");
                            text3.add("Thuộc tính vế trái " + veTrai + " không là một siêu khóa và thuộc tính vế phải " + vePhai + " không là 1 thuộc tính khóa  => Vi phạm dạng chuẩn 3");
                        }
                    }
                }
            }

            if (!checkDC3) {
                detail3.setText(text3);
                details.add(detail3);
                normalFormContent.setValue(details);
                normalFormContent.setResult(Constant.RESULT_DANG_CHUAN_2);
                return normalFormContent;
            }

            text3.add("Vì mọi thuộc tính không khóa đều không phụ thuộc bắc cầu vào một khóa nào =>Đạt dạng chuẩn 3NF");
            detail3.setText(text3);
            details.add(detail3);

            // Xét dạng chuẩn BC
            Detail detail4 = new Detail();
            List<String> text4 = new ArrayList<>();
            detail4.setStep("Xét dạng chuẩn BC::");

            for (String pthtt : listToiThieuPTH) {
                String veTrai = pthtt.split("→")[0];
                String vePhai = pthtt.split("→")[1];
                if (!veTrai.contains(vePhai)) { // VP không thuộc Vt
                    for (String khoa : listTapKhoa) {
                        if (khoa.equals(veTrai)) {
                           // checkDCBC = true; Đúng
                        } else {
                            checkDCBC = false;
                            text4.add("+) Xét phụ thuộc hàm " + pthtt + ":");
                            text4.add("Thuộc tính vế trái " + veTrai + " là phải là siêu khóa không khóa. => Vi phạm dạng chuẩn BC");
                        }
                    }
                }
            }

            if (!checkDCBC) {
                detail4.setText(text4);
                details.add(detail4);
                normalFormContent.setValue(details);
                normalFormContent.setResult(Constant.RESULT_DANG_CHUAN_3);
                return normalFormContent;
            }

            text4.add("Vì mọi phụ thuộc hàm đều có vế trái là siêu khóa =>Đạt dạng chuẩn BCNF");
            detail4.setText(text4);
            details.add(detail4);
            normalFormContent.setValue(details);
            normalFormContent.setResult(Constant.RESULT_DANG_CHUAN_BC);
        }

        return normalFormContent;
    }


    @Override
    public ResponseBase listExampleCSDL() {
        List<ExampleCSDL> listCSDL = new ArrayList<>();
        ExampleCSDL item1 = new ExampleCSDL("Lược đồ 1", "ABCDEGH", "{A→ BC, BE → G, E → D, D → G, A → B, AG → BC}");
        ExampleCSDL item2 = new ExampleCSDL("Lược đồ 2", "A, B, C, D, E, G", "AB→ C, AC→D, D→EG, G→B, A→D, CG→A");
        ExampleCSDL item3 = new ExampleCSDL("Lược đồ 3", "{A, B, C, D, E, G, H, I, J}", "{A → BDE, DE → G, H → J, J → HI, E → DG, BC→ GH, HG→J, E→G}");
        ExampleCSDL item4 = new ExampleCSDL("Lược đồ 4", "ABCDEHK", "{AB→C; CD→E; AH→K; A→D; B→D}");
        ExampleCSDL item5 = new ExampleCSDL("Lược đồ 5", "A, B, C, D, E, G", "{ AB →C; C →A; BC →D; ACD →B; D →EG; BE →C; CG →BD; CE →AG }");

        listCSDL.add(item1);
        listCSDL.add(item2);
        listCSDL.add(item3);
        listCSDL.add(item4);
        listCSDL.add(item5);

        return  ResponseExample.builder()
                .enumResultCode(EnumResultCode.SUCCESS)
                .data(listCSDL)
                .build();
    }

}
