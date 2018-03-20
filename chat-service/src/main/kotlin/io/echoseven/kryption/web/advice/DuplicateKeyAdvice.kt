package io.echoseven.kryption.web.advice

import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import javax.servlet.http.HttpServletResponse

@ControllerAdvice
class DuplicateKeyAdvice : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [DuplicateKeyException::class])
    fun handleDuplicateKeyException(ex: DuplicateKeyException, response: HttpServletResponse) {
        response.sendError(BAD_REQUEST.value(), "Duplicate data provided")
    }
}