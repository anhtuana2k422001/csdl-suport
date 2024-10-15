package vn.name.hoanhtuan.csdlsuport;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import vn.name.hoanhtuan.csdlsuport.service.EmailService;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@SpringBootApplication
@Slf4j
public class CsdlSuportApplication {

	private final EmailService emailService;

	public CsdlSuportApplication(EmailService emailService) {
		this.emailService = emailService;
	}

	public static void main(String[] args) {
//		SpringApplication.run(CsdlSuportApplication.class, args);
		ConfigurableApplicationContext context =  SpringApplication.run(CsdlSuportApplication.class, args);
		String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
		LOGGER.info("Application started at " + timeStamp);
		CsdlSuportApplication app = context.getBean(CsdlSuportApplication.class);
		app.run();
		LOGGER.info("Send email success {}", timeStamp);
		LOGGER.info("****************** Run app success ******************");
	}

	private void run(){
		LOGGER.info("Start send email");
//		emailService.sendEmail("hoanhtuan.it.dev@gmail.com", "Hello send email", "Hell there");
	}

}
