package com.example.bankcards.service.card;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.UserEntity;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CardServiceImpl Unit Tests")
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
    @DisplayName("requestCardBlock method tests")
    class RequestCardBlockTests {

        @Test
        @DisplayName("Should successfully block an active bank card")
        void requestCardBlock_Success() {
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
        @DisplayName("Should throw WrongCardException when target card is missing or access denied")
        void requestCardBlock_ThrowsWrongCardException() {
            BlockCardRequest request = mock(BlockCardRequest.class);
            when(request.getCardId()).thenReturn(globalCardId);
            when(request.getUserId()).thenReturn(userId);

            when(cardRepository.findByUuidAndUserEntity_Uuid(globalCardId, userId)).thenReturn(Optional.empty());

            assertThrows(WrongCardException.class, () -> cardService.requestCardBlock(request));
            verify(cardRepository, never()).save(any(CardEntity.class));
        }
    }

    @Nested
    @DisplayName("transferFunds method tests")
    class TransferFundsTests {

        @Test
        @DisplayName("Should successfully transfer balance amount between valid active accounts")
        void transferFunds_Success() {
            UUID toCardId = UUID.randomUUID();
            TransferRequest request = mock(TransferRequest.class);
            when(request.getAmount()).thenReturn(new BigDecimal("150.00"));
            when(request.getFromCardId()).thenReturn(globalCardId);
            when(request.getToCardId()).thenReturn(toCardId);
            when(request.getUserId()).thenReturn(userId);

            CardEntity fromCard = CardEntity.builder().status(CardStatus.ACTIVE).balance(new BigDecimal("500.00")).build();
            CardEntity toCard = CardEntity.builder().status(CardStatus.ACTIVE).balance(new BigDecimal("50.00")).build();

            when(cardRepository.findByUuidAndUserEntity_Uuid(globalCardId, userId)).thenReturn(Optional.of(fromCard));
            when(cardRepository.findByUuidAndUserEntity_Uuid(toCardId, userId)).thenReturn(Optional.of(toCard));

            assertDoesNotThrow(() -> cardService.transferFunds(request));
            assertEquals(new BigDecimal("350.00"), fromCard.getBalance());
            assertEquals(new BigDecimal("200.00"), toCard.getBalance());
            verify(cardRepository, times(1)).save(fromCard);
            verify(cardRepository, times(1)).save(toCard);
        }

        @Test
        @DisplayName("Should throw BadTransferAmoundException when money value amount is zero or below")
        void transferFunds_NegativeAmount_ThrowsBadTransferAmoundException() {
            TransferRequest request = mock(TransferRequest.class);
            when(request.getAmount()).thenReturn(new BigDecimal("-10.00"));
            when(request.getFromCardId()).thenReturn(globalCardId);
            when(request.getToCardId()).thenReturn(UUID.randomUUID());

            assertThrows(BadTransferAmoundException.class, () -> cardService.transferFunds(request));
        }

        @Test
        @DisplayName("Should throw SameCardException when destination account mirrors source")
        void transferFunds_IdenticalCards_ThrowsSameCardException() {
            TransferRequest request = mock(TransferRequest.class);
            when(request.getAmount()).thenReturn(new BigDecimal("10.00"));
            when(request.getFromCardId()).thenReturn(globalCardId);
            when(request.getToCardId()).thenReturn(globalCardId);

            assertThrows(SameCardException.class, () -> cardService.transferFunds(request));
        }

        @Test
        @DisplayName("Should throw NotEnoughAmountException when source card balance is short")
        void transferFunds_LowBalance_ThrowsNotEnoughAmountException() {
            UUID toCardId = UUID.randomUUID();
            TransferRequest request = mock(TransferRequest.class);
            when(request.getAmount()).thenReturn(new BigDecimal("100.00"));
            when(request.getFromCardId()).thenReturn(globalCardId);
            when(request.getToCardId()).thenReturn(toCardId);
            when(request.getUserId()).thenReturn(userId);

            CardEntity fromCard = CardEntity.builder().status(CardStatus.ACTIVE).balance(new BigDecimal("20.00")).build();
            CardEntity toCard = CardEntity.builder().status(CardStatus.ACTIVE).balance(new BigDecimal("50.00")).build();

            when(cardRepository.findByUuidAndUserEntity_Uuid(globalCardId, userId)).thenReturn(Optional.of(fromCard));
            when(cardRepository.findByUuidAndUserEntity_Uuid(toCardId, userId)).thenReturn(Optional.of(toCard));

            assertThrows(NotEnoughAmountException.class, () -> cardService.transferFunds(request));
        }
    }

    @Nested
    @DisplayName("getCardBalance method tests")
    class GetCardBalanceTests {

        @Test
        @DisplayName("Should successfully read monetary scale value balance details")
        void getCardBalance_Success() {
            GetCardBalanceRequest request = mock(GetCardBalanceRequest.class);
            when(request.getCardId()).thenReturn(globalCardId);
            when(request.getUserId()).thenReturn(userId);

            CardEntity card = CardEntity.builder().balance(new BigDecimal("1050.75")).build();
            when(cardRepository.findByUuidAndUserEntity_Uuid(globalCardId, userId)).thenReturn(Optional.of(card));

            GetCardBalanceResponse response = cardService.getCardBalance(request);
            assertNotNull(response);
            assertEquals(new BigDecimal("1050.75"), response.getBalance());
        }
    }

    @Nested
    @DisplayName("createCard method tests")
    class CreateCardTests {

        @Test
        @DisplayName("Should create and store unique bank card instance parameters")
        void createCard_Success() {
            CreateCardRequest request = mock(CreateCardRequest.class);
            when(request.getUserId()).thenReturn(userId);
            when(request.getHolderName()).thenReturn("john doe");
            when(request.getInitBalance()).thenReturn(new BigDecimal("100.00"));

            UserEntity user = UserEntity.builder().uuid(userId).build();
            when(userRepository.findByUuid(userId)).thenReturn(Optional.of(user));
            when(cardRepository.save(any(CardEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

            CardEntity result = cardService.createCard(request);
            assertNotNull(result);
            assertEquals("JOHN DOE", result.getHolderName());
            assertEquals(CardStatus.ACTIVE, result.getStatus());
            assertEquals(new BigDecimal("100.00"), result.getBalance());
            assertNotNull(result.getCardNumber());
            assertEquals(16, result.getCardNumber().length());
        }

        @Test
        @DisplayName("Should fail card generation with UserNotFoundException when owner is missing")
        void createCard_UserMissing_ThrowsUserNotFoundException() {
            CreateCardRequest request = mock(CreateCardRequest.class);
            when(request.getUserId()).thenReturn(userId);

            when(userRepository.findByUuid(userId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> cardService.createCard(request));
        }
    }

    @Nested
    @DisplayName("updateCardStatus method tests")
    class UpdateCardStatusTests {

        @Test
        @DisplayName("Should write change context into lifecycle status configurations")
        void updateCardStatus_Success() {
            UpdateCardStatusRequest request = mock(UpdateCardStatusRequest.class);
            when(request.getCardId()).thenReturn(globalCardId);
            when(request.getCardStatus()).thenReturn(CardStatus.BLOCKED);

            CardEntity card = CardEntity.builder().status(CardStatus.ACTIVE).build();
            when(cardRepository.findByUuid(globalCardId)).thenReturn(Optional.of(card));
            assertDoesNotThrow(() -> cardService.updateCardStatus(request));
            assertEquals(CardStatus.BLOCKED, card.getStatus());
            verify(cardRepository, times(1)).save(card);
        }
    }

    @Nested
    @DisplayName("deleteCard method tests")
    class DeleteCardTests {
        @Test
        @DisplayName("Should purge resource matching requested parameter specifications")
        void deleteCard_Success() {
            DeleteCardRequest request = mock(DeleteCardRequest.class);
            when(request.getCardId()).thenReturn(globalCardId);
            when(cardRepository.existsByUuid(globalCardId)).thenReturn(true);
            assertDoesNotThrow(() -> cardService.deleteCard(request));
            verify(cardRepository, times(1)).deleteByUuid(globalCardId);
        }

        @Test
        @DisplayName("Should intercept execution with CardNotFoundException if identifier is void")
        void deleteCard_NotFound_ThrowsCardNotFoundException() {
            DeleteCardRequest request = mock(DeleteCardRequest.class);
            when(request.getCardId()).thenReturn(globalCardId);
            when(cardRepository.existsByUuid(globalCardId)).thenReturn(false);
            assertThrows(CardNotFoundException.class, () -> cardService.deleteCard(request));
            verify(cardRepository, never()).deleteByUuid(any(UUID.class));
        }
    }

    @Nested
    @DisplayName("getAllCards execution slicing tests")
    class GetAllCardsTests {
        @Test
        @DisplayName("Should slice complete dataset elements with operational metadata parameters")
        void getAllCards_Admin_Success() {
            GetCardsAdminRequest request = mock(GetCardsAdminRequest.class);
            when(request.getUserId()).thenReturn(userId);
            when(request.getStatus()).thenReturn(null);
            UserEntity user = UserEntity.builder().uuid(userId).build();
            Page page = new PageImpl<>(List.of(new CardEntity()));
            Pageable pageable = mock(Pageable.class);
            when(userRepository.findByUuid(userId)).thenReturn(Optional.of(user));
            when(cardRepository.findAllByUserEntity_Uuid(userId, pageable)).thenReturn(page);
            Page result = cardService.getAllCards(request, pageable);
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }
    }
}