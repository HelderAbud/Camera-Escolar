package com.faceblogai.repository;

import com.faceblogai.domain.Camera;
import com.faceblogai.domain.CameraTurma;
import com.faceblogai.domain.Turma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CameraTurmaRepository extends JpaRepository<CameraTurma, Long> {

    List<CameraTurma> findByTurma(Turma turma);

    List<CameraTurma> findByCamera(Camera camera);

    Optional<CameraTurma> findByCameraAndTurma(Camera camera, Turma turma);
}
