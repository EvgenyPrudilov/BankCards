package com.example.bankcards.service.card;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.exception.card.*;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.model.*;
import com.example.bankcards.service.model.enums.CardStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование сервиса карт CardServiceImpl")
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CardServiceImpl cardService;

    private UUID userId;
    private UUID globalCardId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        globalCardId = UUID.randomUUID();
    }


    @Nested
    @DisplayName("Блокировка карты (requestCardBlock)")
    class RequestCardBlockTests {

        @Test
        @DisplayName("Успешная блокировка существующей карты")
        void success() {

            BlockCardRequest request = mock(BlockCardRequest.class);
            when(request.getCardId()).thenReturn(globalCardId);
            when(request.getUserId()).thenReturn(userId);

            CardEntity card = CardEntity.builder().status(CardStatus.ACTIVE).build();

            when(cardRepository.findByUuidAndUserEntity_Uuid(globalCardId, userId)).thenReturn(Optional.of(card));

            assertDoesNotThrow(() -> cardService.requestCardBlock(request));

            assertEquals(CardStatus.BLOCKED, card.getStatus());
            verify(cardRepository, times(1)).save(card);
        }

        @Test
        @DisplayName("Ошибка: Карта не найдена или не принадлежит пользователю")
        void throwsWrongCardException() {
            BlockCardRequest request = mock(BlockCardRequest.class);
            when(request.getCardId()).thenReturn(globalCardId);
            when(request.getUserId()).thenReturn(userId);

            when(cardRepository.findByUuidAndUserEntity_Uuid(globalCardId, userId)).thenReturn(Optional.empty());

            assertThrows(WrongCardException.class, () -> cardService.requestCardBlock(request));
            verify(cardRepository, never()).save(any());
        }
    }


    @Nested
    @DisplayName("Перевод средств (transferFunds)")
    class TransferFundsTests {

        private UUID fromCardId;
        private UUID toCardId;
        private CardEntity fromCard;
        private CardEntity toCard;

        @BeforeEach
        void init() {
            fromCardId = UUID.randomUUID();
            toCardId = UUID.randomUUID();
            fromCard = CardEntity.builder().status(CardStatus.ACTIVE).balance(new BigDecimal("100.00")).build();
            toCard = CardEntity.builder().status(CardStatus.ACTIVE).balance(new BigDecimal("50.00")).build();
        }

        @Test
        @DisplayName("Успешный перевод между своими картами")
        void success() {
            TransferRequest request = mock(TransferRequest.class);
            when(request.getUserId()).thenReturn(userId);
            when(request.getFromCardId()).thenReturn(fromCardId);
            when(request.getToCardId()).thenReturn(toCardId);
            when(request.getAmount()).thenReturn(new BigDecimal("40.00"));

            when(cardRepository.findByUuidAndUserEntity_Uuid(fromCardId, userId)).thenReturn(Optional.of(fromCard));
            when(cardRepository.findByUuidAndUserEntity_Uuid(toCardId, userId)).thenReturn(Optional.of(toCard));

            assertDoesNotThrow(() -> cardService.transferFunds(request));

            assertEquals(new BigDecimal("60.00"), fromCard.getBalance());
            assertEquals(new BigDecimal("90.00"), toCard.getBalance());
            verify(cardRepository, times(1)).save(fromCard);
            verify(cardRepository, times(1)).save(toCard);
        }

        @Test
        @DisplayName("Ошибка: Сумма перевода меньше или равна нулю")
        void throwsBadTransferAmountException() {
            TransferRequest request = mock(TransferRequest.class);
            when(request.getAmount()).thenReturn(BigDecimal.ZERO);

            assertThrows(BadTransferAmoundException.class, () -> cardService.transferFunds(request));
        }

        @Test
        @DisplayName("Ошибка: Перевод на ту же самую карту")
        void throwsSameCardException() {
            TransferRequest request = mock(TransferRequest.class);
            when(request.getAmount()).thenReturn(new BigDecimal("10.00"));
            when(request.getFromCardId()).thenReturn(fromCardId);
            when(request.getToCardId()).thenReturn(fromCardId);

            assertThrows(SameCardException.class, () -> cardService.transferFunds(request));
        }

        @Test
        @DisplayName("Ошибка: Карта списания не активна")
        void throwsNotActiveDebitCardException() {
            TransferRequest request = mock(TransferRequest.class);
            when(request.getUserId()).thenReturn(userId);
            when(request.getFromCardId()).thenReturn(fromCardId);
            when(request.getToCardId()).thenReturn(toCardId);
            when(request.getAmount()).thenReturn(new BigDecimal("10.00"));

            fromCard.setStatus(CardStatus.BLOCKED);

            when(cardRepository.findByUuidAndUserEntity_Uuid(fromCardId, userId)).thenReturn(Optional.of(fromCard));
            when(cardRepository.findByUuidAndUserEntity_Uuid(toCardId, userId)).thenReturn(Optional.of(toCard));

            assertThrows(NotActiveDebitCardException.class, () -> cardService.transferFunds(request));
        }

        @Test
        @DisplayName("Ошибка: Недостаточно средств на карте списания")
        void throwsNotEnoughAmountException() {
            TransferRequest request = mock(TransferRequest.class);
            when(request.getUserId()).thenReturn(userId);
            when(request.getFromCardId()).thenReturn(fromCardId);
            when(request.getToCardId()).thenReturn(toCardId);
            when(request.getAmount()).thenReturn(new BigDecimal("150.00"));

            when(cardRepository.findByUuidAndUserEntity_Uuid(fromCardId, userId)).thenReturn(Optional.of(fromCard));
            when(cardRepository.findByUuidAndUserEntity_Uuid(toCardId, userId)).thenReturn(Optional.of(toCard));

            assertThrows(NotEnoughAmountException.class, () -> cardService.transferFunds(request));
        }
    }


    @Nested
    @DisplayName("Получение баланса карты (getCardBalance)")
    class GetCardBalanceTests {

        @Test
        @DisplayName("Успешное получение текущего баланса")
        void success() {
            GetCardBalanceRequest request = mock(GetCardBalanceRequest.class);
            when(request.getCardId()).thenReturn(globalCardId);
            when(request.getUserId()).thenReturn(userId);

            CardEntity card = CardEntity.builder().balance(new BigDecimal("123.45")).build();

            when(cardRepository.findByUuidAndUserEntity_Uuid(globalCardId, userId)).thenReturn(Optional.of(card));

            GetCardBalanceResponse response = cardService.getCardBalance(request);

            assertNotNull(response);
            assertEquals(new BigDecimal("123.45"), response.getBalance());
        }
    }


