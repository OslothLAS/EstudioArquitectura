package Silvero.EstudioArquitectura.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(ImagenesProperties.class)
public class CloudinaryConfig {

    // Solo se crea si la variable de entorno CLOUDINARY_URL está definida.
    // Sin ella la app funciona igual, pero no sincroniza imágenes.
    @Bean
    @ConditionalOnExpression("!'${app.cloudinary.url:}'.isBlank()")
    public Cloudinary cloudinary(@Value("${app.cloudinary.url}") String cloudinaryUrl) {
        Cloudinary cloudinary = new Cloudinary(cloudinaryUrl);
        cloudinary.config.secure = true;
        return cloudinary;
    }
}
