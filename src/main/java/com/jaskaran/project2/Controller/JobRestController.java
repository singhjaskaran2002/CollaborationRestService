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
import org.springframework.web.bind.annotation.RequestParam;
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
	
	/***************************************************** Related to JOB *********************************************************/	
	
	
//	http://localhost:8086/collaborationRestService/job/list
	@RequestMapping("/job/list")
	public ResponseEntity<List<Job>> jobList()
	{		
		List<Job> jobList = jobDAO.jobList(); 
		if(jobList.isEmpty())							// if no jobs are available then it will send appropriate message 
		{
			job.setStatusMessage("no jobs are available");
			jobList.add(job);
			return new ResponseEntity<List<Job>>(jobList, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<List<Job>>(jobList, HttpStatus.OK);
	}
	
	
	
//	http://localhost:8086/collaborationRestService/job/get/{jobid}
	@RequestMapping("/job/get/{jobid}")
	public ResponseEntity<Job> getJob(@PathVariable int jobid)
	{
		Job job = jobDAO.getJob(jobid);
		if(job == null)						// check whether the job with this id is exist or not
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
	
	
	
//	http://localhost:8086/collaborationRestService/job/getOpenedJobList/{jobstatus}	
	@RequestMapping("/job/getOpenedJobList/{jobstatus}")									// getting the opened job list to register for the jobs
	public ResponseEntity<List<Job>> getOpenedJobList(@PathVariable char jobstatus)
	{
		List<Job> jobs = jobDAO.jobList(jobstatus);
		if(jobs.isEmpty())
		{
			jobs.add(job);
			job.setStatusMessage("no jobs are open");
			return new ResponseEntity<List<Job>>(jobs, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<List<Job>>(jobs, HttpStatus.OK);
	}
	
	
	
//	http://localhost:8086/collaborationRestService/job/post
	@PostMapping("job/post")
	public ResponseEntity<Job> saveJob(@RequestBody Job job)
	{
		/*Job job = new Job();
		Job j = jobDAO.getJob(jobid);
		if(j != null)	// if the job already exists with the same jobid
		{
			//job = new Job();
			job.setStatusMessage("Job already exist with Jobid: "+job.getJobid());
			return new ResponseEntity<Job>(job, HttpStatus.CONFLICT);
		}*/
		
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
	
	
	
//	http://localhost:8086/collaborationRestService/job/delete/{jobid}	
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
	
	
//	http://localhost:8086/collaborationRestService/job/update
	@PutMapping("/job/update")
	public ResponseEntity<Job> updateJob(@RequestBody Job job)
	{
		if(jobDAO.updateJob(job))
		{
			job.setStatusMessage("Job Updated Successfully with Jobid: "+job.getJobid());
			return new ResponseEntity<Job>(job, HttpStatus.OK);
		}
		else
		{
			job = new Job();
			job.setStatusMessage("Cannot update Job right now with Jobid: "+job.getJobid()+", please try again later..");
			return new ResponseEntity<Job>(job, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
//	http://localhost:8086/collaborationRestService/job/update/{email}/{jobstatus}
	@PutMapping("/job/update/{jobid}/{jobstatus}")
	public ResponseEntity<Job> updateJob(@PathVariable int jobid, @PathVariable char jobstatus)
	{
		job = jobDAO.getJob(jobid);
		if(job == null)
		{	//if job does not exist with this jobid
			job.setStatusMessage("Job doesnt exist with this jobid: "+jobid);
			return new ResponseEntity<Job>(job, HttpStatus.NOT_FOUND);
		}
		
		job.setJobstatus(jobstatus);
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
	
	
	
	
	
	
/***************************************************** Related to JOB APPLICATION *********************************************************/
	

	
//	http://localhost:8086/collaborationRestService/job/registration
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
			JobApplication j = new JobApplication();
			j.setStatusMessage("Already Registered");
			return new ResponseEntity<JobApplication>(j, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else
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
	}
	

}
