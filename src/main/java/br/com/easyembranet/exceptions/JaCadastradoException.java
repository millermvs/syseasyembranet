package br.com.easyembranet.exceptions;

public class JaCadastradoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public JaCadastradoException() {
		super("Ja cadastrado no sistema.");
	}

	public JaCadastradoException(String mensagem) {
		super(mensagem);
	}

}