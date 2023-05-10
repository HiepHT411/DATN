package com.hoanghiep.hust.serviceImpl;

import com.hoanghiep.hust.dto.CreatePartDto;
import com.hoanghiep.hust.dto.PartDto;
import com.hoanghiep.hust.entity.Part;
import com.hoanghiep.hust.entity.Question;
import com.hoanghiep.hust.entity.UnitTest;
import com.hoanghiep.hust.exception.ResourceUnavailableException;
import com.hoanghiep.hust.repository.PartRepo;
import com.hoanghiep.hust.repository.QuestionRepo;
import com.hoanghiep.hust.repository.UnitTestRepository;
import com.hoanghiep.hust.repository.UserRepository;
import com.hoanghiep.hust.service.IAudioService;
import com.hoanghiep.hust.service.IPartService;
import com.hoanghiep.hust.service.S3StorageService;
import com.hoanghiep.hust.utility.CommonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PartServiceImpl implements IPartService {

    @Autowired
    private PartRepo partRepo;

    @Autowired
    private UnitTestRepository unitTestRepo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepo questionRepo;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private IAudioService audioService;

    @Autowired
    private S3StorageService s3service;

    @Override
    public Page<Part> getAllParts(int pageNo, int pageSize, String sortField, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return partRepo.findAll(pageable);
    }

    @Override
    public Part getPartById(Long id) {
        return partRepo.findById(id).orElseThrow(()-> new ResourceUnavailableException("part not found with id: "+id));
    }

    @Override
    public void deletePartById(Long id) {
        Optional<Part> part = partRepo.findById(id);
        if(part.isPresent()) {
            List<Question> questions = questionRepo.findByPart(part.get());
            questionRepo.deleteAll(questions);
        }
        partRepo.deleteById(id);
    }

    @Override
//    @Transactional
    public Part createPart(CreatePartDto createPartDto, String username) {
        UnitTest unitTest = new UnitTest();
        Part part = new Part();
        if(Objects.nonNull(unitTestRepo.findByYearAndUnitTestNumber(createPartDto.getUnitTestYear(), createPartDto.getUnitTestNumber()))){
            unitTest = unitTestRepo.findByYearAndUnitTestNumber(createPartDto.getUnitTestYear(), createPartDto.getUnitTestNumber());
            if(Objects.nonNull(partRepo.findByUnitTestIdAndPartNumber(unitTest.getId(), createPartDto.getPartNumber()))){
                part = partRepo.findByUnitTestIdAndPartNumber(unitTest.getId(), createPartDto.getPartNumber());
            }
        } else {
            unitTest.setCreatedBy(username);
            unitTest.setYear(createPartDto.getUnitTestYear());
            unitTest.setUnitTestNumber(createPartDto.getUnitTestNumber());
            unitTest.setDescription(createPartDto.getUnitTestDescription());
            unitTest.setCreatedDate(LocalDateTime.now());
            unitTest = unitTestRepo.save(unitTest);
        }
        part.setUnitTest(unitTest);
        part.setCreatedBy(userRepository.findByUsername(username));
        part.setPartNumber(createPartDto.getPartNumber());
        part.setDescription(createPartDto.getPartDescription());
        part.setPartType(createPartDto.getPartType());
        part.setTimes(createPartDto.getTimes());
        part.setNumberOfQuestions(createPartDto.getNumberOfQuestions());

        return this.partRepo.save(part);
    }

    public Part createPart(CreatePartDto createPartDto, String username, MultipartFile audio) {
        Part newPart = this.createPart(createPartDto, username);
        if (Objects.nonNull(audio) && !audio.isEmpty()) {
//            StringBuilder fileNames = new StringBuilder();
//            audioService.store(audio);
//            fileNames.append(audio.getOriginalFilename());
//            newPart.setAudio("/audios/"+fileNames);
            String audioLink = s3service.uploadFile(audio, "audiofiles/");
            newPart.setAudio("https://toeicappstorage.s3.ap-southeast-2.amazonaws.com/" + audioLink);
        }
        return partRepo.save(newPart);
    }

    @Override
    public List<Part> getPartByUnitTestId(Long unitTestId) {
        return partRepo.findByUnitTestId(unitTestId);
    }

    @Override
    public Part getPartByUnitTestIdAndPartNumber(Long unitTestId, int partNumber) {
        return partRepo.findByUnitTestIdAndPartNumber(unitTestId, partNumber);
    }

    @Override
    public Page<PartDto> getAllNonNullParts(int pageNo, int pageSize, String sortField, String sortDir) {
//        Page<Part> partPage = getAllParts(pageNo, pageSize, sortField, sortDir);
//        Page<PartDto> pagePartDto = commonMapper.convertToResponsePage(partPage, PartDto.class, partPage.getPageable());
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, sort);
        Page<Part> partList = partRepo.findAllPart(pageable);
        Page<PartDto> partDtoList = commonMapper.convertToResponsePage(partList, PartDto.class, partList.getPageable());

//        List<PartDto> partDtos = new ArrayList<>();

        for (int i = 0; i < partDtoList.getContent().size(); i++) {

            UnitTest unitTest = partList.getContent().get(i).getUnitTest();
            if (Objects.nonNull(unitTest)) {
                partDtoList.getContent().get(i).setYear(unitTest.getYear());
                partDtoList.getContent().get(i).setUnitTestNumber(unitTest.getUnitTestNumber());
            }
        }

//        for (int i = partDtoList.size()-1; i >= 0 ; i--) {
//            PartDto dto = partDtoList.get(i);
//            UnitTest unitTest = partList.get(i).getUnitTest();
//            dto.setYear(unitTest.getYear());
//            dto.setUnitTestNumber(unitTest.getUnitTestNumber());
//            dto.setNumberOfQuestions(Objects.isNull(dto.getNumberOfQuestions()) ? 0 : dto.getNumberOfQuestions());
//            partDtos.add(dto);
//        }

//        if(pageNo>1){
//            int from = (pageNo-1)*10;
////            int to = pageNo*10 <= partDtos.size() ? pageNo*10 : partDtos.size();
//            for(int i=from-1; i>=0; i--) {
//                partDtos.remove(i);
//            }
//        } else if (pageNo ==1 && partDtos.size() > 10) {
//            for (int i=10; i<partDtos.size(); i++) {
//                partDtos.remove(i);
//            }
//        }

//        final int start = (int) partPage.getPageable().getOffset();
//        final int end = Math.min((start + partPage.getPageable().getPageSize()), partDtos.size());
//        final Page<PartDto> page = new PageImpl<>(partDtos.subList(start, end), partPage.getPageable(), partDtos.size());

//        final Page<PartDto> page = new PageImpl<>(partDtos, pageable, partDtos.size());

        return partDtoList;
    }
}
