package com.example.bankcards.service.card;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.service.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardService {
    //    Page<CardEntity> getUserCards(Long userId, CardStatus status, Pageable pageable);
    void requestCardBlock(BlockCardRequest request);

    void transferFunds(TransferRequest request);

    GetCardBalanceResponse getCardBalance(GetCardBalanceRequest request);

    CardEntity createCard(CreateCardRequest request);

    void updateCardStatus(UpdateCardStatusRequest request);

    void deleteCard(DeleteCardRequest request);

    Page<CardEntity> getAllCards(GetCardsRequest request, Pageable pageable);
//    Page<CardEntity> getAllCards(UUID uuid, GetCardsRequest request, Pageable pageable);
}
