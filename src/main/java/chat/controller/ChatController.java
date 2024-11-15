package paw.togaether.chat.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.log4j.Log4j;
import paw.togaether.chat.dto.ChattingRoom;
import paw.togaether.chat.service.ChatService;
import paw.togaether.common.domain.CommandMap;



@Log4j
@Controller
public class ChatController {

	@Resource(name = "chatService")
	private ChatService chatService;
	
	// Main screen
	@GetMapping("/chat")
	public ModelAndView chatMain(HttpSession session) throws Exception {
		ModelAndView mv = new ModelAndView("chat/chat");
		
		commandMap.put("mem_id", session.getAttribute("mem_id"));
		
		List<Map<String, Object>> chatMessageList = chatService.chatMessageList(commandMap.getMap());
		List<Map<String, Object>> memInfo = chatService.memInfo(commandMap.getMap());
		
		mv.addObject("chatMessageList", chatMessageList);
		mv.addObject("memInfo", memInfo);
		System.out.println();
		
		return mv;
	}

	
	// Chat room list
	@GetMapping("/chattingRoomList")
	public ResponseEntity<?> chattingRoomList() throws Exception {
		return new ResponseEntity<LinkedList<ChattingRoom>>(chattingRoomList, HttpStatus.OK);
	}


	// Create a chat room
	@PostMapping("/chattingRoom")
	public ResponseEntity<?> createChattingRoom(String roomName, String nickname, String roomNumber, HttpSession session) throws Exception {
		nickname = (String) session.getAttribute("mem_id");
		addCookie("nickname", nickname);
		addCookie("roomNumber", roomNumber);

		
		// Create a chat room and add it to the list
		ChattingRoom chattingRoom = ChattingRoom.builder()
				.roomNumber(roomNumber)
				.users(new LinkedList<>())
				.roomName(roomName)
				.build();
		
		commandMap.put("CR_IDX", chattingRoom.getRoomNumber());
		commandMap.put("CR_TITLE", chattingRoom.getRoomName());
		commandMap.put("CR_JOIN_PEOPLE", 0);
		commandMap.put("mem_id", session.getAttribute("mem_id"));

		System.out.println("************** chattingRoom 메소드 실행 **************");
		System.out.println("************** "+ commandMap.get("CR_IDX") +" **************");

		int result = chatService.chatRoomCheck(commandMap.getMap());

		if (result == 0) {
			chattingRoomList.add(chattingRoom);
			chatService.chattingRoom(commandMap.getMap());
		} 

		enterChattingRoom(chattingRoom, nickname, session);
		
		return new ResponseEntity<>(chattingRoom, HttpStatus.OK);
	}

	
	// Enter the chat room
	@GetMapping("/chattingRoom")
	public ResponseEntity<?> chattingRoom() throws Exception {
		/* If the cookie contains the nickname and room number, 
		   it indicates that there was an ongoing conversation in that room*/
		Map<String, String> map = findCookie();

		if (map == null) {
			return new ResponseEntity<>(HttpStatus.OK);
		}

		String roomNumber = map.get("roomNumber");
		String nickname = map.get("nickname");

		ChattingRoom chattingRoom = findRoom(roomNumber);

		if (chattingRoom == null) {
			deleteCookie();
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			Map<String, Object> map2 = new HashMap<>();
			List<String> users = chattingRoom.getUsers();
			
			if(!users.contains(nickname))
			users.add(users.size(), nickname);
			
			map2.put("chattingRoom", chattingRoom);
			map2.put("myNickname", nickname);

			return new ResponseEntity<>(map2, HttpStatus.OK);
		}
	}
	
	// Exit the chat room
	@PatchMapping("/chattingRoom-exit")
	public ResponseEntity<?> exitChattingRoom() throws Exception {

		Map<String, String> map = findCookie();

		if (map == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		String roomNumber = map.get("roomNumber");
		String nickname = map.get("nickname");

		//Retrieve the user list corresponding to the room number from the room list
		ChattingRoom chattingRoom = findRoom(roomNumber);
		List<String> users = chattingRoom.getUsers();

		// Delete the nickname
		users.remove(nickname);
		chatService.deleteChatWith(commandMap.getMap());

		// Remove the nickname and room number from the cookie
		deleteCookie();

		// Delete the room if the number of users is zero
		if (users.size() == 0) {
			chattingRoomList.remove(chattingRoom);
			
		}
		
		/* Check the remaining number of users in the chat room;
		   delete the room if there are zero users*/
		int result = chatService.chatWithCountCheck(commandMap.getMap());
		
		if (result <= 0) {
		chatService.deleteChatRoom(commandMap.getMap());
		}
		return new ResponseEntity<>(chattingRoom, HttpStatus.OK);
	}

	
	/* Functions required for control*/
	// Chat room list
	public static LinkedList<ChattingRoom> chattingRoomList = new LinkedList<>();
	
	static HttpServletRequest request = null;
	CommandMap commandMap = new CommandMap();

	// Utility methods

	// Find a room by its room number
	public ChattingRoom findRoom(String roomNumber) throws Exception {
		ChattingRoom room = ChattingRoom.builder().roomNumber(roomNumber).build();
		int index = chattingRoomList.indexOf(room);

		if (chattingRoomList.contains(room)) {
			return chattingRoomList.get(index);
		}
		return null;
	}

	// Add to the cookie
	public static void addCookie(String cookieName, String cookieValue) throws Exception {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletResponse response = attr.getResponse();

		Cookie cookie = new Cookie(cookieName, cookieValue);

		int maxage = 60 * 60 * 24 * 7;
		cookie.setMaxAge(maxage);
		response.addCookie(cookie);
	}

	// Delete room number and nickname from the cookie
	public void deleteCookie() throws Exception {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletResponse response = attr.getResponse();

		Cookie roomCookie = new Cookie("roomNumber", null);
		Cookie nicknameCookie = new Cookie("nickname", null);

		roomCookie.setMaxAge(0);
		nicknameCookie.setMaxAge(0);

		response.addCookie(nicknameCookie);
		response.addCookie(roomCookie);
	}

	// Retrieve room number and nickname from the cookie
	public Map<String, String> findCookie() throws Exception {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = attr.getRequest();

		Cookie[] cookies = request.getCookies();
		String roomNumber = "";
		String nickname = "";

		if (cookies == null) {
			return null;
		}

		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if ("roomNumber".equals(cookies[i].getName())) {
					roomNumber = cookies[i].getValue();
				}
				if ("nickname".equals(cookies[i].getName())) {
					nickname = cookies[i].getValue();
				}
			}

			if (!"".equals(roomNumber) && !"".equals(nickname)) {
				Map<String, String> map = new HashMap<>();
				map.put("nickname", nickname);
				map.put("roomNumber", roomNumber);

				return map;
			}
		}

		return null;
	}


	//Enter the chat room
	public boolean enterChattingRoom(ChattingRoom chattingRoom, String nickname, HttpSession session) throws Exception {
		nickname = (String) session.getAttribute("mem_id");
		addCookie("nickname", nickname);
		
		commandMap.put("CR_IDX", chattingRoom.getRoomNumber());
		commandMap.put("mem_id", nickname);
		

		int result = chatService.chatWithCheck(commandMap.getMap());
		System.out.println("************** enterChattingRoom 메소드 실행 **************");

		if (result == 0) {
			chatService.pulsPeople(commandMap.getMap());
		}
		
		addCookie("roomNumber", chattingRoom.getRoomNumber());
		session.setAttribute("roomNumber", chattingRoom.getRoomNumber());
		session.setAttribute("nickname", nickname);


		return true;
	}
}
