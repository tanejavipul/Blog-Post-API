package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class Post implements HttpHandler {

    @Inject
    DB connection;
    @Inject Utils utils;

    Post() {

    }

    public void handle(HttpExchange r) {
        try {
            if (r.getRequestMethod().equals("PUT")) {
                handlePut(r);
            } else if (r.getRequestMethod().equals("GET")) {
                handleGet(r);
            } else if (r.getRequestMethod().equals("DELETE")) {
                handleDelete(r);
            } else {
                r.sendResponseHeaders(405, 0);
                OutputStream os = r.getResponseBody();
                os.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }



    public void handlePut(HttpExchange r) throws IOException {
        try {
            Document httpInput = new Document();
            String input = utils.convert(r.getRequestBody());
            httpInput = httpInput.parse(input);

            Document httpOutput = new Document();
            Document mongoDoc = new Document();
            String output;

            //checker
            if (!(httpInput.containsKey("title") && httpInput.containsKey("author") && httpInput.containsKey("content") &&
                    httpInput.containsKey("tags"))) {
                Exception e = new Exception("Input does not contain required attributes.");
                throw e;
            }
            //checker
            if (!(httpInput.get("title").getClass().equals(String.class) && httpInput.get("author").getClass().equals(String.class)
                    && httpInput.get("content").getClass().equals(String.class))) {
                Exception e = new Exception("Needs String for Attributes.");
                throw e;
            }

            List<String> tags = (List<String>) httpInput.get("tags");
            mongoDoc.append("title", httpInput.get("title"));
            mongoDoc.append("author", httpInput.get("author"));
            mongoDoc.append("content", httpInput.get("content"));
            mongoDoc.append("tag", tags);

            //MongoDB Connection
            String id = connection.putPost(mongoDoc).toString(); // returns objectID

            if (id.equals("-1")) {
                r.sendResponseHeaders(500, 0);
                OutputStream os = r.getResponseBody();
                os.close();
            } else
            {
                try {
                    httpOutput.append("_id", id);
                    output = httpOutput.toJson().toString();


                    byte[] outputBytes = output.getBytes("UTF-8");
                    r.sendResponseHeaders(200, outputBytes.length);
                    OutputStream os = r.getResponseBody();
                    os.write(outputBytes);
                    os.close();
                }
                catch (Exception e)
                {
                    System.out.println("500: " + e.getMessage());
                    r.sendResponseHeaders(500, 0);
                    OutputStream os = r.getResponseBody();
                    os.close();
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            r.sendResponseHeaders(400, 0);
            OutputStream os = r.getResponseBody();
            os.close();
        }

    }



    public void handleGet(HttpExchange r) throws IOException {
        try {
            Document httpInput = new Document();
            String input = utils.convert(r.getRequestBody());
            httpInput = httpInput.parse(input);

            Document httpOutput = new Document();
            Document mongoDoc = new Document();
            String output;
            try {
                if (httpInput.containsKey("title") && httpInput.containsKey("_id")) {
                    String title = httpInput.get("title").toString();
                    String id = httpInput.get("_id").toString();
                    if (!(httpInput.get("title").getClass().equals(String.class) && httpInput.get("_id").getClass().equals(String.class))) {
                        Exception e = new Exception("Needs String for Attributes.");
                        throw e;
                    }

                    ArrayList<String> list = connection.getPost(title, id);
                    if (list == null || list.size() < 1) {
                        r.sendResponseHeaders(404, 0);
                        OutputStream os = r.getResponseBody();
                        os.close();
                        return;
                    } else {
                        try {
                            output = list.toString();

                            byte[] bs = output.getBytes("UTF-8");
                            r.sendResponseHeaders(200, bs.length);
                            OutputStream os = r.getResponseBody();
                            os.write(bs);
                            os.close();
                        }
                        catch (Exception e)
                        {
                            System.out.println(e.getMessage());
                            r.sendResponseHeaders(500, 0);
                            OutputStream os = r.getResponseBody();
                            os.close();
                        }
                    }
                } else if (httpInput.containsKey("title")) {
                    String title = httpInput.get("title").toString();
                    if (!(httpInput.get("title").getClass().equals(String.class))) {
                        Exception e = new Exception("Needs String for Attributes.");
                        throw e;
                    }

                    ArrayList<String> list = connection.getPost(title, null);
                    if (list == null || list.size() < 1) {
                        r.sendResponseHeaders(404, 0);
                        OutputStream os = r.getResponseBody();
                        os.close();
                        return;
                    }
                    else {


                        output = list.toString();

                        byte[] bs = output.getBytes("UTF-8");
                        r.sendResponseHeaders(200, bs.length);
                        OutputStream os = r.getResponseBody();
                        os.write(bs);
                        os.close();
                    }
                } else if (httpInput.containsKey("_id")) {
                    String id = httpInput.get("_id").toString();
                    if (!(httpInput.get("_id").getClass().equals(String.class))) {
                        Exception e = new Exception("Needs String for Attributes.");
                        throw e;
                    }
                    ArrayList<String> list = connection.getPost(null, id);

                    if (list == null || list.size() < 1) {
                        r.sendResponseHeaders(404, 0);
                        OutputStream os = r.getResponseBody();
                        os.close();
                        return;
                    }
                    else {
                        output = list.toString();

                        byte[] outputBytes = output.getBytes("UTF-8");
                        r.sendResponseHeaders(200, outputBytes.length);
                        OutputStream os = r.getResponseBody();
                        os.write(outputBytes);
                        os.close();
                    }


                } else {
                    Exception e = new Exception("NOT FOUND");
                    throw e;
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
                r.sendResponseHeaders(400, 0);
                OutputStream os = r.getResponseBody();
                os.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            r.sendResponseHeaders(400, 0);
            OutputStream os = r.getResponseBody();
            os.close();
        }
    }





    public void handleDelete(HttpExchange r) throws IOException {
        try {
            Document httpInput = new Document();
            String input = utils.convert(r.getRequestBody());
            httpInput = httpInput.parse(input);


            String id = httpInput.get("_id").toString();
            Boolean feedback = connection.deletePost(id);

            try {
                if (feedback == null) //if there is nothing to delete
                {
                    r.sendResponseHeaders(404, 0);
                    OutputStream os = r.getResponseBody();
                    os.close();
                } else if (feedback == true) {
                    r.sendResponseHeaders(200, 0);
                    OutputStream os = r.getResponseBody();
                    os.close();
                } else {
                    r.sendResponseHeaders(400, 0);
                    OutputStream os = r.getResponseBody();
                    os.close();
                }
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
                r.sendResponseHeaders(500, 0);
                OutputStream os = r.getResponseBody();
                os.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            r.sendResponseHeaders(400, 0);
            OutputStream os = r.getResponseBody();
            os.close();
        }
    }





}
