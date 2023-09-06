package vn.name.hoanhtuan.csdlsuport.model.csdl.response;

import lombok.Data;

import java.util.List;

@Data
public class Detail {
    private String step;
    private List<String> text;
}
