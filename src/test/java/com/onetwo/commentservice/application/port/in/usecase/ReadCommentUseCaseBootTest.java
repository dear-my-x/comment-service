package com.onetwo.commentservice.application.port.in.usecase;

import com.onetwo.commentservice.application.port.in.command.FindCommentDetailCommand;
import com.onetwo.commentservice.application.port.in.command.RegisterCommentCommand;
import com.onetwo.commentservice.application.port.in.response.CommentDetailResponseDto;
import com.onetwo.commentservice.application.port.out.RegisterCommentPort;
import com.onetwo.commentservice.application.service.service.CommentService;
import com.onetwo.commentservice.common.exceptions.BadRequestException;
import com.onetwo.commentservice.common.exceptions.NotFoundResourceException;
import com.onetwo.commentservice.domain.Comment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ReadCommentUseCaseBootTest {

    @Autowired
    private CommentService readCommentUseCase;

    @Autowired
    private RegisterCommentPort registerCommentPort;

    private final Long commentId = 1L;
    private final Long postingId = 1L;
    private final String userId = "testUserId";
    private final String content = "content";

    @Test
    @DisplayName("[단위][Use Case] Comment 상세 조회 - 성공 테스트")
    void readCommentUseCaseSuccessTest() {
        //given
        RegisterCommentCommand registerCommentCommand = new RegisterCommentCommand(userId, postingId, content);
        Comment comment = Comment.createNewCommentByCommand(registerCommentCommand);

        Comment savedComment = registerCommentPort.registerComment(comment);

        FindCommentDetailCommand findCommentDetailCommand = new FindCommentDetailCommand(savedComment.getId());

        //when
        CommentDetailResponseDto result = readCommentUseCase.findCommentsDetail(findCommentDetailCommand);

        //then
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("[단위][Use Case] Comment 상세 조회 comment does not exist - 실패 테스트")
    void readCommentUseCaseCommentDoesNotExistFailTest() {
        //given
        FindCommentDetailCommand findCommentDetailCommand = new FindCommentDetailCommand(commentId);

        //when then
        Assertions.assertThrows(NotFoundResourceException.class, () -> readCommentUseCase.findCommentsDetail(findCommentDetailCommand));
    }

    @Test
    @DisplayName("[단위][Use Case] Comment 상세 조회 comment already deleted - 실패 테스트")
    void readCommentUseCaseCommentAlreadyDeletedFailTest() {
        //given
        RegisterCommentCommand registerCommentCommand = new RegisterCommentCommand(userId, postingId, content);
        Comment comment = Comment.createNewCommentByCommand(registerCommentCommand);

        comment.deleteComment();

        Comment savedComment = registerCommentPort.registerComment(comment);

        FindCommentDetailCommand findCommentDetailCommand = new FindCommentDetailCommand(savedComment.getId());

        //when then
        Assertions.assertThrows(BadRequestException.class, () -> readCommentUseCase.findCommentsDetail(findCommentDetailCommand));
    }
}