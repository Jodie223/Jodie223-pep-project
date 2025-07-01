package Service;

import DAO.AccountDAO;
import DAO.MessageDAO;
import Model.Account;
import Model.Message;
import java.util.*;

public class MessageService {

    private final MessageDAO messageDAO = new MessageDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    public Message createMessage(Message message) {
        if (message.getMessage_text() == null || message.getMessage_text().isBlank() || message.getMessage_text().length() > 255)
            return null;

        Account account = accountDAO.getAccountById(message.getPosted_by());
        if (account == null) return null;

        return messageDAO.createMessage(message);
    }

    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    public Message getMessageById(int message_id) {
        return messageDAO.getMessageById(message_id);
    }

    public Message updateMessage(int message_id, String newMessageText) {
        if (newMessageText == null || newMessageText.isBlank() || newMessageText.length() > 255) return null;

        Message existingMessage = messageDAO.getMessageById(message_id);
        if (existingMessage == null) return null;

        return messageDAO.updateMessageText(message_id, newMessageText);
    }

    public Message deleteMessage(int message_id) {
        Message toDelete = messageDAO.getMessageById(message_id);
        if (toDelete == null) return null;

        boolean deleted = messageDAO.deleteMessage(message_id);
        if (deleted) {
            return toDelete;
        }
        return null;
    }

    public List<Message> getMessagesByAccountId(int account_id) {
        Account account = accountDAO.getAccountById(account_id);
        if (account == null) return new ArrayList<>();

        return messageDAO.getMessagesByAccountId(account_id);
    }
}