package com.example.bankcards.service;

import com.example.bankcards.dto.auth.*;
import com.example.bankcards.dto.card.*;
import com.example.bankcards.dto.mapper.CardMapper;
import com.example.bankcards.dto.mapper.UserMapper;
import com.example.bankcards.service.auth.AdminInvitationService;
import com.example.bankcards.service.auth.AuthService;
import com.example.bankcards.service.card.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServicesGate {
    private final AuthService authService;
    private final AdminInvitationService adminInvitationService;
    private final CardService cardService;
    private final UserMapper userMapper;
    private final CardMapper cardMapper;


    public void registerNewUser(RegisterRequestDto requestDto) {
        authService.registerNewUser(userMapper.toDomain(requestDto));
    }

    public LoginResponseDto login(LoginRequestDto requestDto) {
        return userMapper.toDto(authService.login(userMapper.toDomain(requestDto)));
    }

    public RefreshResponseDto useRefreshToken(String refreshToken) {
        return userMapper.toDto(authService.useRefreshToken(refreshToken));
    }

    public void logout(String userName) {
        authService.logout(userName);
    }

    public AdminInvitationResponseDto createInvitation(String email) {
        return adminInvitationService.createInvitation(email);
    }

    public void registerAdminByToken(
        AdminRegisterDto registerDto
    ) {
        adminInvitationService.registerAdminByToken(userMapper.toDomain(registerDto));
    }

    public CreateCardResponseDto createCard(CreateCardRequestDto requestDto) {
        return cardMapper.toDto(
            cardService.createCard(cardMapper.toDomain(requestDto))
        );
    }

    public GetCardBalanceResponseDto getCardBalance(GetCardBalanceRequestDto requestDto) {
        return cardMapper.toDto(
            cardService.getCardBalance(cardMapper.toDomain(requestDto))
        );
    }

    public void requestCardBlock(BlockCardRequestDto requestDto) {
        cardService.requestCardBlock(cardMapper.toDomain(requestDto));
    }

    public void updateCardStatus(UpdateCardStatusRequestDto requestDto) {
        cardService.updateCardStatus(cardMapper.toDomain(requestDto));
    }

    public void deleteCard(DeleteCardRequestDto requestDto) {
        cardService.deleteCard(cardMapper.toDomain(requestDto));
    }

    public void transferFunds(TransferRequestDto requestDto) {
        cardService.transferFunds(cardMapper.toDomain(requestDto));
    }

    public GetCardsResponseDto getAllCards(GetCardsRequestDto requestDto, Pageable pageable) {
//        return getAllCards(null, requestDto, pageable);
        return cardMapper.toDto(
            cardService.getAllCards(cardMapper.toDomain(requestDto), pageable)
        );
    }

//    public GetCardsResponseDto getAllCards(UUID uuid, GetCardsRequestDto requestDto, Pageable pageable) {
//        return cardMapper.toDto(
//            cardService.getAllCards(uuid, cardMapper.toDomain(requestDto), pageable)
//        );
//    }
}
