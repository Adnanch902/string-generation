package com.random.stringGenerator.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class StringGenerationService {
    public List<String> generateRandomStrings(int numberStrings, int charactersPerString, String[] selectedCharacterTypes) {
        String allowedCharacters = Arrays.stream(selectedCharacterTypes)
                .flatMapToInt(String::chars)
                .distinct()
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return IntStream.range(0, numberStrings)
                .mapToObj(i -> generateRandomString(charactersPerString, allowedCharacters))
                .collect(Collectors.toList());
    }

    public List<String> generateUniqueRandomStrings(int numberStrings, int charactersPerString, String[] selectedCharacterTypes) {
        Set<String> uniqueStrings = generateUniqueStrings(numberStrings, charactersPerString, selectedCharacterTypes);

        return uniqueStrings.stream().collect(Collectors.toList());
    }

    private Set<String> generateUniqueStrings(int numberStrings, int charactersPerString, String[] selectedCharacterTypes) {
        String allowedCharacters = Arrays.stream(selectedCharacterTypes)
                .flatMapToInt(String::chars)
                .distinct()
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        Set<String> uniqueStrings = new HashSet<>();

        while (uniqueStrings.size() < numberStrings) {
            String randomString = generateRandomString(charactersPerString, allowedCharacters);
            uniqueStrings.add(randomString);
        }

        return uniqueStrings;
    }
    public String generateRandomString(int length, String allowedCharacters) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be a positive integer.");
        }

        Random random = new Random();
        return random.ints(length, 0, allowedCharacters.length())
                .mapToObj(allowedCharacters::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

}