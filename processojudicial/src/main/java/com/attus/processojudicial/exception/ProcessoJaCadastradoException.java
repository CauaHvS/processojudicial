package com.attus.processojudicial.exception;

public class ProcessoJaCadastradoException extends RuntimeException {

    public ProcessoJaCadastradoException(String numero) {
        super("Já existe um processo cadastrado com o número" + numero);
    }
}
