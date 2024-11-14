package paw.togaether.chat.service;

import java.util.List;
import java.util.Map;

public interface ChatService {

	// Create a chat room
	void chattingRoom(Map<String, Object> map) throws Exception;

	// Delete the chat room
	void deleteChatRoom(Map<String, Object> map) throws Exception;

	/* Increase the number of participants with entering the chat room
	   and add the participant's member ID*/
	void pulsPeople(Map<String, Object> map) throws Exception;

	// Check for duplicate chat rooms
	int chatRoomCheck(Map<String, Object> map) throws Exception;

	// Check for duplicate participants in the chat room
	int chatWithCheck(Map<String, Object> map) throws Exception;

	// Add CHAT_WITH when entering the chat room
	void puls_CHAT_WITH(Map<String, Object> map) throws Exception;

	// Save the chat message
	void insertMessage(Map<String, Object> map) throws Exception;

	// CHAT_WITH list
	List<Map<String, Object>> chatWihtList(Map<String, Object> map) throws Exception;

	// MESSAGE list
	List<Map<String, Object>> chatMessageList(Map<String, Object> map) throws Exception;

	// Remove a participant from the chat room
	void deleteChatWith(Map<String, Object> map) throws Exception;

	// Retrieve the list of participants in the chat room
	int chatWithCountCheck(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> memInfo(Map<String, Object> map) throws Exception;

}
