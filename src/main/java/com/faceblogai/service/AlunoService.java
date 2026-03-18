package com.faceblogai.service;

import com.faceblogai.domain.Aluno;
import com.faceblogai.repository.AlunoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    public List<Aluno> listarTodos() {
        return alunoRepository.findAll();
    }

    public Optional<Aluno> buscarPorId(Long id) {
        return alunoRepository.findById(id);
    }

    public Aluno criar(String nome, String matricula) {
        Aluno aluno = new Aluno(nome, matricula);
        return alunoRepository.save(aluno);
    }

    public Optional<Aluno> atualizar(Long id, String nome) {
        return alunoRepository
                .findById(id)
                .map(aluno -> {
                    aluno.setNome(nome);
                    return alunoRepository.save(aluno);
                });
    }

    public void deletar(Long id) {
        alunoRepository.deleteById(id);
    }
}
