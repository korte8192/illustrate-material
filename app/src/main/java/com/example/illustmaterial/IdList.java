package com.example.illustmaterial;

import java.util.*;
public class IdList{
    ArrayList<String> id;

    IdList(){
        id=new ArrayList<>();
    }
    void add(String s){
        id.add(s);
    }

    int idSearch(String s){
        int result=-1;
        for(int i=0;i<id.size();i++){
            if(id.get(i).equals(s)){
                result=i;
            }
        }
        return result;
    }

    void idDelete(String s){
        int temp=idSearch(s);
        if(temp!=-1){
            id.remove(temp);

        }
    }

}
