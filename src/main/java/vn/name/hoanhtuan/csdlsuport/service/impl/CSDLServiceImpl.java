package vn.name.hoanhtuan.csdlsuport.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import vn.name.hoanhtuan.csdlsuport.common.Constant;
import vn.name.hoanhtuan.csdlsuport.common.EnumResultCode;
import vn.name.hoanhtuan.csdlsuport.model.ResponseBase;
import vn.name.hoanhtuan.csdlsuport.model.csdl.request.RequestBaoDong;
import vn.name.hoanhtuan.csdlsuport.model.csdl.request.RequestCSDLSupport;
import vn.name.hoanhtuan.csdlsuport.model.csdl.response.DataCSDL;
import vn.name.hoanhtuan.csdlsuport.model.csdl.response.ResponseBaoDong;
import vn.name.hoanhtuan.csdlsuport.model.csdl.response.ResponseCSDLSupport;
import vn.name.hoanhtuan.csdlsuport.service.CSDLService;
import vn.name.hoanhtuan.csdlsuport.service.Handle;
import vn.name.hoanhtuan.csdlsuport.validate.ValidationCSDL;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CSDLServiceImpl implements CSDLService {
    private static List<String> listToiThieuPTH = new ArrayList<>();// List khóa để xét DC
    public static List<String> listTapKhoa = new ArrayList<>();// List khóa để xét DC
    public static boolean checkKhoaDuyNhat = false;

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

        return ResponseBaoDong.builder()
                .enumResultCode(EnumResultCode.SUCCESS)
                .data(dataResponse)
                .build();
    }

    @Override
    public ResponseBase phanTichCSDL(RequestCSDLSupport request) {
        DataCSDL dataResponse = null;
        List<String> listPTH; // danh sách phụ thuộc hàm
        String tapPTH = request.getDependencyChain();
        String baoDong = Handle.BaoDong(request.getAttributeSet());

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

        // Tìm khóa
        String tapvetrai = Handle.VeTraiPTH(listPTH);
        // Todo:  txtTapVeTrai.setText(tapvetrai);

        String tapvephai = Handle.VePhaiPTH(listPTH);
        // Todo: txtTapVePhai.setText(tapvephai);

        String NguonPTH =  Handle.TapNguon(baoDong, tapvephai);
        // Todo: txtTapNguonPTH.setText(NguonPTH);

        String TrungGianPTH =  Handle.TapTrungGian(tapvephai, tapvetrai);
        // Todo:  txtTapTrungGianPTH.setText(TrungGianPTH);

        String kqKhoaDuyNhat = TimKhoaDuyNhat(NguonPTH, listPTH, baoDong, tapPTH);
        if(StringUtils.isEmpty(kqKhoaDuyNhat))
        {
            checkKhoaDuyNhat = false;
        }

        String kqTapKhoa = TimTapKhoa(NguonPTH,baoDong, listPTH, TrungGianPTH);
        String ketquaKhoa = kqKhoaDuyNhat + kqTapKhoa;

        // Tìm phu toi thieu

        String ketquaFtt = PhuToiThieu(listPTH);

        // Tìm dạng chuẩn
        String ketquadangchuan = TimDangChuan(NguonPTH);


        if(request.getDetailExplanation().equalsIgnoreCase(Constant.DETAIL_EXPLANATION)){
            dataResponse = DataCSDL.builder()
                    .primaryKey(ketquaKhoa)
                    .minimalCove(ketquaFtt)
                    .normalForm(ketquadangchuan)
                    .build();
        }

        return ResponseCSDLSupport.builder()
                .enumResultCode(EnumResultCode.SUCCESS)
                .data(dataResponse)
                .build();
    }

    // Hàm tìm phụ thuộc hàm tối thiểu
    public String PhuToiThieu(List<String> listPTH) {
        // TODO: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n\n+) Dạng 2: Tìm PTH tối thiểu ??? ");
        //System.out.print("\n***** Dạng 2: Tìm PTH tối thiểu ??? *****");

        // Trường hợp không có phụ thuộc hàm
        if (listPTH.isEmpty()) {
           // TODO:  NoiDungLoiGiai = NoiDungLoiGiai.concat("\nVì không có phụ thuộc hàm");
            // TODO: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n=> phụ thuộc hàm tối thiểu là: Ftt = {Ø}");
            //System.out.println("Vì không có phụ thuộc hàm");
            return "Phụ thuộc hàm tối thiểu là: Ftt = {Ø}";
        }

        // trường hợp chỉ có 1 PTh mà không thể phân rã
        if (listPTH.size() == 1) {
            String VP = listPTH.get(0).split("→")[1];
            if (VP.length() == 1) {
               // TODO:  NoiDungLoiGiai = NoiDungLoiGiai.concat("\nVì chỉ có 1 phụ thuộc hàm");
                // TODO: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n=> Phụ thuộc hàm tối thiểu là: Ftt = { " + listPTH.get(0) + " }");
                listToiThieuPTH = listPTH; // gán vào list tối thiểu public
                return "Phụ thuộc hàm tối thiểu là: Ftt = { " + listPTH.get(0) + " }";
            }
        }
        //=== Bước 1:  Phân rã phụ thuộc hàm ở vế phải
        List<String> listPTHPhanRa = new ArrayList(); // phân rã PTH ở vế phải
        for (String pth : listPTH) {
            String VT = pth.split("→")[0];
            String VP = pth.split("→")[1];
            String itemF = "";
            itemF = itemF.concat(VT).concat("→");
            for (int i = 0; i < VP.length(); i++) {
                String indexVP = String.valueOf(VP.charAt(i));
                itemF = itemF.concat(indexVP);
                listPTHPhanRa.add(itemF);
                itemF = itemF.replace(indexVP, "");
            }
        }
        String FToiThieu = "";
        for (int i = 0; i < listPTHPhanRa.size(); i++) {
            FToiThieu = FToiThieu.concat(listPTHPhanRa.get(i));
            if (i != listPTHPhanRa.size() - 1) {
                FToiThieu = FToiThieu.concat(", ");
            }
        }
        // TODO: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n--Bước 1: Phân rã \n\tF1 = {" + FToiThieu + "}");

        // TODO: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n--Bước 2: Loại bỏ vế trái dư thừa:");
        //=== Bước 2:  Lược bỏ vế trái PTH
        String checkPTHUnique = "";
        List<String> listPTHBoVT = new ArrayList();
        List<String> listPTHBoVTUnique = new ArrayList();
        for (String pth : listPTHPhanRa) {
            String VT = pth.split("→")[0];
            String VP = pth.split("→")[1];
            String itemF = pth; // loại bỏ PTH vế trái
            if (VT.length() > 1) {
                // TODO: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n - Xét: " + pth + ":");
                for (int i = 0; i < VT.length(); i++) {
                    String indexVT = String.valueOf(VT.charAt(i));
                    String conlaiVT = VT;
                    conlaiVT = conlaiVT.replace(indexVT, "");
                    String baodong = Handle.TimBaoDong(conlaiVT, listPTHPhanRa);
                    if (baodong.contains(VP)) {
                        // TODO: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n\tNếu bỏ " + indexVT + ": {" + conlaiVT + "}+ = " + baodong + " có chứa " + VP + " => " + indexVT + " dư thừa");
                        itemF = "";
                        itemF = itemF.concat(conlaiVT).concat("→").concat(VP);
                    } else {
                        // TODO: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n\tNếu bỏ " + indexVT + ": {" + conlaiVT + "}+ = " + baodong + " không chứa " + VP + " => " + indexVT + " dư thừa");
                    }
                }
            }

            if (!checkPTHUnique.contains(itemF)) {
                checkPTHUnique = checkPTHUnique.concat(itemF + ",");
                listPTHBoVTUnique.add(itemF);
            }
            listPTHBoVT.add(itemF); // chưa tối ưu
        }

        FToiThieu = ""; // Gán lại bằng rỗng
        for (int i = 0; i < listPTHBoVTUnique.size(); i++) {
            FToiThieu = FToiThieu.concat(listPTHBoVTUnique.get(i));
            if (i != listPTHBoVTUnique.size() - 1) {
                FToiThieu = FToiThieu.concat(", ");
            }
        }

        if (listPTHBoVT.size() > listPTHBoVTUnique.size()) {
            String FpthBoVT = ""; // Gán lại bằng rỗng
            for (int i = 0; i < listPTHBoVT.size(); i++) {
                FpthBoVT = FpthBoVT.concat(listPTHBoVT.get(i));
                if (i != listPTHBoVT.size() - 1) {
                    FpthBoVT = FpthBoVT.concat(", ");
                }
            }
            if (FToiThieu.length() != FpthBoVT.length()) {
                // TODO: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n=>Sau khi loại bỏ dưa thừa VT là: F1 = {" + FpthBoVT + "}");
            }
        }

        // Xuất kết quả bước 2
        // NoiDungLoiGiai = NoiDungLoiGiai.concat("\nVậy F2 = {" + FToiThieu + "}");

        //=== Bước 3: Lược bỏ PTH dư thừa
        // TODO:  NoiDungLoiGiai = NoiDungLoiGiai.concat("\n--Bước 3: Loại bỏ PTH dư thừa:");

        // Trường hợp chỉ có 1 PTH
        if (listPTHBoVTUnique.size() == 1) {
            // TODO: NoiDungLoiGiai = NoiDungLoiGiai.concat("\nChỉ còn lại có 1 phụ thuộc hàm");
            //TODO:  NoiDungLoiGiai = NoiDungLoiGiai.concat("\n=> Phụ thuộc hàm tối thiểu là: Ftt = { " + FToiThieu + " }");
            listToiThieuPTH = listPTHBoVTUnique;
            return "=> Phụ thuộc hàm tối thiểu là: Ftt = { " + FToiThieu + " }";
        }

        List<String> listPTHBoPTH = new ArrayList();
        // Sao chép list
        for (String item : listPTHBoVTUnique) {
            listPTHBoPTH.add(item);
        }

        List<String> listPTHToiThieu = new ArrayList();
        // Xử lý loại bỏ
        int vitri = 0;
        String itemFtt = "";
        String itemCuoi = listPTHBoVTUnique.get(listPTHBoVTUnique.size() - 1); // Thay thế cho vị trí xóa
        for (String pth : listPTHBoVTUnique) {
            String VT = pth.split("→")[0];
            String VP = pth.split("→")[1];
            String itemF = pth;
            listPTHBoPTH.remove(vitri);
            if (pth.equals(itemCuoi)) { // Nếu xét PTH cuối
                listPTHBoPTH.remove(0); // Thay đổi PTH đầu tiên bằng PTh không dư thừa đã xác định
                listPTHBoPTH.add(0, itemFtt);
            }
            String baodong = Handle.TimBaoDong(VT, listPTHBoPTH);
            if (baodong.contains(VP)) {
                if (itemFtt.isEmpty()) {
                    listPTHBoPTH.add(vitri, itemCuoi);
                } else {
                    listPTHBoPTH.add(vitri, itemFtt);
                }
                //TODO: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n  Nếu xóa " + pth + " khỏi F2 thì:\t{" + VT + "}+ = " + baodong + " có chứa " + VP + "   =>" + pth + " dư thừa");
            } else {
                listPTHBoPTH.add(vitri, itemF);
                //TODO:  NoiDungLoiGiai = NoiDungLoiGiai.concat("\n  Nếu xóa " + pth + " khỏi F2 thì:\t{" + VT + "}+ = " + baodong + " không chứa " + VP + "   =>" + pth + " không dư thừa");
                listPTHToiThieu.add(itemF);
                if (itemFtt.isEmpty()) {
                    itemFtt = itemF; // Chưa có thì gán 1 lần đầu để thay thế
                }
            }
            vitri++;
        }

        FToiThieu = ""; // Gán lại bằng rỗng
        for (int i = 0; i < listPTHToiThieu.size(); i++) {
            FToiThieu = FToiThieu.concat(listPTHToiThieu.get(i));
            if (i != listPTHToiThieu.size() - 1) {
                FToiThieu = FToiThieu.concat(", ");
            }
        }
        // TODO: NoiDungLoiGiai = NoiDungLoiGiai.concat("\nKết luận F tối thiểu là:  Ftt = { " + FToiThieu + " }");

        listToiThieuPTH = listPTHToiThieu;// gán để dụng kế thừa tìm dạng chuẩn
        return "Phụ thuộc hàm tối thiểu là: Ftt = { " + FToiThieu + " }";
    }

    private String TimKhoaDuyNhat(String NguonPTH, List<String> listPTH, String BaoDong, String phuThuocHam) {
        String BaoDongNguon = Handle.TimBaoDong(NguonPTH, listPTH); //Gọi
        // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("Tập thuộc tính: Q = (" + BaoDong + ")");
        String tapPTH = Handle.TapPhuThuocHam(phuThuocHam);
        if (tapPTH.isEmpty()) {
            tapPTH = "Ø";
        }
        // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\nPhụ Thuộc Hàm: F = {" + tapPTH + "}");
        // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n\n\t\t\t\tLời Giải\n");
        // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n+) Dạng 1: Tìm Khóa ??? \n");
        String thuoctinh = "";
        if (listPTH.isEmpty()) {
            // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("Vì không có phụ thuộc hàm nên " + BaoDong + " là siêu khóa");
            checkKhoaDuyNhat = true;
            return "Lược đồ có siêu khóa: " + BaoDong;
        } else {
            if (NguonPTH.length() != 0) {
                for (int i = 0; i < NguonPTH.length(); i++) {
                    thuoctinh = thuoctinh.concat(String.valueOf(NguonPTH.charAt(i)));
                    if (i != NguonPTH.length() - 1) {
                        thuoctinh = thuoctinh.concat(", ");
                    }
                }
                String title = thuoctinh.concat(" là thuộc tính không xuất hiến bên vế phải");
                // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat(title);
                // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n=> N = {" + thuoctinh + "}");
                if (BaoDong.length() == BaoDongNguon.length()) {
                    // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n{" + NguonPTH + "}+ = " + BaoDongNguon + " = Q+");
                    // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\nVậy Q có khóa duy nhất là: K = N = {" + thuoctinh.replace(", ", "") + "}");
                    checkKhoaDuyNhat = true;
                } else {
                    // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n{" + NguonPTH + "}+ = " + BaoDongNguon + " ≠ Q+");
                    checkKhoaDuyNhat = false;
                    return StringUtils.EMPTY;
                }
            } else {
                thuoctinh = "Không có thuộc tính nào mà không xuất hiện bên vế phải";
                // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat(thuoctinh);
                // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n=> N = {Ø}");
                checkKhoaDuyNhat = false;
                return StringUtils.EMPTY;
            }
        }

        // Trường hợp có 1 PTH
        return "Lược đồ có 1 khóa duy nhất: " + thuoctinh.replace(", ", "");
    }
    // Hàm tìm tập khóa hội Tập nguồn với Tập trung gian
    private String TimTapKhoa(String NguonPTH, String BaoDong, List<String> listPTH, String TrungGianPTH) {
        String TapKhoa = "";
        String thuoctinhM = "";
        if (checkKhoaDuyNhat == false) {
            for (int i = 0; i < TrungGianPTH.length(); i++) {
                thuoctinhM = thuoctinhM.concat(String.valueOf(TrungGianPTH.charAt(i)));
                if (i != TrungGianPTH.length() - 1) {
                    thuoctinhM = thuoctinhM.concat(", ");
                }
            }
            // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\nTa có: M =  {" + thuoctinhM + "}" + " là thuộc tính xuất hiến cả hai vế \n");
            // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("Bao đóng tập hội của Mi với N là: ");
            String TapConM = thuoctinhM.replace(", ", "");
            TapConM =  Handle.SapXepKyTu(TapConM);
            // Hội lại để tìm tất cả tập con của M
            List<String> listTapKhoaHienTai = new ArrayList<>(); // Khai báo

            List<String> listThuocTinhGhep = Handle.listTapTrungGianLienNhau(TapConM);
            List<String> listTapHoi = Handle.listTapTrungGianGhep(listThuocTinhGhep);
            List<String> listTapTrungGian = Handle.listTapTrungGian(listTapHoi);
            for (int i = 0; i < listTapTrungGian.size(); i++) {
                String temp = "";
                temp = temp.concat(NguonPTH).concat(listTapTrungGian.get(i));

                // Kiểm tra Mi hội N có chứa khóa trước đó hay không
                boolean checkSieuKhoa = false;
                for (String khoa : listTapKhoaHienTai) {
                    int ChieuDaiKhoa = 0;
                    for (int j = 0; j < khoa.length(); j++) {
                        String itemKhoa = String.valueOf(khoa.charAt(j));
                        if (temp.contains(itemKhoa)) {
                            ChieuDaiKhoa++;
                        }
                    }
                    if (ChieuDaiKhoa == khoa.length()) {
                        //Tức là các Mi hội N này duyệt thấy đã chứa đủ số ký tự khóa
                        checkSieuKhoa = true;
                    }
                }

                if (checkSieuKhoa == false) {
                    // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n- Xét " + temp + ": {" + temp + "}+ = ");
                    String baodongMi = Handle.TimBaoDong(temp, listPTH);
                    if (baodongMi.length() == BaoDong.length()) {
                        // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat(baodongMi + " = Q+ => là khóa");
                        listTapKhoaHienTai.add(temp);
                    } else {
                        // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat(baodongMi + " ≠ Q+");
                    }
                }

                if (i == listTapTrungGian.size() - 1) {
                    // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n- Các thuộc tính Mi hội N còn lại chứa khóa ở trên nên là siêu khóa sẽ không cần xét.");
                }
            }

            // Xuất ra các khoa của lược đồ Q
            String ChuoiKhoa = "";
            for (int i = 0; i < listTapKhoaHienTai.size(); i++) {
                ChuoiKhoa = ChuoiKhoa.concat(listTapKhoaHienTai.get(i));
                if (i != listTapKhoaHienTai.size() - 1) {
                    ChuoiKhoa = ChuoiKhoa.concat(", ");
                }
            }

            // Đừa vào list để xét dạng chuẩn
            for (String khoa : listTapKhoaHienTai) {
                listTapKhoa.clear();
                listTapKhoa.add(khoa);
            }
            // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n=> Vậy lược đồ Q có " + listTapKhoaHienTai.size() + " khóa là: " + ChuoiKhoa);
            TapKhoa = TapKhoa.concat("Lược đồ trên có " + listTapKhoaHienTai.size() + " khóa là: " + ChuoiKhoa);
        }
        return TapKhoa;
    }

    public String TimDangChuan(String NguonPTH) {
        String dangchuancaonhat;
        // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n\n+) Dạng 3: Dạng chuẩn ??? ");

        boolean checkDC2 = true;
        boolean checkDC3 = true;
        boolean checkDCBC = true;

        if (listToiThieuPTH.isEmpty()) {
            // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\nVì lược đồ không có 0 phụ thuộc hàm nào");
            // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n=> Kết luận: Lược đồ đạt dạng chuẩn BCNF.");
            dangchuancaonhat = "Lược đồ đạt dạng chuẩn BCNF";
            return dangchuancaonhat;
        } else {
            // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n-Xét dạng chuẩn 1: \n\tVì mọi thuộc tính Q đều là thuộc tính đơn =>Đạt dạng chuẩn 1NF");
            if (checkKhoaDuyNhat == true) { // có 1 khóa
                listTapKhoa.clear();
                listTapKhoa.add(NguonPTH);
            }

            // Xét dạng chuẩn 2
            // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n-Xét dạng chuẩn 2: ");
            for (String pth : listToiThieuPTH) {
                String VT = pth.split("→")[0];
                String VP = pth.split("→")[1];
                for (String khoa : listTapKhoa) {
                    if (khoa.length() > VT.length()) {
                        if (khoa.contains(VT) && !khoa.contains(VP)) {
                            // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n\t+) Xét phụ thuộc hàm " + pth + " có: \n\t   " + VP + " là thuộc tính không khóa và " + VT + " là thuộc tính khóa của khóa " + khoa + " => Vi phạm dạng chuẩn 2");
                            checkDC2 = false; // Vi phạm
                        }
                    }
                }
            }

            if (!checkDC2) {
                // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n=> Vậy lược đồ quan hệ chỉ đạt dạng chuẩn 1NF");
                dangchuancaonhat = "Lược đồ đạt dạng chuẩn 1NF";
                return dangchuancaonhat;
            }
            // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n\tVì mọi thuộc tính không khóa đều phụ thuộc đầy đủ vào khóa =>Đạt dạng chuẩn 2NF");

            // Xét dạng chuẩn 3
            // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n-Xét dạng chuẩn 3: ");
            for (String pthtt : listToiThieuPTH) {
                String VT = pthtt.split("→")[0];
                String VP = pthtt.split("→")[1];
                if (!VT.contains(VP)) { // VP không thuộc Vt
                    for (String khoa : listTapKhoa) {
                        if (khoa.equals(VT) || khoa.contains(VP)) {
                            //checkDC3 = true; // đúng
                        } else {
                            checkDC3 = false;
                            // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n\t Xét " + pthtt + ": thuộc tính vế trái " + VT + " không là một siêu khóa và thuộc tính vế phải " + VP + " không là 1 thuộc tính khóa \n\t => Vi phạm dạng chuẩn 3\n");
                        }
                    }
                }
            }

            if (!checkDC3) {
                // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n=> Vậy Lược đồ quan hệ chỉ đạt dạng chuẩn 2");
                dangchuancaonhat = "Lược đồ đạt dạng chuẩn 2NF";
                return dangchuancaonhat;
            }
            // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n\tVì mọi thuộc tính không khóa đều không phụ thuộc bắc cầu vào một khóa nào =>Đạt dạng chuẩn 3NF");

            // Xét dạng chuẩn BC
            // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n-Xét dạng chuẩn BC: ");
            for (String pthtt : listToiThieuPTH) {
                String VT = pthtt.split("→")[0];
                String VP = pthtt.split("→")[1];
                if (!VT.contains(VP)) { // VP không thuộc Vt
                    for (String khoa : listTapKhoa) {
                        if (khoa.equals(VT)) {
                            //checkDCBC = true;
                        } else {
                            checkDCBC = false;
                            // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n\t+) Xét phụ thuộc hàm " + pthtt + ": \n\tthuộc tính vế trái " + VT + " là phải là siêu khóa không khóa. => Vi phạm dạng chuẩn BC");
                        }
                    }
                }
            }

            if (!checkDCBC) {
                // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n=> Vậy Lược đồ quan hệ chỉ đạt dạng chuẩn 3NF");
                dangchuancaonhat = "Lược đồ đạt dạng chuẩn BCNF";
                return dangchuancaonhat;
            }

            // Todo: NoiDungLoiGiai = NoiDungLoiGiai.concat("\n\tVì mọi phụ thuộc hàm đều có vế trái là siêu khóa =>Đạt dạng chuẩn BCNF");
            dangchuancaonhat = "Lược đồ đạt dạng chuẩn BCNF";
        }

        return dangchuancaonhat;
    }

}
