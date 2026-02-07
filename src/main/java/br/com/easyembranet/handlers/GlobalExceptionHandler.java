package br.com.easyembranet.handlers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.easyembranet.exceptions.NaoEncontradoException;
import br.com.easyembranet.exceptions.RegraDeNegocioException;

@ControllerAdvice
public class GlobalExceptionHandler {

	public ResponseEntity<Object> createResponse(HttpStatus status, Exception ex) {
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("datetime", LocalDateTime.now());
		body.put("status", status.value());
		body.put("message", ex.getMessage());

		return ResponseEntity.status(status.value()).body(body);
	}

	@ExceptionHandler(RegraDeNegocioException.class)
	public ResponseEntity<Object> handlerRegraNegocio(RegraDeNegocioException ex) {
		return createResponse(HttpStatus.UNPROCESSABLE_CONTENT, ex);
	}
	
	@ExceptionHandler(NaoEncontradoException.class)
	public ResponseEntity<Object> handlerNaoEncontrado(NaoEncontradoException ex) {
		return createResponse(HttpStatus.NOT_FOUND, ex);
	}
}