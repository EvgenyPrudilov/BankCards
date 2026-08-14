package com.example.bankcards.service.card;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.card.*;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.model.*;
import com.example.bankcards.service.model.enums.CardStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void requestCardBlock(BlockCardRequest request) {
        CardEntity cardEntity = cardRepository.findByUuidAndUserEntity_Uuid(request.getCardId(), request.getUserId())
            .orElseThrow(WrongCardException::new);
        cardEntity.setStatus(CardStatus.BLOCKED);
        cardRepository.save(cardEntity);
    }

    @Override
    @Transactional
    public void transferFunds(TransferRequest request) {
        BigDecimal amount = request.getAmount();
        UUID fromCardId = request.getFromCardId();
        UUID toCardId = request.getToCardId();
        UUID userId = request.getUserId();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadTransferAmoundException();
        }

        if (fromCardId.equals(toCardId)) {
            throw new SameCardException();
        }

        CardEntity fromCardEntity = cardRepository.findByUuidAndUserEntity_Uuid(fromCardId, userId)
            .orElseThrow(WrongCardException::new);
        CardEntity toCardEntity = cardRepository.findByUuidAndUserEntity_Uuid(toCardId, userId)
            .orElseThrow(WrongCardException::new);

        if (fromCardEntity.getStatus() != CardStatus.ACTIVE) {
            throw new NotActiveDebitCardException();
        }
        if (toCardEntity.getStatus() != CardStatus.ACTIVE) {
            throw new NotActiveDepositCardException();
        }
        if (fromCardEntity.getBalance().compareTo(amount) < 0) {
            throw new NotEnoughAmountException();
        }

        fromCardEntity.setBalance(fromCardEntity.getBalance().subtract(amount));
        toCardEntity.setBalance(toCardEntity.getBalance().add(amount));

        cardRepository.save(fromCardEntity);
        cardRepository.save(toCardEntity);
    }

    @Override
    public GetCardBalanceResponse getCardBalance(GetCardBalanceRequest request) {
        CardEntity cardEntity = cardRepository.findByUuidAndUserEntity_Uuid(request.getCardId(), request.getUserId())
            .orElseThrow(WrongCardException::new);
        return new GetCardBalanceResponse(cardEntity.getBalance());
    }

    @Override
    @Transactional
    public CardEntity createCard(CreateCardRequest request) {
//        UserEntity userEntity = userRepository.findByUuid(request.getUserId())
//            .orElseThrow(UserNotFoundException::new);
        UserEntity userEntity = userRepository.findByUsername(request.getUserName())
            .orElseThrow(UserNotFoundException::new);

        CardEntity cardEntity = CardEntity.builder()
            .userEntity(userEntity)
            .cardNumber(generateRandomCardNumber())
            .holderName(request.getHolderName().toUpperCase())
            .expiryDate(java.time.ZonedDateTime.now().plusYears(5).toInstant())
            .status(CardStatus.ACTIVE)
            .balance(request.getInitBalance() == null ? BigDecimal.ZERO : request.getInitBalance())
            .build();

        return cardRepository.save(cardEntity);
    }

    @Override
    @Transactional
    public void updateCardStatus(UpdateCardStatusRequest request) {
        CardEntity cardEntity = cardRepository.findByUuid(request.getCardId())
            .orElseThrow(CardNotFoundException::new);
        cardEntity.setStatus(request.getCardStatus());
        cardRepository.save(cardEntity);
    }

    @Override
    @Transactional
    public void deleteCard(DeleteCardRequest request) {
        UUID uuid = request.getCardId();

        if (!cardRepository.existsByUuid(uuid)) {
            throw new CardNotFoundException();
        }
        cardRepository.deleteByUuid(uuid);
    }

    @Override
    public Page<CardEntity> getAllCards(GetCardsAdminRequest request, Pageable pageable) {
        UserEntity userEntity = userRepository.findByUsername(request.getUserName())
            .orElseThrow(UserNotFoundException::new);
        if (request.getStatus() == null) {
            return cardRepository.findAllByUserEntity_Uuid(userEntity.getUuid(), pageable);
        }
        return cardRepository.findByUserIdAndOptionalStatus(userEntity.getUuid(), request.getStatus(), pageable);
    }

    @Override
    public Page<CardEntity> getAllCards(GetCardsRequest request, Pageable pageable) {
        if (request.getStatus() == null) {
            return cardRepository.findAllByUserEntity_Uuid(request.getUserId(), pageable);
        }
        return cardRepository.findByUserIdAndOptionalStatus(request.getUserId(), request.getStatus(), pageable);
    }

    private String generateRandomCardNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
