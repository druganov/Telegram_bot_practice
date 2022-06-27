import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import javax.validation.constraints.Null;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    static Boolean Iscity;
    public static void main(String[] args) {
        ApiContextInitializer.init();
        Iscity = false;
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());

        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }


    public boolean sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        sendMessage.setChatId(message.getChatId().toString());

        sendMessage.setReplyToMessageId(message.getMessageId());

        sendMessage.setText(text);
        try {

            setButtons(sendMessage);
            sendMessage(sendMessage);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return false;
    }



    public void onUpdateReceived(Update update) {
        Model model = new Model();
        Message message = update.getMessage();
        if (Iscity) {
            try {
                sendMsg(message, Weather.getWeather(message.getText(), model));
                execute(sendInlineKeyBoardMessage(Long.valueOf(update.getMessage().getChatId())));
            } catch (IOException e) {
                sendMsg(message, "Город не найден");
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
        else if(update.hasCallbackQuery()){
            try {
                execute(new SendMessage().setText(
                                update.getCallbackQuery().getData())
                        .setChatId(Long.valueOf(update.getCallbackQuery().getMessage().getChatId())));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else if  (message != null && message.hasText()) {
            switch (message.getText()) {
                case "Найти город":
                {    sendMsg(message, "Напиши название города");
                     Iscity=true;
                }
                    break;
                case "Подписки":
                    sendMsg(message, "Ваши подписки");
                    break;
                default:
                    sendMsg(message, "Команда не выбрана!");

            }
        }

    }



    public void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("Найти город"));
        keyboardFirstRow.add(new KeyboardButton("Ваши подписки"));

        keyboardRowList.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);


    }

    public String getBotUsername() {
        return "Weather1For1Bot";
    }

    public String getBotToken() {
        return "5419963128:AAEiHkSfWNKQ4Lbm6fyZwRVqWogaTyGaSgQ";
    }




    public static SendMessage sendInlineKeyBoardMessage(Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Подписаться");
        inlineKeyboardButton1.setCallbackData("Scribe");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return new SendMessage().setChatId(chatId).setText("Подпишитесь, чтобы получать прогноз погоды для этого города").setReplyMarkup(inlineKeyboardMarkup);
    }



}


