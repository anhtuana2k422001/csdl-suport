package vn.name.hoanhtuan.csdlsuport.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Thay đổi đường dẫn thành "/**" để áp dụng cho tất cả các đường dẫn.
                .allowedOrigins("*") // Cho phép tất cả các nguồn gốc truy cập.
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Cho phép tất cả các phương thức HTTP.
                .allowedHeaders("*"); // Cho phép tất cả các tiêu đề.
    }
}
