package com.floti.api.domain.board.qna.controller;

import com.floti.api.domain.board.qna.dto.AnswerRequest;
import com.floti.api.domain.board.qna.dto.AnswerResponse;
import com.floti.api.domain.board.qna.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/qna")
public class AnswerController {
    private final AnswerService answerService;

    /* 1. 등록 */
    @PostMapping("/posts/{postId}/answers")
    public ResponseEntity<AnswerResponse> createAnswer(@Validated @RequestBody AnswerRequest answer,
                                                       @PathVariable Long postId) {
        AnswerResponse response = answerService.createAnswer(answer.getAuthorId(), postId, answer);
        return ResponseEntity.status(CREATED).body(response); // 201 Created
    }

    /* 2. 수정 */
    @PutMapping("/answers/{id}")
    public ResponseEntity<AnswerResponse> updateAnswer(@Validated @RequestBody AnswerRequest Answer,
                                                         @PathVariable Long id) {
        AnswerResponse response = answerService.updateAnswer(Answer.getAuthorId(), id, Answer);
        return ResponseEntity.ok(response); // 200 Ok
    }

    /* 3. 삭제 */
    @DeleteMapping("/answers/{id}")
    public ResponseEntity<Void> deleteAnswer(@RequestParam Long userId, //임시
                                             @PathVariable Long id) {
        answerService.deleteAnswer(userId, id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    /* 4. 채택 */
    @PatchMapping("/posts/{postId}/answers/{id}/accept")
    public ResponseEntity<?> acceptAnswer(@RequestParam Long userId, //임시
                                          @PathVariable Long postId,
                                          @PathVariable Long id) {
        answerService.acceptAnswer(userId, postId, id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
