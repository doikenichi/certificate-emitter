package com.br.shizen.certificateemitter.services.answersheet;

import com.br.shizen.certificateemitter.dto.answersheet.AnswerSheetImportResult;
import com.br.shizen.certificateemitter.entity.Student;
import com.br.shizen.certificateemitter.entity.Take;
import com.br.shizen.certificateemitter.repository.ResponseRepository;
import com.br.shizen.certificateemitter.repository.StudentRepository;
import com.br.shizen.certificateemitter.repository.TakeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerSheetPersistenceService {

    private final StudentRepository studentRepository;
    private final TakeRepository takeRepository;
    private final ResponseRepository responseRepository;

    /**
     * Persist the answersheet and return the persisted result.
     *
     * @param answerImport the answersheet to be persisted
     * @return the result of the persistence
     * @throws IllegalStateException if there is more than one or no quiz
     */
    public AnswerSheetImportResult persist(AnswerSheetImportResult answerImport) throws IllegalStateException {
        answerImport.getAnswers().forEach(answer -> {
            Student savedStudent = studentRepository.save(answer.getStudent());
            Take take = answer.getTake();
            take.setStudent(savedStudent);
            Take savedTake = takeRepository.save(take);
            answer.getResponses().forEach(response -> {
                response.setTake(savedTake);
                responseRepository.save(response);
            });
        });
        return answerImport;
    }
}
