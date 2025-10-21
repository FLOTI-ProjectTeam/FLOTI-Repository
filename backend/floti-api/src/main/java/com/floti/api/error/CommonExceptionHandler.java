package com.floti.api.error;

import com.floti.api.error.exception.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class CommonExceptionHandler {
    /* 공통 예외 처리 */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 403 Forbidden
    }

    @ExceptionHandler({EntityNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<String> handleEntityNotFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 Not Found
    }

    /* 댓글 예외 처리 */
    @ExceptionHandler(ReplyNotAllowedException.class)
    public ResponseEntity<String> handleReplyNotAllowed(ReplyNotAllowedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 403 Forbidden
    }

    /* 답변 예외 처리 */
    @ExceptionHandler({AcceptedAnswerUpdateException.class, AcceptedAnswerDeletionException.class})
    public ResponseEntity<String> handleAcceptedAnswer(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 403 Forbidden
    }

    @ExceptionHandler(PostAlreadyClosedException.class)
    public ResponseEntity<String> handlePostAlreadyClosed(PostAlreadyClosedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 403 Forbidden
    }

    @ExceptionHandler(PostAlreadyAcceptedException.class)
    public ResponseEntity<String> handlePostAlreadyAccepted(PostAlreadyAcceptedException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // 409 Conflict
    }

    /* 참여자 예외 처리 */
    @ExceptionHandler(MaxParticipantExceededException.class)
    public ResponseEntity<String> handleMaxParticipantExceeded(MaxParticipantExceededException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // 409 Conflict
    }

    /* 이미지 예외 처리 */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxUploadSize(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("최대 20MB까지 업로드 가능합니다."); // 413 Payload Too Lager
    }

    @ExceptionHandler({ImageSaveFailedException.class, ImageDeleteFailedException.class})
    public ResponseEntity<String> handleImageFailed(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()); // 500 Internal Server Error
    }
}
