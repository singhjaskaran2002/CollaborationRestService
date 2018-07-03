  package com.jaskaran.project2.Controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jaskaran.project2.DAO.FriendDAO;
import com.jaskaran.project2.DAO.UserDAO;
import com.jaskaran.project2.Domain.Friend;
import com.jaskaran.project2.Domain.User;

@RestController
public class FriendRestController 
{
	@Autowired
	HttpSession session;
	
	@Autowired
	private FriendDAO friendDAO;
	
	@Autowired
	private Friend friend;
	
	@Autowired
	private UserDAO userDAO;
	
	// http://localhost:8086/collaborationRestService/friend/list/{loginname}
	@RequestMapping("friend/list")
	public ResponseEntity<List<Friend>> showFriendList()
	{
		String loginname = (String) session.getAttribute("loginname");
		
		List<Friend> friends = friendDAO.friendList(loginname);
		
		if(friends.isEmpty())
		{
			Friend friend = new Friend();
			friend.setStatusMessage("No Friends Yet !!!");
			friends.add(friend);
			return new ResponseEntity<List<Friend>>(friends, HttpStatus.NOT_FOUND);
		}
		else
		{
			return new ResponseEntity<List<Friend>>(friends, HttpStatus.OK);
		}
	}
	
	@RequestMapping("friend/pendingRequest")
	public ResponseEntity<List<Friend>> PendingFriendRequest()
	{
		String loginname = (String) session.getAttribute("loginname");
				
		List<Friend> friends = friendDAO.pendingFriendRequestList(loginname);
		
		if(friends.isEmpty())
		{
			Friend friend = new Friend();
			friend.setStatusMessage("No Pending Friend Requests Yet !!!");
			friends.add(friend);
			return new ResponseEntity<List<Friend>>(friends, HttpStatus.NOT_FOUND);
		}
		else
		{
			return new ResponseEntity<List<Friend>>(friends, HttpStatus.OK);
		}
	}
	
	@RequestMapping("friend/suggested")
	public ResponseEntity<List<User>> suggestedPeople()						// @PathVariable String loginname
	{		
		String loginname = (String) session.getAttribute("loginname");
		
		List<User> suggestedPeople = friendDAO.suggestedPeopleList(loginname);
		
		if(suggestedPeople.isEmpty())
		{
			return new ResponseEntity<List<User>>(suggestedPeople, HttpStatus.NOT_FOUND);
		}
		else
		{
			return new ResponseEntity<List<User>>(suggestedPeople, HttpStatus.OK);
		}
	}
	
	@RequestMapping("friend/sendRequest/{username}")
	public ResponseEntity<Friend> sendRequest(@PathVariable String username)
	{
		String loginname = (String) session.getAttribute("loginname");
		User u = userDAO.getUserByName(username);
				
		friend.setLoginname(loginname);
		friend.setFriendname(u.getLoginname());
		
		if(friendDAO.sendFriendRequest(friend))
		{
			friend.setStatusMessage("Request Sent");
			return new ResponseEntity<Friend>(friend, HttpStatus.OK);
		}
		else
		{
			friend.setStatusMessage("Request not Sent");
			return new ResponseEntity<Friend>(friend, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("friend/acceptRequest/{friendid}")
	public ResponseEntity<Friend> acceptRequest(@PathVariable int friendid)
	{
		Friend friend = new Friend();
		if(friendDAO.acceptFriendRequest(friendid))
		{
			friend = friendDAO.getFriend(friendid);
			friend.setStatusMessage("Request Accepted");
			return new ResponseEntity<Friend>(friend, HttpStatus.OK);
		}
		else
		{
			friend = friendDAO.getFriend(friendid);
			friend.setStatusMessage("internal server error");
			return new ResponseEntity<Friend>(friend, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("friend/deleteRequest/{friendid}")
	public ResponseEntity<Friend> rejectRequest(@PathVariable int friendid)
	{
		Friend friend = new Friend();
		if(friendDAO.deleteFriendRequest(friendid))
		{
			return new ResponseEntity<Friend>(friend, HttpStatus.OK);
		}
		else
		{
			return new ResponseEntity<Friend>(friend, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("friend/delete/{friendid}")
	public ResponseEntity<Friend> DeleteFriend(@PathVariable int friendid)
	{
		Friend friend = new Friend();
		if(friendDAO.deleteFriendRequest(friendid))
		{
			friend = friendDAO.getFriend(friendid);
			friend.setStatusMessage("Friend Deleted");
			return new ResponseEntity<Friend>(friend, HttpStatus.OK);
		}
		else
		{
			friend = friendDAO.getFriend(friendid);
			friend.setStatusMessage("internal server error");
			return new ResponseEntity<Friend>(friend, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
