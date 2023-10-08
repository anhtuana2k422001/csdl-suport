package vn.name.hoanhtuan.csdlsuport.model.csdl.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExampleCSDL {
    private String id;
    private String name;
    private String attributeSet;
    private String dependencyChain;
}
