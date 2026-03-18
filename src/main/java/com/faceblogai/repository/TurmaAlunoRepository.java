package com.faceblogai.repository;

import com.faceblogai.domain.Aluno;
import com.faceblogai.domain.Turma;
import com.faceblogai.domain.TurmaAluno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TurmaAlunoRepository extends JpaRepository<TurmaAluno, Long> {

    List<TurmaAluno> findByTurma(Turma turma);

    List<TurmaAluno> findByAluno(Aluno aluno);

    Optional<TurmaAluno> findByTurmaAndAluno(Turma turma, Aluno aluno);
}
