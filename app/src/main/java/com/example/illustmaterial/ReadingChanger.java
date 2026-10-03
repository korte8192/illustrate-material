package com.example.illustmaterial;

import java.util.*;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

public class ReadingChanger{
    private static final Tokenizer tokenizer = new Tokenizer();
    static String  getYomi(String text){

        List<Token> tokens=tokenizer.tokenize(text);

        String result="";

        for(Token t : tokens){

            String reading = t.getReading();

            if(reading == null || reading.isEmpty()|| reading.equals("*")){
                result += t.getSurface();
            }else{
                result += reading;
            }
        }
        return toHiragana(result);
    }

    static String toHiragana(String text){

        StringBuilder result=new StringBuilder();

        for(char c:text.toCharArray()){
            if (c >= 'ァ' && c <= 'ヶ') {
                result.append((char)(c - 0x60));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }



}



