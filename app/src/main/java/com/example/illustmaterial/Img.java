package com.example.illustmaterial;

import java.util.*;

public class Img{
    ArrayList<String> imgName; //pngの名前保存
    String title; //title名
    String reading; //titleよみ
    Taglist tag;//該当するすべてのtag

    Img(ArrayList<String> n,String t,Taglist g){
        imgName=n;
        title=t;
        tag=g;

        reading=ReadingChanger.getYomi(title);
    }

    Img(){
        imgName=new ArrayList<>();
        imgName.add("sample");

        title="sample";

        ArrayList<Tag> temp=new ArrayList<>();
        tag=new Taglist(temp);

        reading=ReadingChanger.getYomi(title);
    }

    boolean titleJudge(String word){
        String search=ReadingChanger.getYomi(word);
        if(reading.contains(search)){
            return true;
        }else{
            return false;
        }

    }

    boolean judge(String word){
        if(titleJudge(word)||tag.tagJudge(word)){
            return true;
        }else{
            return false;
        }
    }
}