package com.example.illustmaterial;

import org.json.JSONObject;
import org.json.JSONArray;
import java.util.*;

public class DataList{
    ArrayList<Img> imgList;
    //ArrayList<Myimg> myImgList;
    Taglist mainTaglist;


    DataList(ArrayList<Img> i,Taglist t){
        imgList=i;
        //myImgList=m;
        mainTaglist=t;
    }
    DataList(){
        imgList=new ArrayList<>();
        mainTaglist=new Taglist();
    }
    //keywordは検索するためのtag名とタイトル名

    //tagFindはあくまで該当するtagを調べるものでtagのデータと画像３枚が欲しい
    //帰り値はkeyがtagの名前ArrayListの方が該当tagの画像3枚
    //tag存在検索用
    HashMap<Tag,ArrayList<String>> tagFind(String tagName){
        HashMap<Tag,ArrayList<String>> result=new HashMap<>();
        for(Tag x:mainTaglist.taglist){
            if(x.name.equals(tagName)){
                Tag tagTemp=x;

                //該当画像３つのimgNameを取得
                ArrayList<String> ans =new ArrayList<>();
                int count=0;
                for(Img img:imgList){
                    if(img.tag.tagNameJudge(x.name)){
                        ans.add(img.imgName.get(0));
                        count++;
                    }
                    if(count>=3){
                        break;
                    }

                }

                result.put(tagTemp,ans);

            }

        }
        return result;
    }
    //内部検索用
    DataList tagSearch(String tagname){
        DataList result=new DataList();
        ArrayList<Img> list =new ArrayList<>();
        for(Img img:imgList){
            for(Tag x:img.tag.taglist){
                if(x.name.equals(tagname)){
                    list.add(img);
                }
            }

        }
        result.imgList=list;
        return result;
    }

    int searchImgIndex(Img img){
        for(int i=0;i<imgList.size();i++){
            if(imgList.get(i).equals(img)){
                return i;
            }
        }
        return -1;
    }

    DataList search(List<String> words){
        DataList result = new DataList();

        for(Img img : imgList){
            boolean match = true;
            for(String word : words){
                if(!img.judge(word)){
                    match = false;
                    break;
                }
            }
            if(match){
                result.imgList.add(img);
            }
        }
        return result;
    }
    //画面に一枚だけ最新の画像を表示するためにimgを渡す
    Img pickUp(){
        if(!imgList.isEmpty()){
            return imgList.get(imgList.size()-1);
        }else {
            return new Img();
        }

    }
    void addImg(ArrayList<String> lis,String ti,ArrayList<String> ta){//順に画像データ、タイトル、つけたいタグ
        //Imgに入れるtaglistの作成
        ArrayList<Tag> tempTL=new ArrayList<>();
        for(String x:ta){
            Tag tempT=new Tag(x,1);
            tempTL.add(tempT);

            //登録後のmainTaglistの管理
            mainTaglist.tagPlus(x);
        }
        Taglist reTaglist =new Taglist(tempTL);

        //Imgの設定
        Img tempImg=new Img(lis,ti,reTaglist);
        imgList.add(tempImg);

    }

    void updateMainTag(){
        mainTaglist = new Taglist();
        for(Img i:imgList){
            for(Tag t:i.tag.taglist){
                mainTaglist.tagPlus(t.name);
            }
        }
    }

    void deleteImgs(Collection<Img> imgs){
        for(Img i:imgs){
            imgList.remove(i);
        }
        updateMainTag();
    }

    void deleteImg(Img x){
        imgList.remove(x);
        updateMainTag();
    }

    String save(){
        JSONObject root=new JSONObject();

        try{
            JSONArray images=new JSONArray();

            for(Img img:imgList){
                JSONObject image=new JSONObject();

                image.put("imgTitle",img.title);

                JSONArray names=new JSONArray();
                for(String name: img.imgName){
                    names.put(name);
                }

                image.put("imgName",names);

                JSONArray tags=new JSONArray();

                for(Tag t:img.tag.taglist){
                    JSONObject tag=new JSONObject();
                    tag.put("tagName",t.name);
                    tag.put("tagCount",t.count);

                    tags.put(tag);
                }
                image.put("tags",tags);

                images.put(image);

            }root.put("images",images);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return root.toString();
    }

    void load(String json){
        try{
            JSONObject root =new JSONObject(json);

            JSONArray images=root.getJSONArray("images");

            for(int i=0;i<images.length();i++){
                JSONObject image=images.getJSONObject(i);

                //タイトル
                String title=image.getString("imgTitle");

                //image name
                JSONArray names=image.getJSONArray("imgName");

                ArrayList<String> imgNames=new ArrayList<>();

                for(int j=0;j<names.length();j++){
                    imgNames.add(names.getString(j));
                }

                //tag
                JSONArray tags=image.getJSONArray("tags");

                Taglist taglist =new Taglist();

                for(int k=0;k<tags.length();k++){
                    JSONObject tag=tags.getJSONObject(k);

                    String tagName=tag.getString("tagName");
                    int tagCount=tag.getInt("tagCount");

                    Tag tempTag = new Tag(tagName, tagCount);

                    taglist.taglist.add(tempTag);
                }
                // Imgを作る
                Img tempImg = new Img(imgNames, title, taglist);

                imgList.add(tempImg);
            }
            updateMainTag();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
