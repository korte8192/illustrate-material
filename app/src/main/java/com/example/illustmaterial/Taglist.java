package com.example.illustmaterial;

import java.util.*;

public class Taglist{
    ArrayList<Tag> taglist;

    Taglist(ArrayList<Tag> t){
        taglist=t;
    }
    Taglist(){
        taglist=new ArrayList<>();
    }



    boolean tagJudge(String word){
        String temp=ReadingChanger.getYomi(word);

        for(Tag tag:taglist){

            if(tag.reading.contains(temp)){
                return true;
            }
        }
        return false;
    }

    boolean tagNameJudge(String tagName){
        for(Tag tag:taglist){
            if(tag.name.equals(tagName)){
                return true;
            }
        }
        return false;
    }

    void tagPlus(String tagA){
        for(Tag tag:taglist){
            if(tag.name.equals(tagA)){
                tag.count++;
                return;//処理終了
            }
        }
        Tag temp_tag =new Tag(tagA,1);
        taglist.add(temp_tag);
    }
    void tagPlusExist(String tagA){
        Tag temp_tag =new Tag(tagA,1);
        taglist.add(temp_tag);
    }

    void sort(){
        taglist.sort((a,b)->Integer.compare(b.count,a.count));
    }

    void tagMinus(String tagA){
        for(int i=0;i<taglist.size();i++){
            Tag tag=taglist.get(i);
            if(tag.name.equals(tagA)){
                tag.count--;
                if(tag.count<1){
                    taglist.remove(i);
                }
                return;
            }

        }
    }

    ArrayList<String> showList(){
        ArrayList<String> result =new ArrayList<>();
        for(Tag tag:taglist){
            result.add(tag.name);
        }
        return result;
    }

    void PlusTag(String s){
        ArrayList<String> ans = new ArrayList<>(Arrays.asList(s.split(" ")));
        for(String d:ans){
            int found=-1;
            for(Tag tag:taglist){
                if(tag.name.equals(d)){
                    tag.count++;
                    found++;
                    break;
                }
            }
            if(found==-1){
                Tag tagTemp=new Tag(d,1);
                taglist.add(tagTemp);
            }

        }

    }



}