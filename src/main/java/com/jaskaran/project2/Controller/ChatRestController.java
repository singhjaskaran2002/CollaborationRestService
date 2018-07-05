package com.jaskaran.project2.Controller;

import java.util.Date;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
import com.jaskaran.project2.Domain.Message;
import com.jaskaran.project2.Domain.OutputMessage;

@RestController
public class ChatRestController 
{
	@MessageMapping("/chat")
	@SendTo("/topic/message")
	public OutputMessage sendMessage(Message message)
	{
		return new OutputMessage(message,new Date());
	}
}
