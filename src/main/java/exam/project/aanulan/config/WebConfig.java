package exam.project.aanulan.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Применяем CORS ко всем путям
                .allowedOrigins("http://localhost:3000")  // Разрешаем только запросы с фронтенда на этом порту
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // Разрешаем методы
                .allowedHeaders("*")
                .allowCredentials(true);// Разрешаем любые заголовки
    }
}