package vn.name.hoanhtuan.csdlsuport.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Handle {

    /**
     *  Hàm tìm bao đóng
     * @return replaceAll("[,\\s\\(\\)\\{\\}\\.\\;\\=]", "")
     */

    public static String baoDong(String lQHP) {
        String[] searchList = {",", " ", "(", ")", "{", "}", ".", ";", "="};
        String[] replacementList = {"", "", "", "", "", "", "", "", ""};
        return StringUtils.replaceEach(lQHP, searchList, replacementList);
    }

    /**   Hàm xử lý lại đề bài nhập vào */
    public static String tapPhuThuocHam(String pth){
        String[] searchList = {"", "", "-->", "->", "\\?", ",", "\\}", "\\{", "\\(", "\\)", "\\=", "\\."};
        String[] replacementList = {"→", "→", "→", "→", "→", ";", "", "", "", "", "", ""};
        return StringUtils.replaceEach(pth, searchList, replacementList);
    }

    /** Tách phụ thuộc vào danh sách */
    public static List<String> xuLyPhuThuocHam(String pth){
        List<String> listPTH = new ArrayList<>();
        String[] searchList = {"", "", "-->", "->", "?", ",", " ", "}", "{", "(", ")", ".", "="};
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
            return Collections.emptyList();
        }
        LOGGER.info("ListPTH Xu ly: {}", listPTH);
        return listPTH; // trả về list pth
    }

    /** Loại bỏ phủ thuộc hàm xác định nhau */
    public static List<String> boThuocTinhPTHThua(List<String> listPTH){
        List<String> listPTHDung = new ArrayList<>();
        for(String itemPTH : listPTH)
        {
            String veTr = itemPTH.split("→")[0];
            String vePh = itemPTH.split("→")[1];
            for (int i = 0; i < vePh.length(); i++) {
                String index = String.valueOf(vePh.charAt(i));
                if(veTr.contains(index)){
                    vePh = vePh.replace(index, "");
                }
            }

            if(!vePh.isEmpty()){
                String pthDung = veTr + "→" + vePh;
                listPTHDung.add(pthDung);
            }
        }
        return listPTHDung;
    }

    /** Lấy vế trái phụ thuộc hàm */
    public static String veTraiPTH(List<String> listPTH){
        String veTraiPTH = ""; // Tập thuộc tính vế trái
        for (String pth : listPTH) {
            String veTr = pth.split("→")[0];
            veTraiPTH = veTraiPTH.concat(veTr);
        }
        String uniqueVeTraiPTH = ""; // Tập thuộc tính vế trái unique
        // Tối ưu tập vế trái
        for (int j = 0; j < veTraiPTH.length(); j++) {
            if (!uniqueVeTraiPTH.contains(String.valueOf(veTraiPTH.charAt(j)))) {
                uniqueVeTraiPTH = uniqueVeTraiPTH.concat(String.valueOf(veTraiPTH.charAt(j)));
            }
        }
        return uniqueVeTraiPTH;
    }

    /** Lấy vế phải phụ thuộc hàm */
    public static String vePhaiPTH(List<String> listPTH){
        String vePhaiPTH = ""; // Tập thuộc tính vế trái
        for (String pth : listPTH) {
            String vePh = pth.split("→")[1];
            vePhaiPTH = vePhaiPTH.concat(vePh);
        }

        String uniqueVePhaiPTH = ""; // Tập thuộc tính vế trái unique
        // Tối ưu tập vế trái
        for (int j = 0; j < vePhaiPTH.length(); j++) {
            if (!uniqueVePhaiPTH.contains(String.valueOf(vePhaiPTH.charAt(j)))) {
                uniqueVePhaiPTH = uniqueVePhaiPTH.concat(String.valueOf(vePhaiPTH.charAt(j)));
            }
        }
        return uniqueVePhaiPTH;
    }

    /** Tìm N: Tập thuộc nguồn tính không xuất hiện vế phải PTH */
    public static String tapNguon(String baodong, String vephaiPTH){
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

    /** Tìm M là tập thuộc tính trung gian xuất hiện cả hai bên */
    public static String tapTrungGian(String vephaiPTH, String vetraiPTH){
        String taptrunggianPTH = "";
        for (int i = 0; i < vephaiPTH.length(); i++) {
            String indexPhai = String.valueOf(vephaiPTH.charAt(i));
            if (vetraiPTH.contains(indexPhai)) {
                taptrunggianPTH = taptrunggianPTH.concat(indexPhai);
            }
        }
        return taptrunggianPTH;
    }

    /** Hàm xử lý tìm bao đóng */
    public static String timBaoDong(String thuoctinh, List<String> listPth) {
        String baoDongCanTim = thuoctinh;
        int kytuBDBatDau = baoDongCanTim.length();
        for (int i = 0; i < listPth.size(); i++) {
            String veTr = listPth.get(i).split("→")[0];
            String vePh = listPth.get(i).split("→")[1];
            int soTTVeTrai = 0; //Số thuộc tính vế trái cần kiểm tra
            for (int k = 0; k < veTr.length(); k++) {
                for (int j = 0; j < baoDongCanTim.length(); j++) {
                    String indexNg = String.valueOf(baoDongCanTim.charAt(j));
                    if (String.valueOf(veTr.charAt(k)).contains(indexNg)) {
                        soTTVeTrai++;
                        if (soTTVeTrai == veTr.length()) {
                            for (int t = 0; t < vePh.length(); t++) {
                                String ttVP = String.valueOf(vePh.charAt(t));
                                if (!baoDongCanTim.contains(ttVP)) // Kiểm tra tồn tại hay chưa
                                {
                                    baoDongCanTim = baoDongCanTim.concat(ttVP);
                                }
                            }
                        }
                    }
                }
            }

            // Kiểm tra lại các tập phụ thuốc hàm nếu như bao đóng thay đổi
            if (i == listPth.size() - 1 && (baoDongCanTim.length() > kytuBDBatDau)) {
                    i = -1; // Lặp lại PTh , chạy lại = -1 vì i++ = 0
                    kytuBDBatDau = baoDongCanTim.length();

            }
        }
        return baoDongCanTim;
    }

    /** Loại bỏ nhưng ký tứ giống nhau */
    public static String uniqueKyTu(String kytu) {
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

    /** Loại bỏ nhưng ký tứ giống nhau */
    public static String uniqueKyTuSapXep(String kytu) {
        String kytuSapXepUnique = "";
        for (int j = 0; j < kytu.length(); j++) {
            if (!kytuSapXepUnique.contains(String.valueOf(kytu.charAt(j)))) {
                kytuSapXepUnique = kytuSapXepUnique.concat(String.valueOf(kytu.charAt(j)));
            }
        }
        return kytuSapXepUnique;
    }

    /** Hội những tập trung gian M liền nhau theo thứ tự cho vào list */
    public static List<String> listTapTrungGianLienNhau(String thuoctinhtrunggian) {
        List<String> listTapTrungGianLienNhau = new ArrayList<>();
        // Tìm tập khóa NguonPTH
        String tapCon = "";
        int index = -1;
        int sokytuXet = 0;
        for (int j = 0; j < thuoctinhtrunggian.length(); j++) {
            tapCon = tapCon.concat(String.valueOf(thuoctinhtrunggian.charAt(j)));
            tapCon = uniqueKyTu(tapCon); // xoá ký tự trùng lấp
            if (tapCon.length() > sokytuXet) {
                listTapTrungGianLienNhau.add(tapCon);
            }

            if (tapCon.length() > sokytuXet) {
                tapCon = tapCon.replace(String.valueOf(thuoctinhtrunggian.charAt(j)), "");
            }
            if (j == thuoctinhtrunggian.length() - 1) {
                if (sokytuXet == thuoctinhtrunggian.length() - 1) {
                    sokytuXet = 1; // Xét số ký tự từ 2 trở lên vì đã xét từ 1 trước đó
                    index++; // Chạy từ loại bỏ ký tự đầu đã xét
                    j = index;
                    tapCon = ""; // Chạy lại
                } else {
                    j = index; // quay lại xét ký tự đầu
                    sokytuXet++;
                }
            }
        }
        return listTapTrungGianLienNhau;
    }

    public static List<String> listTapTrungGianGhep(List<String> tapTrungGianLienNhau) {
        List<String> tapTrungGianGhep = new ArrayList<>();
        for (String item1 : tapTrungGianLienNhau) {
            for (String item2 : tapTrungGianLienNhau) {
                String item3 = "";
                item3 = item3.concat(item1);
                item3 = item3.concat(item2);
                item3 = sapXepKyTu(item3);
                tapTrungGianGhep.add(item3);
            }
        }
        return tapTrungGianGhep;
    }

    /** Hàm xử lý lấy ra tất cả các tập con của thuốc tính trung gian của M */
    public static List<String> listTapTrungGian(List<String> tapTrungGianGhep) {
        List<String> listTapHoiToiUu = new ArrayList<>();
        String chuoitapconchuatoituu= "";
        for (String item : tapTrungGianGhep) {
            chuoitapconchuatoituu = chuoitapconchuatoituu.concat("-"+item+"-");
        }

        // Tối ưu các tập con trùng nhau
        for (String item : tapTrungGianGhep) {
            item = item.concat("-");
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

    public static String sapXepKyTu(String thuoctinh) {
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
