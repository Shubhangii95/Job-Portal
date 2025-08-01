package com.jobseek.service;

import com.jobseek.dao.CandidateDao;
import com.jobseek.dao.JobApplicationDao;
import com.jobseek.dao.JobDao;
import com.jobseek.dto.ApiResponse;
import com.jobseek.dto.JobApplicationDto;
import com.jobseek.entity.ApplicationStatus;
import com.jobseek.entity.Candidate;
import com.jobseek.entity.Job;
import com.jobseek.entity.JobApplication;
import com.jobseek.exception.DuplicateResourceException;
import com.jobseek.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService {

    public final JobApplicationDao jobApplicationDao;
    public final JobDao jobDao;
    public final CandidateDao candidateDao;

    @Override
    public ApiResponse addJobApplication(JobApplicationDto jobApplicationDto) {
        Job job = jobDao.findAllByJobIdAndIsActiveTrue(jobApplicationDto.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job Not Listed"));
        Candidate candidate = candidateDao.findById(jobApplicationDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate Not Present"));

        boolean alreadyApplied = jobApplicationDao.existsByCandidateAndJob(candidate, job);
        if (alreadyApplied) {
            throw new DuplicateResourceException("Already applied to this job");
        }

        JobApplication application = new JobApplication();
        application.setCandidate(candidate);
        application.setJob(job);
        application.setAppliedDate(LocalDate.now());
        application.setStatus(ApplicationStatus.APPLIED);
        jobApplicationDao.save(application);
        return new ApiResponse("Job Applied Successfullly");
    }
//
//    @Override
//    public List<Job> getAppliedJobs(Long userId) {
//
//        return List.of();
//    }
}
