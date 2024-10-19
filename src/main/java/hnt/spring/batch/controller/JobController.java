package hnt.spring.batch.controller;

import hnt.spring.batch.util.FileUtil;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
@Slf4j
public class JobController {

    private final JobLauncher jobLauncher;
    private final Job job;
    private final FileUtil fileUtil;

    @PostMapping("/import-customers")
    public void importCsvToDBJob(@RequestParam("file") MultipartFile multipartFile) {
        log.info("importCsvToDBJob start {}", multipartFile.getOriginalFilename());
        try {
            File importFile = fileUtil.convertMultipartFileToFile(multipartFile);
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("fullPathFileName", importFile.getAbsolutePath())
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            if (BatchStatus.COMPLETED.equals(jobExecution.getStatus())) {
                FileUtils.forceDelete(importFile);
                log.info("importCsvToDBJob file {} finished at {}", multipartFile.getOriginalFilename(), LocalDateTime.now());
            }

        } catch (JobExecutionAlreadyRunningException | JobParametersInvalidException |
                 JobInstanceAlreadyCompleteException | JobRestartException | IOException e) {

            log.info("importCsvToDBJob get error {} finished at {}", e.getMessage(), LocalDateTime.now());
            throw new RuntimeException(e);
        }
    }

}
