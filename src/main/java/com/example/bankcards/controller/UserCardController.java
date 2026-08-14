package com.example.bankcards.controller;

import com.example.bankcards.controller.docs.UserCardControllerDocs;
import com.example.bankcards.dto.card.*;
import com.example.bankcards.service.ServicesGate;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user/cards")
@RequiredArgsConstructor
public class UserCardController implements UserCardControllerDocs {

    private final ServicesGate servicesGate;

    @Override
    @PostMapping
    public ResponseEntity<GetCardsResponseDto> getMyCards(
        @Parameter(hidden = true) @AuthenticationPrincipal UUID uuid,
        @Valid @RequestBody GetCardsRequestDto requestDto,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        GetCardsResponseDto cards = servicesGate.getAllCards(requestDto.setUserId(uuid), pageable);
        return ResponseEntity.ok(cards);
    }

    @Override
    @PostMapping("/balance")
    public ResponseEntity<GetCardBalanceResponseDto> getBalance(
        @Parameter(hidden = true) @AuthenticationPrincipal UUID uuid,
        @Valid @RequestBody GetCardBalanceRequestDto requestDto
    ) {
        GetCardBalanceResponseDto balance = servicesGate.getCardBalance(requestDto.setUserId(uuid));
        return ResponseEntity.ok(balance);
    }

    @Override
    @PostMapping("/block")
    public ResponseEntity<Void> blockCard(
        @Parameter(hidden = true) @AuthenticationPrincipal UUID uuid,
        @Valid @RequestBody BlockCardRequestDto requestDto
    ) {
        servicesGate.requestCardBlock(requestDto.setUserId(uuid));
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/transfer")
    public ResponseEntity<Void> transferBetweenOwnCards(
        @Parameter(hidden = true) @AuthenticationPrincipal UUID uuid,
        @Valid @RequestBody TransferRequestDto requestDto
    ) {
        servicesGate.transferFunds(requestDto.setUserId(uuid));
        return ResponseEntity.ok().build();
    }
}
