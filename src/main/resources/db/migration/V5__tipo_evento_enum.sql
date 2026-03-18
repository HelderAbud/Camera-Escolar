-- Garante que os valores existentes em evento_monitoramento.tipo_evento
-- sejam compatíveis com o enum TipoEvento

UPDATE evento_monitoramento
SET tipo_evento = 'PRESENCA_DETECTADA'
WHERE tipo_evento NOT IN (
    'PRESENCA_DETECTADA',
    'AUSENCIA_DETECTADA',
    'MOVIMENTO_SUSPEITO',
    'ROSTO_RECONHECIDO',
    'ROSTO_DESCONHECIDO',
    'CAMERA_OFFLINE',
    'CAMERA_ONLINE'
);

