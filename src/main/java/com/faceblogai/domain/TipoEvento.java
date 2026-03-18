package com.faceblogai.domain;

/** Enum de tipos válidos de evento (persistidos em {@code evento_monitoramento.tipo_evento}). */
public enum TipoEvento {
    PRESENCA_DETECTADA,
    AUSENCIA_DETECTADA,
    MOVIMENTO_SUSPEITO,
    ROSTO_RECONHECIDO,
    ROSTO_DESCONHECIDO,
    CAMERA_OFFLINE,
    CAMERA_ONLINE
}

