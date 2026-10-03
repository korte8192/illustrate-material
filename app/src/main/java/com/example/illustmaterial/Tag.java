package com.example.illustmaterial;

public class Tag{
    String name;
    int count;
    String reading;

    Tag(String n,int c){
        this.name=n;
        this.count=c;

        //入力されたタイトルをひらがなにするReadingchanger.javaよび
        reading=ReadingChanger.getYomi(name);
    }

}

