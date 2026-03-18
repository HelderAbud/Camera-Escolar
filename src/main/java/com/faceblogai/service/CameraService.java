package com.faceblogai.service;

import com.faceblogai.domain.Camera;
import com.faceblogai.domain.Escola;
import com.faceblogai.repository.CameraRepository;
import com.faceblogai.repository.EscolaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CameraService {

    private final CameraRepository cameraRepository;
    private final EscolaRepository escolaRepository;

    public CameraService(CameraRepository cameraRepository, EscolaRepository escolaRepository) {
        this.cameraRepository = cameraRepository;
        this.escolaRepository = escolaRepository;
    }

    public List<Camera> listarPorEscola(Long escolaId) {
        Escola escola = escolaRepository.findById(escolaId)
                .orElseThrow(() -> new IllegalArgumentException("Escola não encontrada"));
        return cameraRepository.findByEscola(escola);
    }

    public Optional<Camera> buscarPorId(Long id) {
        return cameraRepository.findById(id);
    }

    @Transactional
    public Camera criar(Long escolaId, String nome, String endpointUrl) {
        Escola escola = escolaRepository.findById(escolaId)
                .orElseThrow(() -> new IllegalArgumentException("Escola não encontrada"));
        Camera camera = new Camera(escola, nome, endpointUrl);
        return cameraRepository.save(camera);
    }

    @Transactional
    public Optional<Camera> atualizar(Long id, String nome, String endpointUrl, boolean ativo) {
        return cameraRepository
                .findById(id)
                .map(camera -> {
                    camera.setNome(nome);
                    camera.setEndpointUrl(endpointUrl);
                    camera.setAtivo(ativo);
                    return cameraRepository.save(camera);
                });
    }

    @Transactional
    public void deletar(Long id) {
        cameraRepository.deleteById(id);
    }
}
