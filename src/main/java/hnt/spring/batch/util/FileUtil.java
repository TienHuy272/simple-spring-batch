package hnt.spring.batch.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Component
public class FileUtil {
    public File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        long timeInMillis = System.currentTimeMillis();
        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdirs();  // Create the directory if it doesn't exist
        }
        File file = new File(directory.getAbsolutePath() + "/" + timeInMillis + multipartFile.getOriginalFilename());
        multipartFile.transferTo(file);
        return file;
    }
}
