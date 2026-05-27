package com.electromart.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Get the absolute path to the static directory
        String currentDir = System.getProperty("user.dir");
        String staticPath = Paths.get(currentDir, "src", "main", "resources", "static")
                .toAbsolutePath().toString();
        
        // Log the paths for debugging
        System.out.println("=== Static Resources Configuration ===");
        System.out.println("Current working directory: " + currentDir);
        System.out.println("Static path: " + staticPath);
        
        // Check if directory exists
        java.io.File staticDir = new java.io.File(staticPath);
        System.out.println("Static directory exists: " + staticDir.exists());
        if (staticDir.exists()) {
            System.out.println("Directories in static:");
            for (String dir : staticDir.list()) {
                System.out.println("  - " + dir);
            }
        }
        System.out.println("======================================");
        
        // Serve ALL static resources from the static folder
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/", "file:" + staticPath + "/")
                .setCachePeriod(3600);
        
        // Specifically for images (covers all subfolders)
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/", "file:" + staticPath + "/images/")
                .setCachePeriod(3600);
        
        // For uploads
        String uploadsPath = Paths.get(currentDir, "uploads").toAbsolutePath().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadsPath + "/")
                .setCachePeriod(3600);
    }
}