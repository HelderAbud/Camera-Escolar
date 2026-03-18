package com.faceblogai.repository;

import com.faceblogai.domain.Escola;
import com.faceblogai.domain.Turma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TurmaRepository extends JpaRepository<Turma, Long> {

    List<Turma> findByEscola(Escola escola);
}
