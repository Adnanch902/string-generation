package com.random.stringGenerator.controller;

import com.random.stringGenerator.entity.GeneratedString;
import com.random.stringGenerator.entity.InputForm;
import com.random.stringGenerator.repository.GeneratedStringRepository;
import com.random.stringGenerator.service.StringGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@ControllerAdvice
public class GenerateController {

    private static final Logger logger = LoggerFactory.getLogger(GenerateController.class);

    @Autowired
    private StringGenerationService stringGenerationService;

    @Autowired
    private GeneratedStringRepository stringRepository;

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("inputForm", new InputForm());
        return "inputForm";
    }

    @GetMapping("/generateAgain")
    public String generateAgain(Model model) {
        model.addAttribute("inputForm", new InputForm());
        return "inputForm";
    }

    @PostMapping("/generate")
    public String generateStrings(
            @ModelAttribute InputForm inputForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        int numberStrings = inputForm.getNumberStrings();
        int charactersPerString = inputForm.getCharactersPerString();

        if (numberStrings <= 0 || charactersPerString <= 0) {
            throw new IllegalArgumentException("Number of strings and characters per string must be positive integers.");
        }
        if (bindingResult.hasErrors()) {
            // Redirect back to the input form with error messages
            return "inputForm";
        }

        try {
            String[] selectedCharacterTypes = inputForm.getCharacterTypes();
            List<String> generatedStrings = generateStrings(
                    inputForm.getNumberStrings(),
                    inputForm.getCharactersPerString(),
                    selectedCharacterTypes,
                    inputForm.isUniqueStrings()
            );
            saveGeneratedStrings(generatedStrings);
            model.addAttribute("generatedStrings", generatedStrings);

            logger.info("Generated strings: {}", generatedStrings);
            return "outputPage";
        } catch (IllegalArgumentException e) {
            logger.error("Error generating strings", e);
            redirectAttributes.addFlashAttribute("error", "Error generating strings: " + e.getMessage());
            return "redirect:/error";
        }
    }

    @GetMapping("/history")
    public String showHistory(Model model) {
        List<String> generatedStrings = stringRepository.findAll().stream()
                .map(GeneratedString::getGeneratedString)
                .collect(Collectors.toList());
        model.addAttribute("generatedStrings", generatedStrings);
        return "outputPage";
    }

    private List<String> generateStrings(int numberStrings, int charactersPerString, String[] selectedCharacterTypes, boolean uniqueStrings) {
        try {
            if (uniqueStrings) {
                return stringGenerationService.generateUniqueRandomStrings(numberStrings, charactersPerString, selectedCharacterTypes);
            } else {
                return stringGenerationService.generateRandomStrings(numberStrings, charactersPerString, selectedCharacterTypes);
            }
        } catch (IllegalArgumentException e) {
            // Handle more specific exceptions if needed
            throw new IllegalArgumentException("Error generating strings: " + e.getMessage());
        }
    }

    private List<String> generateRandomStrings(int numberStrings, int charactersPerString, String allowedCharacters) {
        return IntStream.range(0, numberStrings)
                .mapToObj(i -> stringGenerationService.generateRandomString(charactersPerString, allowedCharacters))
                .collect(Collectors.toList());
    }

    private List<String> generateUniqueRandomStrings(int numberStrings, int charactersPerString, String allowedCharacters) {
        List<String> generatedStrings = new ArrayList<>();
        Set<String> uniqueStrings = new HashSet<>();

        while (uniqueStrings.size() < numberStrings) {
            String randomString = stringGenerationService.generateRandomString(charactersPerString, allowedCharacters);

            if (uniqueStrings.add(randomString)) {
                generatedStrings.add(randomString);
            }
        }

        return generatedStrings;
    }

    private void saveGeneratedStrings(List<String> generatedStrings) {
        generatedStrings.forEach(str -> {
            GeneratedString generatedString = new GeneratedString();
            generatedString.setGeneratedString(str);
            stringRepository.save(generatedString);
        });
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "errorPage";
    }
}
