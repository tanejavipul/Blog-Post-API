package ca.utoronto.utm.mcs;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.*;

import javax.inject.Inject;

import com.mongodb.client.model.Sorts;
import org.bson.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.regex.Pattern;


//@Module(injects = {DaggerModule.class})

public class DB {
    @Inject MongoClient mongodb;
    MongoDatabase database;
    MongoCollection<Document> collection;

    @Inject
    public DB(MongoClient x) {
        mongodb = x;
        try {
            database = mongodb.getDatabase("csc301a2");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }


    public String putPost(Document input) {
        ObjectId objectId;
        try {
            collection = database.getCollection("posts");
            collection.insertOne(input);
            objectId = (ObjectId) input.get("_id");
        } catch (Exception e) {
            return "-1";
        }
        return objectId.toString();
    }




    public ArrayList<String> getPost(String title, String id) {
        collection = database.getCollection("posts"); //MongoCollection<Document>
        Document temp = new Document();
        if (title == null) {
            try{
                ObjectId x = new ObjectId(id);
                temp.append("_id", x);
            }
            catch(Exception e)
            {
                return null;
            }
        } else if (id == null) {
            temp.append("title", Pattern.compile("\\b"+title+"\\b"));
        } else {
            ObjectId x = new ObjectId(id);
            //temp.append("title", title);
            temp.append("_id", x);    //post 224  says only look for id
        }
        FindIterable<Document> docs;
        if(id == null)
        {
            docs = collection.find(temp).sort(Sorts.orderBy(new Document("title", 1))); //     docs = collection.find(temp).sort(  new Document("title", 1));
            //.sort( { age: -1 } )
        }
        else
        {
            docs = collection.find(temp);

        }
        ArrayList<String> output = new ArrayList<>();
        for (Document doc : docs) {
            output.add(doc.toJson().toString());
        }
        System.out.println(output.toString());
        return output;
    }



    public Boolean deletePost(String input) {
        collection = database.getCollection("posts");
        Document temp = new Document();
        try
        {
            ObjectId id = new ObjectId(input);
            temp.append("_id", id);
        }
        catch (Exception e)
        {
            return null;
        }
        Document output = new Document();

        try {
            output = collection.findOneAndDelete(temp);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            if (output == null) {
                return null;
            }
            else
            {
                return false;
            }
        }
        return true;
    }




}