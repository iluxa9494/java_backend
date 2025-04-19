package searchengine.exceptions.general;

import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import searchengine.dto.search.ErrorResponse;
import searchengine.exceptions.indexing.IndexingNotRunningException;
import searchengine.exceptions.indexing.SiteAlreadyExistsException;
import searchengine.exceptions.validation.InvalidQueryException;
import searchengine.exceptions.validation.InvalidUrlException;

/**
 * Глобальный обработчик исключений, возвращающий корректные HTTP-ответы
 * с сообщениями об ошибках в формате JSON.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalState(IllegalStateException ex) {
        return new ErrorResponse(false, ex.getMessage());
    }

    @ExceptionHandler(InvalidUrlException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidUrl(InvalidUrlException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(UrlNotAllowedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleUrlNotAllowed(UrlNotAllowedException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(SiteAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleSiteAlreadyExists(SiteAlreadyExistsException ex) {
        return new ErrorResponse(false, ex.getMessage());
    }

    @ExceptionHandler(InvalidQueryException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidQuery(InvalidQueryException ex) {
        return new ErrorResponse(false, ex.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingParams(MissingServletRequestParameterException ex) {
        return new ErrorResponse(false, "Отсутствуют обязательные параметры запроса");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponse handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return new ErrorResponse(false, "Метод запроса не поддерживается");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoHandlerFound(NoHandlerFoundException ex) {
        return new ErrorResponse(false, "Запрашиваемый ресурс не найден");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneralException(Exception ex) {
        return new ErrorResponse(false, "Ошибка при выполнении поиска");
    }

    @ExceptionHandler(IndexingNotRunningException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIndexingNotRunning(IndexingNotRunningException ex) {
        return new ErrorResponse(false, ex.getMessage());
    }
}