package com.floti.api.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 403 Forbidden
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 Not Found
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<String> handlePostNotFound(PostNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 Not Found
    }

    /* 댓글 관련 예외 */
    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<String> handleCommentNotFound(CommentNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 Not Found
    }

    @ExceptionHandler(ReplyNotAllowedException.class)
    public ResponseEntity<String> handleReplyNotAllowed(ReplyNotAllowedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 403 Forbidden
    }

    /* 답변 관련 예외 */
    @ExceptionHandler(AnswerNotFoundException.class)
    public ResponseEntity<String> handleAnswerNotFound(AnswerNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 Not Found
    }

    @ExceptionHandler(AcceptedAnswerUpdateException.class)
    public ResponseEntity<String> handleAcceptedAnswerUpdate(AcceptedAnswerUpdateException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 403 Not Found
    }

    @ExceptionHandler(AcceptedAnswerDeletionException.class)
    public ResponseEntity<String> handleAcceptedAnswerDeletion(AcceptedAnswerDeletionException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 403 Not Found
    }

    @ExceptionHandler(PostAlreadyClosedException.class)
    public ResponseEntity<String> handlePostAlreadyClosed(PostAlreadyClosedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 403 Not Found
    }

    @ExceptionHandler(PostAlreadyAcceptedException.class)
    public ResponseEntity<String> handlePostAlreadyAccepted(PostAlreadyAcceptedException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // 409 Conflict
    }
}
