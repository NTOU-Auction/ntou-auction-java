package ntou.auction.spring.chat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MessageNotFound extends RuntimeException {

    public MessageNotFound(String errorMessage){
        super(errorMessage);
    }
}
