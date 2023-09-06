package vn.name.hoanhtuan.csdlsuport.model.csdl.response;

import lombok.Data;

import java.util.List;

@Data
public class Content {
    private String title;
    private List<Detail> value;
    private String result;
}
