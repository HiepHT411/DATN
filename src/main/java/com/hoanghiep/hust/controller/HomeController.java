package com.hoanghiep.hust.controller;

import com.hoanghiep.hust.dto.PartDto;
import com.hoanghiep.hust.entity.UnitTest;
import com.hoanghiep.hust.service.IPartService;
import com.hoanghiep.hust.service.IUnitTestService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private IPartService partService;

    @Autowired
    private IUnitTestService unitTestService;

    @GetMapping("/")
    public String main() {
        return "home";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping(value = "/toeictips")
    public String toeicTips() {
        return "toeictips.html";
    }

    @GetMapping(value = "/test/audio")
    public String testAudio(){
        return "startListeningPart";
    }

    @GetMapping("/partList")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public String getUnitTest(Model model, @RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                              @RequestParam(value = "sortField", required = false, defaultValue = "id") String sortField,
                              @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {
//        Page<Part> partPage = partService.getAllParts(pageNo, pageSize, sortField, sortDir);
        Page<PartDto> partDtos = partService.getAllNonNullParts(pageNo, pageSize, sortField, sortDir);

        model.addAttribute("listOfParts", partDtos.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", partDtos.getTotalPages());
        model.addAttribute("totalItems", partDtos.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "partList";
    }

    @GetMapping(value = "/examList")
    @PreAuthorize("isAuthenticated()")
    public String getListOfMockExam(Model model, @RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo,
                                    @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                    @RequestParam(value = "sortField", required = false, defaultValue = "id") String sortField,
                                    @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {

        Page<UnitTest> pageUnitTest = unitTestService.getAllUnitTests(pageNo, pageSize, sortField, sortDir);
        if(!pageUnitTest.getContent().isEmpty()) {
            model.addAttribute("listOfUnitTests", pageUnitTest.getContent());
        }
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", pageUnitTest.getTotalPages());
        model.addAttribute("totalItems", pageUnitTest.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "mockExams";
    }

    @GetMapping(path = {"/searchUnitTestByYear"})
    public String home(Model model, String keyword,
                       @RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo,
                       @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                       @RequestParam(value = "sortField", required = false, defaultValue = "id") String sortField,
                       @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {
        if (keyword != null && StringUtils.isNotBlank(keyword)) {
            Page<UnitTest> pageUnitTest = unitTestService.getUnitTestsByYear(pageNo, pageSize, sortField, sortDir, keyword);
            if(!pageUnitTest.getContent().isEmpty()) {
                model.addAttribute("listOfUnitTests", pageUnitTest.getContent());
            }
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("totalPages", pageUnitTest.getTotalPages());
            model.addAttribute("totalItems", pageUnitTest.getTotalElements());

            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        } else {
            model.addAttribute("keyword", "");
            getListOfMockExam(model, pageNo, pageSize, sortField, sortDir);
        }
        model.addAttribute("keyword", keyword);
        return "mockExams";
    }

}
