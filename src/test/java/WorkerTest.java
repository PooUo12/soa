import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;

public class WorkerTest {


    @BeforeAll
    static void init(){
        try {
            SSLContext sslcontext = SSLContexts.custom()
                    .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                    .build();

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            CloseableHttpClient httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .build();
            Unirest.setHttpClient(httpclient);
        } catch (Exception e){
            e.printStackTrace();
        }


    }

    private HttpResponse<String> sendGetRequest(String url, Object id) throws UnirestException {
        return Unirest.get(url + id)
                .header("Content-Type","application/json")
                .asString();
    }

    private HttpResponse<String> sendDeleteRequest(String url, Object id) throws UnirestException {
        return Unirest.delete(url + id)
                .header("Content-Type","application/json")
                .asString();
    }

    @Test
    public void testGetSalary() throws UnirestException {
        String url = "https://127.0.0.1:8443/worker-0.0.1/api/workers/salary";

        var test_1 = sendGetRequest(url, "");
        Assertions.assertEquals("[3000]", test_1.getBody());
        Assertions.assertEquals(200, test_1.getStatus());

    }

    @Test
    public void testGetWorker() throws UnirestException {
        String url = "https://127.0.0.1:8443/worker-0.0.1/api/workers/";
        int id_1 = 1;
        int id_2 = 406;
        String id_3 = null;
        String id_4 = "Smth went wrong";

        var test_1 = sendGetRequest(url, id_1);
        Assertions.assertEquals("[\"Worker with id 1 not found\"]", test_1.getBody());
        Assertions.assertEquals(400, test_1.getStatus());

        var test_2 = sendGetRequest(url, id_2);
        Assertions.assertTrue(test_2.getBody().contains("\"x\":-555"));
        Assertions.assertTrue(test_2.getBody().contains("\"birthday\":\"2024-12-01T14:55:50.317Z[UTC]\""));
        Assertions.assertTrue(test_2.getBody().contains("\"x\":11.0"));
        Assertions.assertEquals(200, test_2.getStatus());

        var test_3 = sendGetRequest(url, id_3);
        Assertions.assertEquals("Id should be integer", test_3.getBody());
        Assertions.assertEquals(422, test_3.getStatus());

        var test_4 = sendGetRequest(url, id_4);
        Assertions.assertEquals("Id should be integer", test_4.getBody());
        Assertions.assertEquals(422, test_4.getStatus());
    }

    @Test
    public void testRemoveWorker() throws UnirestException{
        String url = "https://127.0.0.1:8443/worker-0.0.1/api/workers/";
        int id_1 = 1;
        int id_2 = 453;
        String id_3 = null;
        String id_4 = "Smth went wrong";

        var test_1 = sendDeleteRequest(url, id_1);
        Assertions.assertEquals("[\"Worker with id 1 not found\"]", test_1.getBody());
        Assertions.assertEquals(400, test_1.getStatus());

//        var test_2 = sendDeleteRequest(url, id_2);
//        Assertions.assertEquals("", test_2.getBody());
//        Assertions.assertEquals(200, test_2.getStatus());

        var test_3 = sendGetRequest(url, id_3);
        Assertions.assertEquals("Id should be integer", test_3.getBody());
        Assertions.assertEquals(422, test_3.getStatus());

        var test_4 = sendGetRequest(url, id_4);
        Assertions.assertEquals("Id should be integer", test_4.getBody());
        Assertions.assertEquals(422, test_4.getStatus());

    }

