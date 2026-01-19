package br.dev.rodrigopinheiro.chronoletter.letter.infrastructure.adapter.in.web;

import br.dev.rodrigopinheiro.chronoletter.letter.application.dto.*;
import br.dev.rodrigopinheiro.chronoletter.letter.application.port.in.*;
import br.dev.rodrigopinheiro.chronoletter.letter.infrastructure.adapter.in.web.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LetterController {

    private final CreateLetterUseCase createLetterUseCase;
    private final GetLetterUseCase getLetterUseCase;
    private final ListUserLettersUseCase listUserLettersUseCase;
    private final UpdateLetterUseCase updateLetterUseCase;
    private final DeleteLetterUseCase deleteLetterUseCase;

    public LetterController(CreateLetterUseCase createLetterUseCase, GetLetterUseCase getLetterUseCase,
            ListUserLettersUseCase listUserLettersUseCase, UpdateLetterUseCase updateLetterUseCase,
            DeleteLetterUseCase deleteLetterUseCase) {
        this.createLetterUseCase = createLetterUseCase;
        this.getLetterUseCase = getLetterUseCase;
        this.listUserLettersUseCase = listUserLettersUseCase;
        this.updateLetterUseCase = updateLetterUseCase;
        this.deleteLetterUseCase = deleteLetterUseCase;
    }

    @PostMapping
    public ResponseEntity<LetterResponse> createLetter(
            @RequestBody CreateLetterRequest request,
            @RequestHeader("X-User-Id") String userId) {
        CreateLetterCommand command = new CreateLetterCommand(
                userId,
                request.content(),
                request.scheduledDate(),
                request.timezone());
        LetterResponse response = createLetterUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{letterId}")
    public ResponseEntity<LetterResponse> getLetter(
            @PathVariable String letterId,
            @RequestHeader("X-User-Id") String userId) {
        LetterResponse response = getLetterUseCase.execute(letterId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<LetterSummaryResponse>> listUserLetters(
            @RequestHeader("X-User-Id") String userId) {
        List<LetterSummaryResponse> letters = listUserLettersUseCase.execute(userId);
        return ResponseEntity.ok(letters);
    }

    @PutMapping("/{letterId}")
    public ResponseEntity<LetterResponse> updateLetter(
            @PathVariable String letterId,
            @RequestBody UpdateLetterRequest request,
            @RequestHeader("X-User-Id") String userId) {
        UpdateLetterCommand command = new UpdateLetterCommand(
                letterId,
                userId,
                request.content(),
                request.scheduledDate());
        LetterResponse response = updateLetterUseCase.execute(command);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{letterId}")
    public ResponseEntity<Void> deleteLetter(
            @PathVariable String letterId,
            @RequestHeader("X-User-Id") String userId) {
        deleteLetterUseCase.execute(letterId, userId);
        return ResponseEntity.noContent().build();
    }

}
