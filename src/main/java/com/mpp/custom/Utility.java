package com.mpp.custom;

import java.util.Arrays;
import java.util.List;

public class Utility {

    static List<String> fromCommaSeperatedString(String input){
        return Arrays.asList(input.split(","));
    }



}
