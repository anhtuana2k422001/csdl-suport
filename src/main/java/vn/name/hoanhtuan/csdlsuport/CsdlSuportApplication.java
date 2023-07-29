package vn.name.hoanhtuan.csdlsuport;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class CsdlSuportApplication {

	public static void main(String[] args) {
		SpringApplication.run(CsdlSuportApplication.class, args);
		LOGGER.info("****************** Run app success ******************");
	}

}
