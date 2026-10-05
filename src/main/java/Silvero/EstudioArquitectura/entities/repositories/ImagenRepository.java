package Silvero.EstudioArquitectura.entities.repositories;

import Silvero.EstudioArquitectura.entities.models.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImagenRepository extends JpaRepository<Imagen, Long> {

    List<Imagen> findBySeccionOrderByOrdenAsc(String seccion);

    List<Imagen> findBySeccion(String seccion);

    Optional<Imagen> findByPublicId(String publicId);
}