    @Test
    public void testGetWorkers() throws UnirestException{
        String url = "https://127.0.0.1:8443/worker-0.0.1/api/workers";
        String test_2_res = "[\n" +
                "    \"Offset too large, no elements\",\n" +
                "    {\n" +
                "        \"NextPage\": false\n" +
                "    }\n" +
                "]";

        HttpResponse<String> test_1 = Unirest.get(url)
                .header("Content-Type","application/json")
                .queryString("filter", "workername[con]Anatoliyyy")
                .asString();
        Assertions.assertTrue(test_1.getBody().matches(".*Anatoliyy.*"));
        Assertions.assertEquals(200, test_1.getStatus());

        HttpResponse<String> test_2 = Unirest.get(url)
                .header("Content-Type","application/json")
                .queryString("page_offset", "100")
                .asString();
        Assertions.assertTrue(test_2.getBody().contains("\"Offset too large, no elements\""));
        Assertions.assertEquals(200, test_2.getStatus());

        HttpResponse<String> test_3 = Unirest.get(url)
                .header("Content-Type","application/json")
                .queryString("page_offset", "1")
                .queryString("page_size", "1")
                .asString();
        Assertions.assertTrue(test_3.getBody().replace("[UTC]", "").matches("\\[\\[[^\\[]*"));
        Assertions.assertTrue(test_3.getBody().contains("\"NextPage\":true"));
        Assertions.assertEquals(200, test_3.getStatus());

        HttpResponse<String> test_4 = Unirest.get(url)
                .header("Content-Type","application/json")
                .asString();
        Assertions.assertTrue(test_4.getBody().contains("\"NextPage\":false"));
        Assertions.assertEquals(200, test_4.getStatus());

        HttpResponse<String> test_5 = Unirest.get(url)
                .header("Content-Type","application/json")
                .queryString("filter", "id[asc]")
                .queryString("sort", "id[asc]")
                .asString();
        Assertions.assertEquals("[\"Illegal filter command: id[asc]\"]", test_5.getBody());
        Assertions.assertEquals(422, test_5.getStatus());

        HttpResponse<String> test_6 = Unirest.get(url)
                .header("Content-Type","application/json")
                .queryString("filter", "salary[ge]1")
                .queryString("sort", "salary[ge]1")
                .asString();
        Assertions.assertEquals("[\"Illegal sort command: salary[ge]1\"]", test_6.getBody());
        Assertions.assertEquals(422, test_6.getStatus());

        HttpResponse<String> test_7 = Unirest.get(url)
                .header("Content-Type","application/json")
                .queryString("filter", "salary[g]100")
                .queryString("filter", "salary[l]100")
                .asString();
        Assertions.assertEquals("[[],{\"NextPage\":false}]", test_7.getBody());
        Assertions.assertEquals(200, test_7.getStatus());

        HttpResponse<String> test_8 = Unirest.get(url)
                .header("Content-Type","application/json")
                .queryString("filter", "salary[ge]100")
                .asString();
        Assertions.assertEquals(200, test_8.getStatus());

        HttpResponse<String> test_9 = Unirest.get(url)
                .header("Content-Type","application/json")
                .queryString("filter", "salary[le]100")
                .asString();
        Assertions.assertEquals(200, test_9.getStatus());

        HttpResponse<String> test_10 = Unirest.get(url)
                .header("Content-Type","application/json")
                .queryString("filter", "salary[cmp]100")
                .asString();
        Assertions.assertEquals(200, test_10.getStatus());

        HttpResponse<String> test_11 = Unirest.get(url)
                .header("Content-Type","application/json")
                .queryString("filter", "salary[cmpn]100")
                .asString();
        Assertions.assertEquals(200, test_11.getStatus());

        HttpResponse<String> test_12 = Unirest.get(url)
                .header("Content-Type","application/json")
                .queryString("filter", "salary[con]100")
                .asString();
        Assertions.assertEquals("[\"Contains can only be applied to name: salary[con]100\"]", test_12.getBody());
        Assertions.assertEquals(422, test_12.getStatus());

        HttpResponse<String> test_13 = Unirest.get(url)
                .header("Content-Type","application/json")
                .queryString("filter", "workername[con]100")
                .asString();

        Assertions.assertEquals(200, test_13.getStatus());

        HttpResponse<String> test_14 = Unirest.get(url)
                .header("Content-Type","application/json")
                .queryString("filter", "aaa[le]100")
                .asString();

//        Assertions.assertEquals("", test_14.getBody());
//        Assertions.assertEquals(422, test_14.getStatus());

    }




}
