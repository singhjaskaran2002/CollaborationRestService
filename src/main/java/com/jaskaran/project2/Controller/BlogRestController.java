package com.jaskaran.project2.Controller;

import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jaskaran.project2.DAO.BlogDAO;
import com.jaskaran.project2.Domain.Blog;
import com.jaskaran.project2.Domain.BlogComment;

@RestController
public class BlogRestController 
{
	@Autowired
	private BlogDAO blogDAO;
	
	@Autowired
	HttpSession session;
		
	@GetMapping("/blog/list")
	public ResponseEntity<List<Blog>> blogList()
	{
		List<Blog> blogs = blogDAO.blogList();
		if(blogs.isEmpty())
		{
			return new ResponseEntity<List<Blog>>(blogs, HttpStatus.NOT_FOUND);
		}
		else
		{
			return new ResponseEntity<List<Blog>>(blogs, HttpStatus.OK);
		}
	}
	
	@RequestMapping("/blog/get/{blogid}")
	public ResponseEntity<Blog> getBlog(@PathVariable int blogid)
	{
		Blog blog = blogDAO.getBlog(blogid);
		
		if(blog == null)
		{
			return new ResponseEntity<Blog>(blog, HttpStatus.NOT_FOUND);
		}
		else
		{
			session.setAttribute("blogidforcomment", blogid);
			return new ResponseEntity<Blog>(blog, HttpStatus.OK);
		}
	}
	
	@PostMapping("/blog/add")
	public ResponseEntity<Blog> addBlog(@RequestBody Blog blog)
	{
		String loginname = (String) session.getAttribute("loginname");
		blog.setUser_created(loginname);
		if(blogDAO.saveBlog(blog))
		{
			blog.setStatusMessage("Blog added successfully");
			return new ResponseEntity<Blog>(blog, HttpStatus.OK);
		}
		else
		{
			blog.setStatusMessage("Error Occurred");
			return new ResponseEntity<Blog>(blog, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping("blog/delete/{blogid}")
	public ResponseEntity<Blog> deleteBlog(@PathVariable int blogid)
	{
		Blog blog = blogDAO.getBlog((blogid));
		if(blog == null)
		{
			blog = new Blog();
			blog.setStatusMessage("Blog not found");
			return new ResponseEntity<Blog>(blog, HttpStatus.NOT_FOUND);
		}
		
		if(blogDAO.deleteBlog(blogid))
		{
			blog.setStatusMessage("Blog deleted successully");
			return new ResponseEntity<Blog>(blog, HttpStatus.OK);
		}
		else
		{
			blog.setStatusMessage("Error Occurred");
			return new ResponseEntity<Blog>(blog, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping("/blog/approve/{blogid}")
	public ResponseEntity<Blog> approveBlog(@PathVariable int blogid)
	{
		if(blogDAO.approveBlog(blogid))
		{
			Blog blog = blogDAO.getBlog(blogid);
			blog.setStatusMessage("Approved");
			return new ResponseEntity<Blog>(blog, HttpStatus.OK);
		}
		else
		{
			Blog blog = blogDAO.getBlog(blogid);
			blog.setStatusMessage("Could not Approved, Error Occurred");
			return new ResponseEntity<Blog>(blog, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping("/blog/reject/{blogid}")
	public ResponseEntity<Blog> rejectBlog(@PathVariable int blogid)
	{
		if(blogDAO.rejectBlog(blogid))
		{
			Blog blog = blogDAO.getBlog(blogid);
			blog.setStatusMessage("Rejected");
			return new ResponseEntity<Blog>(blog, HttpStatus.OK);
		}
		else
		{
			Blog blog = blogDAO.getBlog(blogid);
			blog.setStatusMessage("Could not performed operation successfully");
			return new ResponseEntity<Blog>(blog, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping("blog/incLike/{blogid}")
	public ResponseEntity<Blog> incLike(@PathVariable int blogid)
	{
		if(blogDAO.incLikes(blogid))
		{
			Blog blog = blogDAO.getBlog(blogid);
			blog.setStatusMessage("Like incremented successfully");
			return new ResponseEntity<Blog>(blog, HttpStatus.OK);
		}
		else
		{
			Blog blog = blogDAO.getBlog(blogid);
			blog.setStatusMessage("Error Occurred");
			return new ResponseEntity<Blog>(blog, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	
	
	
	
/************************************************************** Related to Blog Comment **************************************************************/
	
	@RequestMapping("blog/listComments/{blogid}")
	public ResponseEntity<List<BlogComment>> listBlogComments(@PathVariable int blogid)
	{
		List<BlogComment> blogcomments = blogDAO.blogCommentList(blogid);
		if(blogcomments.isEmpty())
		{
			return new ResponseEntity<List<BlogComment>>(blogcomments, HttpStatus.NOT_FOUND);
		}
		else
		{
			return new ResponseEntity<List<BlogComment>>(blogcomments, HttpStatus.OK);
		}
	}
		
	@PostMapping("blog/comment")
	public ResponseEntity<BlogComment> commentBlog(@RequestBody BlogComment blogComment)
	{
		String loginname = (String) session.getAttribute("loginname");
		int blogid = (Integer)session.getAttribute("blogidforcomment");
		blogComment.setBlogid(blogid);
		blogComment.setLoginname(loginname);
		if(blogDAO.saveBlogComment(blogComment))
		{
			return new ResponseEntity<BlogComment>(blogComment, HttpStatus.OK);
		}
		else
		{
			return new ResponseEntity<BlogComment>(blogComment, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/blog/comment/delete/{blogcommentid}")
	public ResponseEntity<BlogComment> commentdelete(@PathVariable int blogcommentid)
	{
		if(blogDAO.deletecomment(blogcommentid))
		{
			BlogComment b = new BlogComment();
			return new ResponseEntity<BlogComment>(b, HttpStatus.OK);
		}
		else
		{
			BlogComment b = new BlogComment();
			b = blogDAO.getBlogComment(blogcommentid);
			return new ResponseEntity<BlogComment>(b, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	} 
	
}