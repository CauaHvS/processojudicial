package com.attus.processojudicial.exception;

public class ProcessoNotFoundException extends RuntimeException {

    public ProcessoNotFoundException(Long id) {
        super("Processo não encontrado com id: " + id);
    }

    public ProcessoNotFoundException(String numero) {
        super("Processo não encontrado com o número" + numero);
    }
}
