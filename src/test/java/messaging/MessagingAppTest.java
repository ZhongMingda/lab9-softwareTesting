package messaging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class MessagingAppTest {

    private static final String MESSAGE = "Hello world!";

    private static final String ENCRYPTED_MESSAGE = "askdjalsjdalksjldj";

    private static final String TO = "Alice";

    private static final String FROM = "BOB";

    private static final String ATTACKER = "Mr.Hacker";

    private MessagingApp underTest;

    private EncryptionService encryptionService;


    @BeforeEach
    void setUp() {
        encryptionService = Mockito.mock(EncryptionService.class);
        underTest = new MessagingApp(encryptionService);

    }

    @Test
    void testSendMessageShouldEncryptMessageWhenSendingToAuthenticationUser() {
        Mockito.when(encryptionService.authenticateUser(TO)).thenReturn(true);
        Mockito.when(encryptionService.encrypt(MESSAGE)).thenReturn(ENCRYPTED_MESSAGE);
        // When
        underTest.sendMessage(TO, MESSAGE);
        // Then
        Mockito.verify(encryptionService).authenticateUser(TO);
        Mockito.verify(encryptionService).encrypt(MESSAGE);
        Mockito.verifyNoMoreInteractions(encryptionService);
    }

    @Test
    void testReceiveMessageShouldDecryptTextWhenReceivingFromAuthenticateUser() {
        Mockito.when(encryptionService.authenticateUser(FROM)).thenReturn(true);
        Mockito.when(encryptionService.decrypt(ENCRYPTED_MESSAGE)).thenReturn(MESSAGE);
        // When
        underTest.receiveMessage(FROM, ENCRYPTED_MESSAGE);
        // Then
        Mockito.verify(encryptionService).authenticateUser(FROM);
        Mockito.verify(encryptionService).decrypt(ENCRYPTED_MESSAGE);
        Mockito.verifyNoMoreInteractions(encryptionService);
    }

    @Test
    void testSendMessageShouldThrowIllegalArgumentExceptionWhenWhenSendingToUnauthenticationUser() {
        Mockito.when(encryptionService.authenticateUser(ATTACKER)).thenReturn(false);
        Mockito.when(encryptionService.encrypt(MESSAGE)).thenReturn(ENCRYPTED_MESSAGE);
        try {
            underTest.sendMessage(ATTACKER, MESSAGE);
        } catch (IllegalArgumentException ex) {
            assertEquals(ATTACKER, ex.getMessage());
        } catch (Exception e) {
            fail();
        }
        Mockito.verify(encryptionService).authenticateUser(ATTACKER);
        Mockito.verifyNoMoreInteractions(encryptionService);
    }

    @Test
    void testReceiveMessageShouldThrowIllegalArgumentExceptionWhenReceivingFromUnauthenticatedUser() {
        Mockito.when(encryptionService.authenticateUser(ATTACKER)).thenReturn(false);
        Mockito.when(encryptionService.decrypt(ENCRYPTED_MESSAGE)).thenReturn(MESSAGE);
        try {
            underTest.receiveMessage(ATTACKER, MESSAGE);
        } catch (IllegalArgumentException ex) {
            assertEquals(ATTACKER, ex.getMessage());
        } catch (Exception e) {
            fail();
        }
        Mockito.verify(encryptionService).authenticateUser(ATTACKER);
        Mockito.verifyNoMoreInteractions(encryptionService);
    }
}