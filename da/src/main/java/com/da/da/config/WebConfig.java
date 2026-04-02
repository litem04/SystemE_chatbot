package com.da.da.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
   /* @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Lấy đường dẫn gốc của dự án
        Path uploadDir = Paths.get("product-images");
        String uploadPath = uploadDir.toFile().getAbsolutePath();
        registry.addResourceHandler("/product-images/**")
                .addResourceLocations("file:/" + uploadPath + "/"); 
              
    } */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	    // Khi chạy trong Docker, thư mục ảnh sẽ nằm ở /app/product-images/ theo cấu hình Volumes
	    registry.addResourceHandler("/product-images/**")
	            .addResourceLocations("file:./product-images/");
	}
}