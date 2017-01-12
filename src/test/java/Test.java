import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.json.JSONObject;

/**
 * Created by xianbin.yang on 2017/1/11.
 */
public class Test {
    public static void main(String[] args) throws Exception {

        //1、获取token
        String getToken = "http://localhost:8080/oauth/token?grant_type=password&username=root&password=123456&client_id=weixin&client_secret=123456";
        HttpResponse<String> response = Unirest.post(getToken).asString();
        String body = response.getBody();
        System.out.println("response=" + body);
        JSONObject jsonObject = new JSONObject(body);
        String access_token = jsonObject.getString("access_token");
        System.out.println("access_token=" + access_token);

        //2、访问url
        String resource = "http://localhost:8080/api/test";
        HttpResponse<String> response2 = Unirest.get(resource)
                .header("Authorization", "Bearer " + access_token).asString();
        System.out.println("response=" + response2.getBody());

    }
}
