package com.jaskaran.project2.Controller;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import com.jaskaran.project2.DAO.ProfileDAO;
import com.jaskaran.project2.Domain.Profile;

@RestController
public class ProfileRestController 
{	
	@Autowired
	private ProfileDAO profileDAO;
	
	@Autowired
	HttpSession httpSession;
	
	@PostMapping("profile/ImageUpload")
	public ResponseEntity<?> upload(@RequestParam("file") CommonsMultipartFile file)
	{
		Profile profile = new Profile();
		String loginname = (String) httpSession.getAttribute("loginname");
		profile.setLoginname(loginname);
		profile.setProfilepicture(file.getBytes());
		
		if(profileDAO.uploadProfile(profile))
		{
			return new ResponseEntity<Void>(HttpStatus.OK); 
		}
		else
		{
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping("profile/getProfilePicture/{loginname}")
	public @ResponseBody byte[] getProfilePicture(@PathVariable String loginname)
	{
		loginname = (String) httpSession.getAttribute("loginname");
		Profile profile = profileDAO.getProfile(loginname);
		
		if(profile == null)
		{
			return null;
		}
		else
		{
			byte[] image = profile.getProfilepicture();
			return image;
		}
	}
	
	@RequestMapping("friends/profile/getProfilePicture/{loginname}")
	public @ResponseBody byte[] getFriendsProfilePicture(@PathVariable String loginname)
	{
		Profile profile = profileDAO.getProfile(loginname);
		
		if(profile == null)
		{
			return null;
		}
		else
		{
			byte[] image = profile.getProfilepicture();
			return image;
		}
	}
}
