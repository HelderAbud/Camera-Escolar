package com.faceblogai.repository;

import com.faceblogai.domain.Camera;
import com.faceblogai.domain.Escola;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CameraRepository extends JpaRepository<Camera, Long> {

    List<Camera> findByEscola(Escola escola);
}
