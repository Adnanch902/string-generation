package com.random.stringGenerator.entity;


import lombok.Data;

@Data
public class InputForm {
    private int numberStrings;
    private int charactersPerString;
    private String[] characterTypes;
    private boolean uniqueStrings;
}