//    @Nested
//    @DisplayName("Создание карты (createCard)")
//    class CreateCardTests {
//
//        @Captor
//        private ArgumentCaptor<CardEntity> cardCaptor;
//
//        @Test
//        @DisplayName("Успешное создание новой активной карты")
//        void success() {
//
//            CreateCardRequest request = mock(CreateCardRequest.class);
//            when(request.getUserId()).thenReturn(userId);
//            when(request.getHolderName()).thenReturn("John Doe");
//
//            UserEntity user = new UserEntity();
//            CardEntity savedCard = CardEntity.builder().cardNumber("1234123412341234").build();
//
//            when(userRepository.findByUuid(userId)).thenReturn(Optional.of(user));
//            when(cardRepository.save(any(CardEntity.class))).thenReturn(savedCard);
//
//
//            Instant expectedExpiryWindow = Instant.now().plus(5 * 365, java.time.temporal.ChronoUnit.DAYS);
//
//
//            CardEntity result = cardService.createCard(request);
//
//
//            assertNotNull(result);
//            verify(cardRepository).save(cardCaptor.capture());
//            CardEntity capturedCard = cardCaptor.getValue();
//
//            assertEquals(user, capturedCard.getUserEntity());
//            assertEquals("JOHN DOE", capturedCard.getHolderName());
//            assertEquals(CardStatus.ACTIVE, capturedCard.getStatus());
//            assertEquals(BigDecimal.ZERO, capturedCard.getBalance());
//            assertNotNull(capturedCard.getCardNumber());
//            assertEquals(16, capturedCard.getCardNumber().length());
//
//
//            assertNotNull(capturedCard.getExpiryDate());
//            assertTrue(capturedCard.getExpiryDate().isAfter(expectedExpiryWindow.minusSeconds(60)),
//                "Expiry date should be around 5 years from now");
//        }
//
//        @Test
//        @DisplayName("Ошибка: Пользователь не найден при создании карты")
//        void throwsUserNotFoundException() {
//            CreateCardRequest request = mock(CreateCardRequest.class);
//            when(request.getUserId()).thenReturn(userId);
//            when(userRepository.findByUuid(userId)).thenReturn(Optional.empty());
//            assertThrows(UserNotFoundException.class, () -> cardService.createCard(request));
//            verify(cardRepository, never()).save(any());
//        }
//    }


    @Nested
    @DisplayName("Удаление карты (deleteCard)")
    class DeleteCardTests {
        @Test
        @DisplayName("Успешное удаление карты")
        void success() {
            DeleteCardRequest request = mock(DeleteCardRequest.class);
            when(request.getCardId()).thenReturn(globalCardId);
            when(cardRepository.existsByUuid(globalCardId)).thenReturn(true);
            assertDoesNotThrow(() -> cardService.deleteCard(request));
            verify(cardRepository, times(1)).deleteByUuid(globalCardId);
        }

        @Test
        @DisplayName("Ошибка: Карта для удаления не найдена")
        void throwsCardNotFoundException() {
            DeleteCardRequest request = mock(DeleteCardRequest.class);
            when(request.getCardId()).thenReturn(globalCardId);
            when(cardRepository.existsByUuid(globalCardId)).thenReturn(false);
            assertThrows(CardNotFoundException.class, () -> cardService.deleteCard(request));
            verify(cardRepository, never()).deleteByUuid(any());
        }
    }


