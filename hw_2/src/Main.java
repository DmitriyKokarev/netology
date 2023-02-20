import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    public static final String URI = "https://api.nasa.gov/planetary/apod?api_key=m3UkGWjlxiPTTkElA9JyaqRBkWYIyyhIDUnFngSk";

    //Сущность, которая будет преобразовывать ответ в наш объект NASA
    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        //напишите тут ваш код
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        HttpGet request = new HttpGet(URI);
        CloseableHttpResponse response = httpClient.execute(request);

        NasaObject nasaObject = mapper.readValue(response.getEntity().getContent(), NasaObject.class);
        System.out.println(nasaObject);

        CloseableHttpResponse PictureResponse = httpClient.execute(new HttpGet(nasaObject.getUrl()));

        String[] url_arr = nasaObject.getUrl().split("/");
        String file = url_arr[6];

        //Проверяем что наш ответ не null
        HttpEntity entity = PictureResponse.getEntity();
        if (entity != null) {
            //сохраняем в файл
            FileOutputStream fos = new FileOutputStream(file);
            entity.writeTo(fos);
            fos.close();
        }

    }
}
