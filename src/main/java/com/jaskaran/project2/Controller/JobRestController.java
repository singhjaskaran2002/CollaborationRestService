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
import com.jaskaran.project2.DAO.JobDAO;
import com.jaskaran.project2.DAO.UserDAO;
import com.jaskaran.project2.Domain.Job;
import com.jaskaran.project2.Domain.JobApplication;
import com.jaskaran.project2.Domain.User;

@RestController()
public class JobRestController 
{
	@Autowired
	private Job job;
	
	@Autowired
	private JobDAO jobDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	HttpSession session;
	
	/************************ Related to JOB ********************/	
	
	@RequestMapping("/job/list")
	public ResponseEntity<List<Job>> jobList()
	{		
		List<Job> jobList = jobDAO.jobList(); 
		if(jobList.isEmpty())		
		{
			job.setStatusMessage("no jobs are available");
			jobList.add(job);
			return new ResponseEntity<List<Job>>(jobList, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<List<Job>>(jobList, HttpStatus.OK);
	}
	
	@RequestMapping("/job/get/{jobid}")
	public ResponseEntity<Job> getJob(@PathVariable int jobid)
	{
		Job job = jobDAO.getJob(jobid);
		if(job == null)				
		{
			job = new Job();
			job.setStatusMessage("No job is found with JobID: "+jobid);
			return new ResponseEntity<Job>(job, HttpStatus.NOT_FOUND);
		}
		else
		{
			session.setAttribute("jobidforapply", jobid);
			job.setStatusMessage("This is the particular Job Details of JobID: "+jobid);
			return new ResponseEntity<Job>(job, HttpStatus.OK);
		}
	}
	
	@PostMapping("job/post")
	public ResponseEntity<Job> saveJob(@RequestBody Job job)
	{		
		if(jobDAO.saveJob(job))
		{
			job.setStatusMessage("Job Posted Successfully");
			return new ResponseEntity<Job>(job, HttpStatus.OK);
		}
		else
		{
			job.setStatusMessage("Cannot post Job right now, Please try again later..");
			return new ResponseEntity<Job>(job, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@DeleteMapping("/job/delete/{jobid}")
	public ResponseEntity<Job> deleteJob(@PathVariable int jobid)
	{
		Job j = jobDAO.getJob(jobid);
		if(j == null)					// for deleting job there must job with given jobid exists
		{
			job = new Job();
			job.setStatusMessage("Job doesn't Exists with JobID: "+jobid);
			return new ResponseEntity<Job>(job, HttpStatus.CONFLICT);
		}
		
		List<JobApplication> appliedJobs = jobDAO.jobApplicationlist(jobid);
		if(!appliedJobs.isEmpty())		// checking if any user has applied for this job then we cannot delete the particular job
		{
			job = new Job();
			job.setStatusMessage("Users have applied for this job, we cannot delete this job..");
			return new ResponseEntity<Job>(job, HttpStatus.CONFLICT);
		}
		
		if(jobDAO.deleteJob(jobid))
		{
			job.setStatusMessage("Job Deleted Successfully with Jobid: "+jobid);
			return new ResponseEntity<Job>(job, HttpStatus.OK);
		}
		else
		{
			job = new Job();
			job.setStatusMessage("Cannot delete Job with Jobid: "+jobid+", please try again later..");
			return new ResponseEntity<Job>(job, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/job/update/{jobid}")
	public ResponseEntity<Job> updateJob(@PathVariable int jobid)
	{
		job = jobDAO.getJob(jobid);
			
		job.setJobstatus('C');
		if(jobDAO.updateJob(job))
		{
			job.setStatusMessage("job updated succcessfully");
			return new ResponseEntity<Job>(job, HttpStatus.OK);
		}
		else
		{
			job.setStatusMessage("job not updated due to internal server error, please try again after some time");
			return new ResponseEntity<Job>(job, HttpStatus.INTERNAL_SERVER_ERROR);
		}				
	}
	
	
/****************************** Related to JOB APPLICATION *************************/
	
	@PostMapping("/job/registration")
	public ResponseEntity<JobApplication> jobRegistration(@RequestBody JobApplication jobApplication)
	{
		String loginname = (String) session.getAttribute("loginname");
		User user = userDAO.getUser(loginname);
		int jobid = (Integer)session.getAttribute("jobidforapply");
		Job job = jobDAO.getJob(jobid);
		
		jobApplication.setLoginname(loginname);
		jobApplication.setEmail(user.getEmail());
		jobApplication.setJobdescription(job.getJobdescription());
		jobApplication.setJobid(jobid);
		jobApplication.setJobtitle(job.getJobtitle());
		
		if(!jobDAO.isJobAlreadyApplied(loginname, jobid))
		{
			if(jobDAO.saveJobApplication(jobApplication))
			{
				jobApplication.setStatusMessage("Registered Successfully..");
				return new ResponseEntity<JobApplication>(jobApplication, HttpStatus.OK);
			}
			else
			{
				jobApplication.setStatusMessage("Cannot Register right now, please try again later..");
				return new ResponseEntity<JobApplication>(jobApplication, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
		}
		else
		{
			JobApplication j = new JobApplication();
			j.setStatusMessage("Already Registered");
			return new ResponseEntity<JobApplication>(j, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping("/job/approveApplication/{jobappid}")
	public ResponseEntity<JobApplication> approveApplication(@PathVariable int jobappid)
	{
		if(jobDAO.approveApplication(jobappid))
		{
			JobApplication jobApplication = new JobApplication();
			jobApplication.setStatusMessage("Approved");
			return new ResponseEntity<JobApplication>(jobApplication, HttpStatus.ACCEPTED);
		}
		else
		{
			JobApplication jobApplication = new JobApplication();
			jobApplication.setStatusMessage("Internal server error");
			return new ResponseEntity<JobApplication>(jobApplication, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping("/job/rejectApplication/{jobappid}")
	public ResponseEntity<JobApplication> rejectApplication(@PathVariable int jobappid)
	{
		JobApplication jobApplication = new JobApplication();
		if(jobDAO.rejectApplication(jobappid))
		{
			jobApplication = jobDAO.getApplication(jobappid);
			jobApplication.setStatusMessage("Rejected");
			return new ResponseEntity<JobApplication>(jobApplication, HttpStatus.ACCEPTED);
		}
		else
		{
			jobApplication = jobDAO.getApplication(jobappid);
			jobApplication.setStatusMessage("Internal server error");
			return new ResponseEntity<JobApplication>(jobApplication, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping("job/appliedJobs")
	public ResponseEntity<List<JobApplication>> appliedJobList()
	{
		List<JobApplication> jobapplist = jobDAO.jobApplications();
		
		if(!jobapplist.isEmpty())
		{
			return new ResponseEntity<List<JobApplication>>(jobapplist, HttpStatus.OK);
		}
		else
		{
			return new ResponseEntity<List<JobApplication>>(jobapplist, HttpStatus.NOT_FOUND);
		}
	}
	
	@DeleteMapping("/application/delete/{jobappid}")
	public ResponseEntity<?> deleteapp(@PathVariable int jobappid)
	{
		if(jobDAO.deletejobapp(jobappid))
		{
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		else
		{
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}