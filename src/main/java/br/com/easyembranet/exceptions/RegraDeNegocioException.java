package br.com.easyembranet.exceptions;

public class RegraDeNegocioException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RegraDeNegocioException() {
		super("Regra de negócio inválida.");
	}

	public RegraDeNegocioException(String mensagem) {
		super(mensagem);
	}
}
