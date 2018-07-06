package com.jaskaran.project2.Controller;

import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jaskaran.project2.DAO.JobDAO;
import com.jaskaran.project2.DAO.UserDAO;
import com.jaskaran.project2.Domain.JobApplication;
import com.jaskaran.project2.Domain.User;

@RestController
public class UserRestController 
{
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private JobDAO jobDAO;
	
	@Autowired
	private JobApplication jobApplication;
	
	@Autowired
	HttpSession session;
	
	@RequestMapping("/")
	public String testServer()
	{
		return "This is first web service";
	}
	
	@RequestMapping("/user/list")
	public ResponseEntity<List<User>> userList()
	{
		List<User> users = userDAO.userList();
		return new ResponseEntity<List<User>>(users, HttpStatus.OK);
	}
	
	@RequestMapping("/user/get/{loginname}")
	public ResponseEntity<User> getUser(@PathVariable String loginname)
	{
		User user = userDAO.getUser(loginname);
		if(user == null)
		{
			user = new User();
			user.setStatusMessage("No user exists with this loginname..");
			return new ResponseEntity<User>(user, HttpStatus.NOT_FOUND);
		}
		else
		{
			user.setStatusMessage("User Found...");
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}
	}
	
	@PostMapping("/user/validate")
	public ResponseEntity<User> validate(@RequestBody User user)
	{
		user = userDAO.validateUser(user.getEmail(), user.getPassword());
		if(user == null)
		{
			user = new User();
			user.setStatusMessage("Invalid Credentials, Please try again...");
			return new ResponseEntity<User>(user, HttpStatus.UNAUTHORIZED);
		}
		else
		{
			String useremail = user.getEmail();
			session.setAttribute("useremail", useremail);
			String loginname = user.getLoginname();
			session.setAttribute("loginname", loginname);
			user.setStatusMessage("You Successfully logged in..");
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}
	}
	
	@PostMapping("/user/register")
	public ResponseEntity<User> registerUser(@RequestBody User user)
	{
		if(userDAO.getUser(user.getEmail()) != null)
		{
			user.setStatusMessage("Email already exists.");
			return new ResponseEntity<User>(user , HttpStatus.CONFLICT);
		}
			
		if(userDAO.saveUser(user))
		{
			user.setStatusMessage("User saved Successfully.....");
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}
		else
		{
			user.setStatusMessage("Internal Server Error..");
			return new ResponseEntity<User>(user, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping("user/appliedJobs")
	public ResponseEntity<List<JobApplication>> appliedJobList()
	{
		String loginname = (String) session.getAttribute("loginname");
		
		List<JobApplication> userJobList = jobDAO.jobApplicationList(loginname);
		if(!userJobList.isEmpty())
		{
			jobApplication.setStatusMessage("User applied this job..");
			return new ResponseEntity<List<JobApplication>>(userJobList, HttpStatus.OK);
		}
		else
		{
			jobApplication.setStatusMessage("You have not applied for any job yet..");
			userJobList.add(jobApplication);
			return new ResponseEntity<List<JobApplication>>(userJobList, HttpStatus.NOT_FOUND);
		}
	}
}
