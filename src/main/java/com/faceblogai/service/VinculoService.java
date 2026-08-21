package com.faceblogai.service;

import com.faceblogai.domain.*;
import com.faceblogai.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VinculoService {

    private final TurmaRepository turmaRepository;
    private final AlunoRepository alunoRepository;
    private final TurmaAlunoRepository turmaAlunoRepository;
    private final CameraRepository cameraRepository;
    private final CameraTurmaRepository cameraTurmaRepository;

    public VinculoService(
            TurmaRepository turmaRepository,
            AlunoRepository alunoRepository,
            TurmaAlunoRepository turmaAlunoRepository,
            CameraRepository cameraRepository,
            CameraTurmaRepository cameraTurmaRepository) {
        this.turmaRepository = turmaRepository;
        this.alunoRepository = alunoRepository;
        this.turmaAlunoRepository = turmaAlunoRepository;
        this.cameraRepository = cameraRepository;
        this.cameraTurmaRepository = cameraTurmaRepository;
    }

    @Transactional
    public TurmaAluno vincularAlunoEmTurma(Long turmaId, Long alunoId) {
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada"));
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));

        TurmaAluno vinculo =
                turmaAlunoRepository
                        .findByTurmaAndAluno(turma, aluno)
                        .orElseGet(() -> turmaAlunoRepository.save(new TurmaAluno(turma, aluno)));
        vinculo.getTurma().getId();
        vinculo.getAluno().getNome();
        return vinculo;
    }

    @Transactional
    public void desvincularAlunoDeTurma(Long turmaId, Long alunoId) {
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada"));
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));

        turmaAlunoRepository
                .findByTurmaAndAluno(turma, aluno)
                .ifPresent(turmaAlunoRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<TurmaAluno> listarAlunosDaTurma(Long turmaId) {
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada"));
        List<TurmaAluno> vinculos = turmaAlunoRepository.findByTurma(turma);
        vinculos.forEach(
                vinculo -> {
                    vinculo.getTurma().getId();
                    vinculo.getAluno().getNome();
                });
        return vinculos;
    }

    @Transactional
    public CameraTurma vincularCameraEmTurma(Long turmaId, Long cameraId) {
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada"));
        Camera camera = cameraRepository.findById(cameraId)
                .orElseThrow(() -> new IllegalArgumentException("Câmera não encontrada"));

        CameraTurma vinculo =
                cameraTurmaRepository
                        .findByCameraAndTurma(camera, turma)
                        .orElseGet(() -> cameraTurmaRepository.save(new CameraTurma(camera, turma)));
        vinculo.getTurma().getId();
        vinculo.getCamera().getNome();
        return vinculo;
    }

    @Transactional
    public void desvincularCameraDeTurma(Long turmaId, Long cameraId) {
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada"));
        Camera camera = cameraRepository.findById(cameraId)
                .orElseThrow(() -> new IllegalArgumentException("Câmera não encontrada"));

        cameraTurmaRepository
                .findByCameraAndTurma(camera, turma)
                .ifPresent(cameraTurmaRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<CameraTurma> listarCamerasDaTurma(Long turmaId) {
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new IllegalArgumentException("Turma não encontrada"));
        List<CameraTurma> vinculos = cameraTurmaRepository.findByTurma(turma);
        vinculos.forEach(
                vinculo -> {
                    vinculo.getTurma().getId();
                    vinculo.getCamera().getNome();
                });
        return vinculos;
    }
}
