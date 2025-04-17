package gov.epa.ghg.invdb.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.epa.ghg.invdb.repository.JobEventRepository;
import gov.epa.ghg.invdb.rest.dto.JobEventDto;

@RestController
@RequestMapping("/api/job")
public class JobController {
        @Autowired
        private JobEventRepository jobEventRepository;

        @GetMapping("/event")
        public JobEventDto getJobEvent(@RequestParam(name = "job") String jobName,
                        @RequestParam(name = "layer") int layerId,
                        @RequestParam(name = "year") int rptYear) {
                JobEventDto jobEvent = jobEventRepository.getEventDetails(jobName, layerId, rptYear);
                return jobEvent;
        }

}