//    @Nested
//    @DisplayName("Получение списка карт (getAllCards)")
//    class GetAllCardsTests {
//        private Pageable pageable;
//        private Page emptyPage;
//
//        @BeforeEach
//        void init() {
//            pageable = PageRequest.of(0, 10);
//            emptyPage = new PageImpl<>(Collections.emptyList());
//        }
//
//        @Test
//        @DisplayName("Запрос без фильтрации по статусу — возвращает все карты пользователя")
//        void statusIsNull_ReturnsAllCards() {
//            GetCardsRequest request = mock(GetCardsRequest.class);
//            when(request.getUserId()).thenReturn(userId);
//            when(request.getStatus()).thenReturn(null);
//            when(cardRepository.findAllByUuid(userId, pageable)).thenReturn(emptyPage);
//            Page result = cardService.getAllCards(request, pageable);
//            assertNotNull(result);
//            verify(cardRepository, times(1)).findAllByUuid(userId, pageable);
//            verify(cardRepository, never()).findByUserIdAndOptionalStatus(any(), any(), any());
//        }
//
//        @Test
//        @DisplayName("Запрос с фильтром по статусу — возвращает отфильтрованные карты")
//        void statusIsPresent_ReturnsFilteredCards() {
//            GetCardsRequest request = mock(GetCardsRequest.class);
//            when(request.getUserId()).thenReturn(userId);
//            when(request.getStatus()).thenReturn(CardStatus.ACTIVE);
//            when(cardRepository.findByUserIdAndOptionalStatus(userId, CardStatus.ACTIVE, pageable)).thenReturn(emptyPage);
//            Page result = cardService.getAllCards(request, pageable);
//            assertNotNull(result);
//            verify(cardRepository, times(1)).findByUserIdAndOptionalStatus(userId, CardStatus.ACTIVE, pageable);
//            verify(cardRepository, never()).findAllByUuid(any(), any());
//        }
//    }
}