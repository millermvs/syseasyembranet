package br.com.easyembranet.handlers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ValidationExceptionHandler {
	
	// Captura qualquer erro interno do servidor (500)
	/*@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handlerErroInterno(Exception ex) {
	    Map<String, Object> body = new HashMap<String, Object>();
	    body.put("datahora", LocalDateTime.now());
	    body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
	    body.put("errors", "Erro interno no servidor. Tente novamente mais tarde.");
	    body.put("fordev", ex.getMessage());

	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(body);
	}*/

	// Captura erros de preenchimento de JSON (corpo malformado)
	@ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
	public ResponseEntity<Object> handlerJsonInvalido(
			org.springframework.http.converter.HttpMessageNotReadableException ex) {
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("datahora", LocalDateTime.now());
		body.put("status", HttpStatus.BAD_REQUEST.value());
		body.put("errors", "JSON inválido. Verifique a sintaxe do corpo da requisição.");

		return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(body);
	}
	
	// Captura erros de rota ou caminho inexistente (404)
	@ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
	public ResponseEntity<Object> handlerRotaNaoEncontrada(org.springframework.web.servlet.resource.NoResourceFoundException ex) {
	    Map<String, Object> body = new HashMap<String, Object>();
	    body.put("datahora", LocalDateTime.now());
	    body.put("status", HttpStatus.NOT_FOUND.value());
	    body.put("errors", "Rota não encontrada. Verifique o caminho da requisição.");

	    return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(body);
	}
	
	// Captura erros de validação de DTOs com @Valid
		@ExceptionHandler(MethodArgumentNotValidException.class)
		public ResponseEntity<Object> handlerMethodArgumentNotValid(MethodArgumentNotValidException ex) {

			Map<String, Object> body = new HashMap<String, Object>();
			body.put("datahora", LocalDateTime.now());
			body.put("status", HttpStatus.BAD_REQUEST.value());

			// Lista de erros: campo -> mensagem
			Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
					.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (msg1, msg2) -> msg1));

			body.put("errors", errors);

			return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(body);
		}

}