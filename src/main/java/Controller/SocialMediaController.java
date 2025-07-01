package Controller;

import Model.Account;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import Model.Message;
import java.util.*;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {

    private final AccountService accountService = new AccountService();
    private final MessageService messageService = new MessageService();
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();

        //app.get("example-endpoint", this::exampleHandler);
        app.post("/register", this::handleRegister);
        app.post("/login", this::handleLogin);
        app.post("/messages", this::handleCreateMessage);
        app.get("/messages", this::handleGetAllMessages);
        app.get("/messages/{message_id}", this::handleGetMessageById);
        app.delete("/messages/{message_id}", this::handleDeleteMessage);
        app.patch("/messages/{message_id}", this::handleUpdateMessage);
        app.get("/accounts/{account_id}/messages", this::handleGetMessagesByUser);

        return app;
    }
    
    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     
    
    
    private void exampleHandler(Context context) {
        context.json("sample text");
    }  */

    private void handleRegister(Context ctx) {
        try{
            Account account = ctx.bodyAsClass(Account.class);

            if (account.getUsername() == null || account.getUsername().isBlank() ||
                account.getPassword() == null || account.getPassword().length() < 4){
                ctx.status(400).result("");
                return;
            }

            Account existing = accountService.getAccountByUsername(account.getUsername());
            if (existing != null) {
                ctx.status(400).result("");
                return;
            }

            Account registered = accountService.registerAccount(account);
            if (registered != null) {
                ctx.status(200).json(registered);
            } else {
                ctx.status(400).result("");
            }

        } catch (Exception e) {
            ctx.status(400).result(""); 
        }

    }

    private void handleLogin(Context ctx) {
        try {
            Account loginRequest = ctx.bodyAsClass(Account.class);

            Account found = accountService.getAccountByUsername(loginRequest.getUsername());
            if (found == null || !found.getPassword().equals(loginRequest.getPassword())) {
                ctx.status(401).result("");
                return;
            }

            ctx.status(200).json(found);

        } catch (Exception e) {
            ctx.status(400).result("");
        }
    }

    private void handleCreateMessage(Context ctx) {
        Message message = ctx.bodyAsClass(Message.class);

        if (message.getPosted_by() <= 0 ||
            message.getMessage_text() == null || message.getMessage_text().isBlank() ||
            message.getMessage_text().length() > 255) {
            ctx.status(400).result("");
            return;
        }

        if (accountService.getAccountById(message.getPosted_by()) == null){
            ctx.status(400).result("");
        }

        Message created = messageService.createMessage(message);
        if (created == null) {
            ctx.status(400).result("");
            return;
        }

        ctx.json(created);
    }

    private void handleGetAllMessages(Context ctx) {
        List<Message> messages = messageService.getAllMessages();
        ctx.json(messages);
    }

    private void handleGetMessageById(Context ctx) {
        try {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            Message message = messageService.getMessageById(messageId);

            if (message == null) {
                ctx.status(200).result("");
            } else {
                ctx.status(200).json(message);
            }
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid message id");
        }
    }

    private void handleDeleteMessage(Context ctx) {
        try {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            Message deletedMessage = messageService.deleteMessage(messageId);

            if (deletedMessage == null) {
                ctx.status(200).result("");
                return;
            }

            ctx.status(200).json(deletedMessage);

        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid message id");
        }
    }

    private void handleUpdateMessage(Context ctx) {
        try {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            Message updateData = ctx.bodyAsClass(Message.class);

            if (updateData.getMessage_text() == null || updateData.getMessage_text().isBlank() ||
                updateData.getMessage_text().length() > 255) {
                ctx.status(400).result("");
                return;
            }

            Message updated = messageService.updateMessage(messageId, updateData.getMessage_text());

            if (updated == null) {
                ctx.status(400).result("");
                return;
            }

            ctx.status(200).json(updated);
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid message id");
        }
    }

    private void handleGetMessagesByUser(Context ctx) {
        try {
            int accountId = Integer.parseInt(ctx.pathParam("account_id"));
            List<Message> messages = messageService.getMessagesByAccountId(accountId);
            if (messages == null) {
                messages = new ArrayList<>();
            }
            ctx.status(200).json(messages);
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid account id");
        }
    }
}



