package com.example.bankcards.controller.admin;

import com.example.bankcards.controller.docs.admin.CardControllerDocs;
import com.example.bankcards.dto.card.*;
import com.example.bankcards.service.ServicesGate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/cards")
@RequiredArgsConstructor
public class CardManagingController implements CardControllerDocs {

    private final ServicesGate servicesGate;

    @Override
    @PostMapping
    public ResponseEntity<CreateCardResponseDto> createCard(
        @Valid @RequestBody CreateCardRequestDto requestDto
    ) {
        CreateCardResponseDto cardResponseDto = servicesGate.createCard(requestDto);
        return new ResponseEntity<>(cardResponseDto, HttpStatus.CREATED);
    }

    @Override
    @PostMapping("/search")
    public ResponseEntity<GetCardsResponseDto> getAllCards(
        @Valid @RequestBody GetCardsAdminRequestDto requestDto,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        GetCardsResponseDto cards = servicesGate.getAllCards(requestDto, pageable);
        return ResponseEntity.ok(cards);
    }

    @Override
    @PatchMapping("/status")
    public ResponseEntity<Void> updateStatus(
        @Valid @RequestBody UpdateCardStatusRequestDto requestDto
    ) {
        servicesGate.updateCardStatus(requestDto);
        return ResponseEntity.ok().build();
    }

    @Override
    @DeleteMapping
    public ResponseEntity<Void> deleteCard(
        @Valid @RequestBody DeleteCardRequestDto requestDto
    ) {
        servicesGate.deleteCard(requestDto);
        return ResponseEntity.ok().build();
    }
}
