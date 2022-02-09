package com.vmware.accessmanagement;

import com.vmware.accessmanagement.util.AppContext;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Log4j2
public class AccessManagementApplication{

	public static void main(String... args) {
		ApplicationContext applicationContext = SpringApplication.run(AccessManagementApplication.class, args);
		// Setting the application context for further references
		AppContext.getInstance().setContext(applicationContext);
	}
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
