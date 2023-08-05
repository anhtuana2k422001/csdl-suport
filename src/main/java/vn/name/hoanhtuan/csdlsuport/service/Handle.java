package vn.name.hoanhtuan.csdlsuport.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Handle {

    // Hàm tìm bao đóng
    public String BaoDong(String lqhp) {
        String[] searchList = {",", "\\s", "\\(", "\\)", "\\{", "\\}", "\\.", ";", "="};
        String[] replacementList = {"", "", "", "", "", "", "", "", ""};
        return StringUtils.replaceEach(lqhp, searchList, replacementList);
    }

    // Hàm xử lý lại đề bài nhập vào
    public String TapPhuThuocHam(String pth){
        String[] searchList = {"", "", "-->", "->", "\\?", ",", "\\}", "\\{", "\\(", "\\)", "\\=", "\\."};
        String[] replacementList = {"→", "→", "→", "→", "→", ";", "", "", "", "", "", ""};
        return StringUtils.replaceEach(pth, searchList, replacementList);
    }

    // Tách phụ thuộc vào danh sách
    public List<String> XuLyPhuThuocHam(String pth){
        List<String> listPTH = new ArrayList<>();

        String[] searchList = {"", "", "-->", "->", "\\?", ",", "\\s", "\\}", "\\{", "\\(", "\\)", "\\.", "\\="};
        String[] replacementList = {"→", "→", "→", "→", "→", ";", "", "", "", "", "", "", ""};
        // Thêm ký tự để tách các phụ thuộc hàm
        String pthClear = StringUtils.replaceEach(pth, searchList, replacementList).concat(";");

        try{
            while (pthClear.contains(";")) {
                String item = pthClear.split(";")[0];
                listPTH.add(item);
                pthClear = pthClear.substring(item.length() + ";".length());
            }
        }catch(Exception e){
            LOGGER.info("Phụ thuộc hàm không hợp lệ!\\nKiểm tra lại phụ thuộc hàm. " +
                    "Đặc biệt chú ý: Không được để thừa 2 ký tự sau ở đầu chuỗi và cuối chuỗi PTH ! " +
                    "+) dấu chấm phẩy: ; " +
                    "+) dấu phẩy: ");
            return null;
        }
        return listPTH; // trả về list pth
    }

    // Loại bỏ phủ thuộc hàm xác định nhau
    public List<String> BoThuocTinhPTHThua(List<String> listPTH){
        List<String> listPTHDung = new ArrayList<>();
        for(String itemPTH : listPTH)
        {
            String VT = itemPTH.split("→")[0];
            String VP = itemPTH.split("→")[1];
            for (int i = 0; i < VP.length(); i++) {
                String index = String.valueOf(VP.charAt(i));
                if(VT.contains(index)){
                    VP = VP.replace(index, "");
                }
            }

            if(VP.length()!=0){
                String pthDung = VT + "→" + VP;
                listPTHDung.add(pthDung);
            }
        }
        return listPTHDung;
    }

    /* Lấy vế trái phụ thuộc hàm */
    public String VeTraiPTH(List<String> listPTH){
        String VeTraiPTH = ""; // Tập thuộc tính vế trái
        for (String pth : listPTH) {
            String VT = pth.split("→")[0];
            VeTraiPTH = VeTraiPTH.concat(VT);
        }
        String uniqueVeTraiPTH = ""; // Tập thuộc tính vế trái unique
        // Tối ưu tập vế trái
        for (int j = 0; j < VeTraiPTH.length(); j++) {
            if (!uniqueVeTraiPTH.contains(String.valueOf(VeTraiPTH.charAt(j)))) {
                uniqueVeTraiPTH = uniqueVeTraiPTH.concat(String.valueOf(VeTraiPTH.charAt(j)));
            }
        }
        return uniqueVeTraiPTH;
    }

    /* Lấy vế phải phụ thuộc hàm */
    public String VePhaiPTH(List<String> listPTH){
        String VePhaiPTH = ""; // Tập thuộc tính vế trái
        for (String pth : listPTH) {
            String VP = pth.split("→")[1];
            VePhaiPTH = VePhaiPTH.concat(VP);
        }

        String uniqueVePhaiPTH = ""; // Tập thuộc tính vế trái unique
        // Tối ưu tập vế trái
        for (int j = 0; j < VePhaiPTH.length(); j++) {
            if (!uniqueVePhaiPTH.contains(String.valueOf(VePhaiPTH.charAt(j)))) {
                uniqueVePhaiPTH = uniqueVePhaiPTH.concat(String.valueOf(VePhaiPTH.charAt(j)));
            }
        }
        return uniqueVePhaiPTH;
    }

    // Tìm N: Tập thuộc nguồn tính không xuất hiện vế phải PTH
    public String TapNguon(String baodong, String vephaiPTH){
        String tapnguonPTH;
        tapnguonPTH = baodong;
        for (int i = 0; i < baodong.length(); i++) {
            for (int j = 0; j < vephaiPTH.length(); j++) {
                if (String.valueOf(baodong.charAt(i)).equals(String.valueOf(vephaiPTH.charAt(j)))) {
                    tapnguonPTH = tapnguonPTH.replace(String.valueOf(baodong.charAt(i)), "");
                }
            }
        }
        return tapnguonPTH;
    }

    // Tìm M là tập thuộc tính trung gian xuất hiện cả hai bên
    public String TapTrungGian(String vephaiPTH, String vetraiPTH){
        String taptrunggianPTH = "";
        for (int i = 0; i < vephaiPTH.length(); i++) {
            String indexPhai = String.valueOf(vephaiPTH.charAt(i));
            if (vetraiPTH.contains(indexPhai)) {
                taptrunggianPTH = taptrunggianPTH.concat(indexPhai);
            }
        }
        return taptrunggianPTH;
    }

    // Hàm xử lý tìm bao đóng
    public String TimBaoDong(String thuoctinh, List<String> listPth) {
        String BaoDongCanTim = thuoctinh;
        int kytuBDBatDau = BaoDongCanTim.length();
        for (int i = 0; i < listPth.size(); i++) {
            String VT = listPth.get(i).split("→")[0];
            String VP = listPth.get(i).split("→")[1];
            int SottVeTrai = 0; //Số thuộc tính vế trái cần kiểm tra
            for (int k = 0; k < VT.length(); k++) {
                for (int j = 0; j < BaoDongCanTim.length(); j++) {
                    String indexNg = String.valueOf(BaoDongCanTim.charAt(j));
                    if (String.valueOf(VT.charAt(k)).contains(indexNg)) {
                        SottVeTrai++;
                        if (SottVeTrai == VT.length()) {
                            for (int t = 0; t < VP.length(); t++) {
                                String ttVP = String.valueOf(VP.charAt(t));
                                if (!BaoDongCanTim.contains(ttVP)) // Kiểm tra tồn tại hay chưa
                                {
                                    BaoDongCanTim = BaoDongCanTim.concat(ttVP);
                                }
                            }
                        }
                    }
                }
            }

            // Kiểm tra lại các tập phụ thuốc hàm nếu như bao đóng thay đổi
            if (i == listPth.size() - 1) {
                if (BaoDongCanTim.length() > kytuBDBatDau) {
                    i = -1; // Lặp lại PTh , chạy lại = -1 vì i++ = 0
                    kytuBDBatDau = BaoDongCanTim.length();
                }
            }
        }
        return BaoDongCanTim;
    }

    // Loại bỏ nhưng ký tứ giống nhau
    public String uniqueKyTu(String kytu) {
        for (int i = 0; i < kytu.length(); i++) {
            for (int j = 0; j < kytu.length(); j++) {
                String indexKyTuI = String.valueOf(kytu.charAt(i));
                String indexKyTuJ = String.valueOf(kytu.charAt(j));
                if (i != j && indexKyTuI.equals(indexKyTuJ)) {
                    kytu = kytu.replaceFirst(indexKyTuI, "");
                }
            }
        }
        return kytu;
    }

    // Loại bỏ nhưng ký tứ giống nhau
    public String uniqueKyTuSapXep(String kytu) {
        String KytuSapXepUnique = "";
        for (int j = 0; j < kytu.length(); j++) {
            if (!KytuSapXepUnique.contains(String.valueOf(kytu.charAt(j)))) {
                KytuSapXepUnique = KytuSapXepUnique.concat(String.valueOf(kytu.charAt(j)));
            }
        }
        return KytuSapXepUnique;
    }

    // Hội những tập trung gian M liền nhau theo thứ tự cho vào list
    public List<String> listTapTrungGianLienNhau(String thuoctinhtrunggian) {
        List<String> listTapTrungGianLienNhau = new ArrayList<>();
        // Tìm tập khóa NguonPTH
        String TapCon = "";
        int index = -1;
        // item = item.concat(NguonPTH);
        int sokytuXet = 0;
        for (int j = 0; j < thuoctinhtrunggian.length(); j++) {
            TapCon = TapCon.concat(String.valueOf(thuoctinhtrunggian.charAt(j)));
            TapCon = uniqueKyTu(TapCon); // xoá ký tự trùng lấp
            if (TapCon.length() > sokytuXet) {
                listTapTrungGianLienNhau.add(TapCon);
                //System.out.print("\nXét: "+TapCon);
            }

            if (TapCon.length() > sokytuXet) {
                TapCon = TapCon.replace(String.valueOf(thuoctinhtrunggian.charAt(j)), "");
            }
            if (j == thuoctinhtrunggian.length() - 1) {
                if (sokytuXet == thuoctinhtrunggian.length() - 1) {
                    sokytuXet = 1; // Xét số ký tự từ 2 trở lên vì đã xét từ 1 trước đó
                    index++; // Chạy từ loại bỏ ký tự đầu đã xét
                    j = index;
                    TapCon = ""; // Chạy lại
                } else {
                    j = index; // quay lại xét ký tự đầu
                    sokytuXet++;
                }
            }
        }
        return listTapTrungGianLienNhau;
    }

    public List<String> listTapTrungGianGhep(List<String> TapTrungGianLienNhau) {
        List<String> TapTrungGianGhep = new ArrayList<>();
        for (String item1 : TapTrungGianLienNhau) {
            for (String item2 : TapTrungGianLienNhau) {
                String item3 = "";
                item3 = item3.concat(item1);
                item3 = item3.concat(item2);
                item3 = SapXepKyTu(item3);
                TapTrungGianGhep.add(item3);
            }
        }
        return TapTrungGianGhep;
    }

    // Hàm xử lý lấy ra tất cả các tập con của thuốc tính trung gian của M
    public List<String> listTapTrungGian(List<String> TapTrungGianGhep) {
        List<String> listTapHoiToiUu = new ArrayList<>();
        String chuoitapconchuatoituu= "";
        //System.out.println("--- chiều dài 2: " + listTapTrungGian.size());
        for (String item : TapTrungGianGhep) {
            chuoitapconchuatoituu = chuoitapconchuatoituu.concat("-"+item+"-");
        }

        // Tối ưu các tập con trùng nhau
        for (String item : TapTrungGianGhep) {
            item = item.concat("-");
            //System.out.println("--- Xet chua hoi: " + item);
            if(chuoitapconchuatoituu.contains("-"+item)){
                chuoitapconchuatoituu = chuoitapconchuatoituu.replaceAll("-"+item, "");
                item = item.replace("-", "");
                listTapHoiToiUu.add(item);
            }
        }

        // Sắp xếp tập conn trung gian theo độ dài
        for (int i = 0; i< listTapHoiToiUu.size() -1 ; i++) {
            for (int j= i + 1; j< listTapHoiToiUu.size(); j++) {
                if(listTapHoiToiUu.get(i).length()>listTapHoiToiUu.get(j).length()){
                    String temp = listTapHoiToiUu.get(i);
                    listTapHoiToiUu.set(i, listTapHoiToiUu.get(j));
                    listTapHoiToiUu.set(j, temp);
                }
            }
        }
        return listTapHoiToiUu;
    }

    public String SapXepKyTu(String thuoctinh) {
        String chuoidasapxep = "";
        if (thuoctinh.isEmpty()) {
            return chuoidasapxep;
        }
        String max = String.valueOf(thuoctinh.charAt(0));
        for (int i = 0; i < thuoctinh.length(); i++) {
            String item = String.valueOf(thuoctinh.charAt(i));
            if (max.compareTo(item) > 0) {
                max = item;
            }
            if (i == thuoctinh.length() - 1) {
                chuoidasapxep = chuoidasapxep.concat(max);
                thuoctinh = thuoctinh.replace(max, ""); //xóa đi một thuộc tính max đã nối vào chuỗi sắp xếp
                if (thuoctinh.isEmpty()) {
                    return chuoidasapxep;
                }
                if (thuoctinh.length() == 1) { // nếu còn 1 thuốc tính thì lấy luôn vào chuõi
                    chuoidasapxep = chuoidasapxep.concat(thuoctinh);
                } else {
                    i = 0; // Chạy lại vòng lặp tìm max để nối vào chuỗi xắp xếp
                    max = String.valueOf(thuoctinh.charAt(i));
                }
            }
        }
        return chuoidasapxep;
    }

}
