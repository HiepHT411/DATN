package com.hoanghiep.hust.utility;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.hoanghiep.hust.dto.CreatePartDto;
import com.hoanghiep.hust.entity.Part;
import com.hoanghiep.hust.entity.Question;
import com.hoanghiep.hust.entity.QuestionStackDirections;
import com.hoanghiep.hust.enums.PartType;
import com.hoanghiep.hust.repository.QuestionRepo;
import com.hoanghiep.hust.repository.QuestionStackDirectionsRepository;
import com.hoanghiep.hust.service.IPartService;
import com.hoanghiep.hust.service.S3StorageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class CSVReaderUtils {

    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private QuestionRepo questionRepo;

    @Autowired
    private QuestionStackDirectionsRepository questionStackDirectionsRepository;

    @Autowired
    private IPartService partService;

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    private S3StorageService storageService;

    public List<Question> readQuestionFromCSV(String fileName) {
        List<Question> questions = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);
        // create an instance of BufferedReader
        // using try with resource
        try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.ISO_8859_1)) {
            // read the first line from the text file
            String line = br.readLine();
            if(line != null) {
                line = br.readLine();
            }
            // loop until all lines are read
            while (line != null) {
                // use string.split to load a string array with the values from each line of the file, using a comma as the delimiter
                String[] attributes = line.split(",");
                Question question = createQuestion(attributes);
                // adding question into ArrayList
                questions.add(question);
                // read next line before looping
                // if end of file reached, line would be null
                line = br.readLine(); }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return questionRepo.saveAll(questions);
    }

    public List<Question> importAndStoreImportFileToAwsS3(MultipartFile file) {

        List<Question> questions = new ArrayList<>();

        String fileName = storageService.uploadFile(file, "importfiles/");

        S3Object s3object = s3Client.getObject(bucketName, fileName);
        InputStream is = s3object.getObjectContent();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        try {
            // read the first line from the text file
            String line = br.readLine();
            if(line != null) {
                line = br.readLine();
            }
            // loop until all lines are read
            while (line != null) {
                // use string.split to load a string array with the values from each line of the file, using a comma as the delimiter
                String[] attributes = line.split(";");
                Question question = createQuestion(attributes);
                // adding question into ArrayList
                questions.add(question);
                // read next line before looping
                // if end of file reached, line would be null
                line = br.readLine(); }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return questionRepo.saveAll(questions);
    }

    private Question createQuestion(String[] metadata) {

        int index = Integer.parseInt(metadata[0]);
        String title = metadata[1];
        String optionA = metadata[2];
        String optionB = metadata[3];
        String optionC = metadata[4];
        String optionD = metadata[5];
        int ans = Integer.parseInt(metadata[6]);
        int chose = -1;
        String image = metadata[7];
        String qsdTitle = metadata[8];
        String qsdDirection = metadata[9];
//        long partId = Long.parseLong(metadata[10]);
        int unitTestNumber = Integer.parseInt(metadata[10]);
        String unitTestYear = metadata[11];
        String unitTestDescription = metadata[12];
        int partNumber = Integer.parseInt(metadata[13]);
        PartType partType = PartType.valueOf(metadata[14]);
        String partDescription = metadata[15];
        int numberOfQuestions = Integer.parseInt(metadata[16]);
        int times = Integer.parseInt(metadata[17]);

        QuestionStackDirections questionStackDirections = null;
        if(Objects.nonNull(qsdTitle) && !qsdTitle.isEmpty()
                && Objects.nonNull(qsdDirection) && !qsdDirection.isEmpty()){
            QuestionStackDirections newQuestionStackDirections = new QuestionStackDirections(qsdTitle, qsdDirection);
            questionStackDirections = questionStackDirectionsRepository.save(newQuestionStackDirections);
        }
//        Part part = partService.getPartById(partId);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        CreatePartDto createPartDto = new CreatePartDto(unitTestNumber,unitTestYear, unitTestDescription, partNumber, partType, partDescription, numberOfQuestions, times);
        Part part = partService.createPart(createPartDto, username);
        Question question = Question.builder()
                .index(index)
                .title(title)
                .ans(ans)
                .chose(chose)
                .optionA(optionA)
                .optionB(optionB)
                .optionC(optionC)
                .optionD(optionD)
                .image(StringUtils.isNotBlank(image) ? "/images/"+image : "")
                .part(part)
                .build();
        if (Objects.nonNull(questionStackDirections)){
            question.setQuestionStackDirections(questionStackDirections);
        }
        return question;
    }

}
