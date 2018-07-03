package com.jaskaran.project2.Controller;

import java.util.List;

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

import com.jaskaran.project2.DAO.JobDAO;
import com.jaskaran.project2.DAO.UserDAO;
import com.jaskaran.project2.Domain.JobApplication;
import com.jaskaran.project2.Domain.User;

@RestController
public class UserController 
{
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private User user;
	
	@Autowired
	private JobDAO jobDAO;
	
	@Autowired
	private JobApplication jobApplication;
	
//	http://localhost:8080/collaborationRestService/
	@RequestMapping("/")
	public String testServer()
	{
		return "This is first web service";
	}
	
	
//	http://localhost:8080/collaborationRestService/userList
	@RequestMapping("/userList")
	public ResponseEntity<List<User>> userList()
	{
		List<User> users = userDAO.userList();
		return new ResponseEntity<List<User>>(users, HttpStatus.OK);
	}
	
	
//	http://localhost:8080/collaborationRestService/getUser/{email}
	@RequestMapping("/getUser/{email}")
	public ResponseEntity<User> getUser(@PathVariable String email)
	{
		User user = userDAO.getUser(email);
		if(user == null)
		{
			user = new User();
			user.setStatusMessage("No user exists with this email..");
			return new ResponseEntity<User>(user, HttpStatus.NOT_FOUND);
		}
		else
		{
			user.setStatusMessage("User Found...");
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}
	}
	
	
//	http://localhost:8080/collaborationRestService/validate/{email}/{password}
	@PostMapping("/validate/{email}/{password}")
	public ResponseEntity<User> validate(@PathVariable String email, @PathVariable String password)
	{
		user = userDAO.validateUser(email, password);
		if(user == null)
		{
			user = new User();
			user.setStatusMessage("Invalid Credentials, Please try again...");
			return new ResponseEntity<User>(user, HttpStatus.UNAUTHORIZED);
		}
		else
		{
			user.setStatusMessage("You Successfully logged in..");
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}
	}
	
	
//	http://localhost:8080/collaborationRestService/registerUser
	@PostMapping("/registerUser")
	public ResponseEntity<User> registerUser(@RequestBody User user)
	{
		if(userDAO.getUser(user.getEmail()) != null)
		{
			user.setStatusMessage("Email already exists.");
			return new ResponseEntity<User>(user, HttpStatus.CONFLICT);
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
	
	
//	http://localhost:8080/collaborationRestService/deleteUser
	@DeleteMapping("/deleteUser/{email}")
	public ResponseEntity<User> deleteUser(@PathVariable String email)
	{
		if(userDAO.getUser(email) == null)
		{
			User user = new User();
			user.setStatusMessage("No user exists with this email..");
			return new ResponseEntity<User>(user, HttpStatus.NOT_FOUND);
		}
		 
		if(jobDAO.jobApplicationList(email).size() != 0)
		{
			user.setStatusMessage("Couldn't Deleted User as this User Has Applied for a Job..");
			return new ResponseEntity<User>(user, HttpStatus.CONFLICT);
		}
		
		if(userDAO.deleteUser(email))
		{
			user.setStatusMessage("User Deleted Successfully");
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}
		else
		{
			user.setStatusMessage("Cannot Delete User right now, please try again after some time..");
			return new ResponseEntity<User>(user, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
//	http://localhost:8080/collaborationRestService/updateUser
	@PutMapping("/updateUser")
	public ResponseEntity<User> updateUser(@RequestBody User user)
	{
		if(userDAO.getUser(user.getEmail()) == null)
		{
			user.setStatusMessage("No user exists with this email..");
			return new ResponseEntity<User>(user, HttpStatus.NOT_FOUND);
		}
				
		if(userDAO.updateUser(user))
		{
			
			user.setStatusMessage("User updated Successfully..");
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}
		else
		{
			user.setStatusMessage("User Not updated Successfully..");
			return new ResponseEntity<User>(user, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
//	http://localhost:8080/collaborationRestService/userJobList
	@RequestMapping("/userJobList/{email}")
	public ResponseEntity<List<JobApplication>> appliedJobList(@PathVariable String email)
	{
		List<JobApplication> userJobList = jobDAO.jobApplicationList(email);
		if(userJobList.isEmpty())
		{
			 jobApplication.setStatusMessage("You have not applied for any job yet..");
			 userJobList.add(jobApplication);
			 return new ResponseEntity<List<JobApplication>>(userJobList, HttpStatus.NOT_FOUND);
		}
		else
		{
			 //jobApplication.setStatusMessage("User applied this job..");
			 return new ResponseEntity<List<JobApplication>>(userJobList, HttpStatus.OK);
		}
	}
}
