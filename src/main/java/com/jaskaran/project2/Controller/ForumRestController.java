package com.jaskaran.project2.Controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jaskaran.project2.DAO.ForumDAO;
import com.jaskaran.project2.Domain.Blog;
import com.jaskaran.project2.Domain.Forum;

@RestController
public class ForumRestController 
{
	@Autowired
	private ForumDAO forumDAO;
	
	@Autowired
	HttpSession session;
	
	@RequestMapping("/forum/list")
	public ResponseEntity<List<Forum>> getForumList()
	{
		List<Forum> forumList = forumDAO.forumList();
		if(!forumList.isEmpty())
		{
			return new ResponseEntity<List<Forum>>(forumList, HttpStatus.OK);
		}
		else
		{
			return new ResponseEntity<List<Forum>>(forumList, HttpStatus.NOT_FOUND);
		}
	}
	
	@RequestMapping("/forum/approvedList")
	public ResponseEntity<List<Forum>> getApprovedForumList()
	{
		List<Forum> forumList = forumDAO.approvedForumsList();
		if(!forumList.isEmpty())
		{
			return new ResponseEntity<List<Forum>>(forumList, HttpStatus.OK);
		}
		else
		{
			return new ResponseEntity<List<Forum>>(forumList, HttpStatus.NOT_FOUND);
		}
	}
	
//	http://localhost:8086/collaborationRestService/forum/add
	@PostMapping("/forum/add")
	public ResponseEntity<Forum> saveForum(@RequestBody Forum forum)
	{
		String loginname = (String) session.getAttribute("loginname");
		String useremail = (String) session.getAttribute("useremail");
		forum.setEmail(useremail);
		forum.setLoginname(loginname);
		if(forumDAO.saveForum(forum))
		{
			forum.setStatusMessage("Forum Added Successfully");
			return new ResponseEntity<Forum>(forum, HttpStatus.OK);
		}
		else
		{
			forum.setStatusMessage("Error Occurred");
			return new ResponseEntity<Forum>(forum, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/forum/delete/{forumid}")
	public ResponseEntity<Forum> deleteJob(@PathVariable int forumid)
	{
		Forum forum = forumDAO.getForum(forumid);
		if(forum == null)
		{
			forum = new Forum();
			forum.setStatusMessage("Forum not found");
			return new ResponseEntity<Forum>(forum, HttpStatus.NOT_FOUND);
		}
		
		if(forumDAO.deleteforum(forumid))
		{
			forum.setStatusMessage("Forum Deleted Successfully");
			return new ResponseEntity<Forum>(forum, HttpStatus.OK);
		}
		else
		{
			forum.setStatusMessage("Could not Deleted Forum Successfully");
			return new ResponseEntity<Forum>(forum, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping("/forum/get/{forumid}")
	public ResponseEntity<Forum> getForum(@PathVariable int forumid)
	{
		Forum forum = forumDAO.getForum(forumid);
		if(forum == null)
		{
			return new ResponseEntity<Forum>(forum, HttpStatus.NOT_FOUND);
		}
		else
		{
			return new ResponseEntity<Forum>(forum, HttpStatus.OK);
		}
	}
	
	@PutMapping("/forum/approve/{forumid}")
	public ResponseEntity<Forum> approveForum(@PathVariable int forumid)
	{
		if(forumDAO.approveForum(forumid))
		{
			Forum forum = forumDAO.getForum(forumid);
			forum.setStatusMessage("Approved Forum Successfully");
			return new ResponseEntity<Forum>(forum, HttpStatus.OK);
		}
		else
		{
			Forum forum = forumDAO.getForum(forumid);
			forum.setStatusMessage("Error Occurred");
			return new ResponseEntity<Forum>(forum, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/forum/reject/{forumid}")
	public ResponseEntity<Forum> rejectForum(@PathVariable int forumid)
	{
		if(forumDAO.rejectForum(forumid))
		{
			Forum forum = forumDAO.getForum(forumid);
			forum.setStatusMessage("Rejected Forum Successfully");
			return new ResponseEntity<Forum>(forum, HttpStatus.OK);
		}
		else
		{
			Forum forum = forumDAO.getForum(forumid);
			forum.setStatusMessage("Error Occurred");
			return new ResponseEntity<Forum>(forum, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping("forum/incLike/{forumid}")
	public ResponseEntity<Forum> incLike(@PathVariable int forumid)
	{
		if(forumDAO.incLikes(forumid))
		{
			Forum forum = forumDAO.getForum(forumid);
			forum.setStatusMessage("Like Incremented");
			return new ResponseEntity<Forum>(forum, HttpStatus.OK);
		}
		else
		{
			Forum forum = forumDAO.getForum(forumid);
			forum.setStatusMessage("Error Occurred in Incrementing Likes");
			return new ResponseEntity<Forum>(forum, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
