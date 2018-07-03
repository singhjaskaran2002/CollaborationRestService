package com.jaskaran.project2.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jaskaran.project2.DAO.JobDAO;
import com.jaskaran.project2.DAO.UserDAO;
import com.jaskaran.project2.Domain.Job;
import com.jaskaran.project2.Domain.JobApplication;
import com.jaskaran.project2.Domain.User;

@RestController
public class JobController 
{
	@Autowired
	private Job job;
	
	@Autowired
	private JobDAO jobDAO;
	
	@Autowired
	private JobApplication jobApplication;
	
	
	
//	http://localhost:8080/collaborationRestService/jobList
	@RequestMapping("/jobList")
	public ResponseEntity<List<Job>> jobList()
	{
		List<Job> jobList = jobDAO.jobList(); 
		return new ResponseEntity<List<Job>>(jobList, HttpStatus.OK);
	}
	
	
	
//	http://localhost:8080/collaborationRestService/getJob/{jobid}
	@RequestMapping("/getJob/{jobid}")
	public ResponseEntity<Job> getJob(@PathVariable int jobid)
	{
		job = jobDAO.getJob(jobid);
		if(job == null)
		{
			job = new Job();
			job.setStatusMessage("No job is found with JobID: "+jobid);
			return new ResponseEntity<Job>(job, HttpStatus.NOT_FOUND);
		}
		else
		{
			job.setStatusMessage("This is the particular Job Details of JobID: "+jobid);
			return new ResponseEntity<Job>(job, HttpStatus.OK);
		}
	}
	
	
	
//	http://localhost:8080/collaborationRestService/getJobListByStatus/{jobstatus}	
	@RequestMapping("/getJobListByStatus/{jobstatus}")
	public ResponseEntity<List<Job>> getJobListByStatus(@PathVariable char jobstatus)
	{
		List<Job> jobs = jobDAO.jobList(jobstatus);
		if(jobs.isEmpty())
		{
			jobs.add(job);
			job.setStatusMessage("No job found with JobStatus: "+jobstatus);
			return new ResponseEntity<List<Job>>(jobs, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<List<Job>>(jobs, HttpStatus.OK);
	}
	
	
	
//	http://localhost:8080/collaborationRestService/saveJob
	@PostMapping("/saveJob")
	public ResponseEntity<Job> saveJob(@RequestBody Job job)
	{
		Job j = jobDAO.getJob(job.getJobid());
		if(j != null)
		{
			job = new Job();
			job.setStatusMessage("Job already exist with Jobid: "+job.getJobid());
			return new ResponseEntity<Job>(job, HttpStatus.CONFLICT);
		}
		
		if(jobDAO.saveJob(job))
		{
			job.setStatusMessage("Job Saved Successfully with Jobid: "+job.getJobid());
			return new ResponseEntity<Job>(job, HttpStatus.OK);
		}
		else
		{
			job = new Job();
			job.setStatusMessage("Cannot post Job right now with Jobid: "+job.getJobid()+", please try again later..");
			return new ResponseEntity<Job>(job, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	
//	http://localhost:8080/collaborationRestService/deleteJob/{jobid}	
	@DeleteMapping("/deleteJob/{jobid}")
	public ResponseEntity<Job> deleteJob(@PathVariable int jobid)
	{
		Job j = jobDAO.getJob(jobid);
		if(j == null)
		{
			job = new Job();
			job.setStatusMessage("Job doesn't Exists with JobID: "+jobid);
			return new ResponseEntity<Job>(job, HttpStatus.CONFLICT);
		}
		
		List<JobApplication> appliedJobs = jobDAO.jobApplicationlist(jobid);
		if(!appliedJobs.isEmpty())
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
	
	
	
	
	
// related to JOB APPLICATION
	

	
//	http://localhost:8080/collaborationRestService/jobRegistration
	@PostMapping("/jobRegistration")
	public ResponseEntity<JobApplication> jobRegistration(@RequestBody JobApplication jobApplication)
	{
		if(jobDAO.saveJobApplication(jobApplication))
		{
			jobApplication.setStatusMessage("Registered Successfully..");
			return new ResponseEntity<JobApplication>(jobApplication, HttpStatus.OK);
		}
		else
		{
			jobApplication.setStatusMessage("Cannot Register now please try again later..");
			return new ResponseEntity<JobApplication>(jobApplication, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
}
