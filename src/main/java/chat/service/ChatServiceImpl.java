package paw.togaether.chat.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import paw.togaether.chat.dao.ChatDAO;

@Service("chatService")
public class ChatServiceImpl implements ChatService {
	
	@Resource(name = "chatDAO")
	private ChatDAO chatDAO;

	// Create a chat room
	@Override
	public void chattingRoom(Map<String, Object> map) throws Exception {
		chatDAO.chattingRoom(map);
		
	}
	
	// Check for duplicate chat rooms
	@Override
	public int chatRoomCheck(Map<String, Object> map) throws Exception {
		int result = chatDAO.chatRoomCheck(map);
		return result;
	}

	/* Increase the participant count upon entering the chat room and
	   add the member ID of the participant*/
	@Override
	public void pulsPeople(Map<String, Object> map) throws Exception {
		chatDAO.puls_CR_PEOPLE(map);
		chatDAO.puls_CHAT_WITH(map);
	}

	// Add CHAT_WITH when entering the chat room
	@Override
	public void puls_CHAT_WITH(Map<String, Object> map) throws Exception {
		chatDAO.puls_CHAT_WITH(map);
	}

	// Check for duplicate participants in the chat room
	@Override
	public int chatWithCheck(Map<String, Object> map) throws Exception {
		int result = chatDAO.chatWithCheck(map);
		return result;
	}

	// Save the chat message
	@Override
	public void insertMessage(Map<String, Object> map) throws Exception {
		chatDAO.insertMessage(map);
	}

	// CHAT_WITH list
	@Override
	public List<Map<String, Object>> chatWihtList(Map<String, Object> map) throws Exception {
		return chatDAO.chatWihtList(map);
	}

	// MESSAGE list
	@Override
	public List<Map<String, Object>> chatMessageList(Map<String, Object> map) throws Exception {
		return chatDAO.chatMessageList(map);
	}

	// Remove a participant from the chat room
	@Override
	public void deleteChatWith(Map<String, Object> map) throws Exception {
		chatDAO.deleteChatWith(map);
		chatDAO.minus_CR_PEOPLE(map);
	}

	// Retrieve the list of participants in the chat room
	@Override
	public int chatWithCountCheck(Map<String, Object> map) throws Exception {
		int result = chatDAO.chatWithCountCheck(map);
		return result;
	}
	
	// Delete the chat room
	@Override
	public void deleteChatRoom(Map<String, Object> map) throws Exception {
		chatDAO.cleanChatWith(map);
		chatDAO.deleteChatRoom(map);
	}

	@Override
	public List<Map<String, Object>> memInfo(Map<String, Object> map) throws Exception {
		return chatDAO.memInfo(map);
	}

}
