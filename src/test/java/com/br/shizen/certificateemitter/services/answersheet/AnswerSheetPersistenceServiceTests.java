package com.br.shizen.certificateemitter.services.answersheet;

import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetImportResult;
import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetResult;
import com.br.shizen.certificateemitter.entity.Response;
import com.br.shizen.certificateemitter.entity.Student;
import com.br.shizen.certificateemitter.entity.Take;
import com.br.shizen.certificateemitter.repository.ResponseRepository;
import com.br.shizen.certificateemitter.repository.StudentRepository;
import com.br.shizen.certificateemitter.repository.TakeRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnswerSheetPersistenceServiceTests {

    @Test
    void persistsStudentThenTakeThenResponsesForEachAnswer() {
        StudentRepository studentRepository = mock(StudentRepository.class);
        TakeRepository takeRepository = mock(TakeRepository.class);
        ResponseRepository responseRepository = mock(ResponseRepository.class);
        AnswerSheetPersistenceService service = new AnswerSheetPersistenceService(studentRepository, takeRepository, responseRepository);

        Student importedStudent = new Student();
        Student savedStudent = new Student();
        Take importedTake = new Take();
        Take savedTake = new Take();
        Response firstResponse = new Response();
        Response secondResponse = new Response();
        AnswerSheetResult answer = new AnswerSheetResult();
        answer.setStudent(importedStudent);
        answer.setTake(importedTake);
        answer.getResponses().addAll(List.of(firstResponse, secondResponse));
        AnswerSheetImportResult importResult = new AnswerSheetImportResult();
        importResult.setAnswers(List.of(answer));

        when(studentRepository.save(importedStudent)).thenReturn(savedStudent);
        when(takeRepository.save(importedTake)).thenReturn(savedTake);

        AnswerSheetImportResult result = service.persist(importResult);

        assertThat(result).isSameAs(importResult);
        assertThat(importedTake.getStudent()).isSameAs(savedStudent);
        assertThat(firstResponse.getTake()).isSameAs(savedTake);
        assertThat(secondResponse.getTake()).isSameAs(savedTake);

        var inOrder = inOrder(studentRepository, takeRepository, responseRepository);
        inOrder.verify(studentRepository).save(importedStudent);
        inOrder.verify(takeRepository).save(importedTake);
        inOrder.verify(responseRepository).save(firstResponse);
        inOrder.verify(responseRepository).save(secondResponse);
    }
}
