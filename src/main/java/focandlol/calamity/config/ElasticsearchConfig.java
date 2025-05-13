package focandlol.calamity.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@Slf4j
@EnableElasticsearchRepositories
public class ElasticsearchConfig extends ElasticsearchConfiguration {

  @Value("${spring.elasticsearch.uris}")
  private String host;

//  @Value("${spring.elasticsearch.username}")
//  private String username;
//
//  @Value("${spring.elasticsearch.password}")
//  private String password;

  @Override
  public ClientConfiguration clientConfiguration() {
    return ClientConfiguration.builder()
        .connectedTo(host)
        .withConnectTimeout(Duration.ofSeconds(160))
        .withSocketTimeout(Duration.ofSeconds(160))
        //.usingSsl(disableSslVerification(), allHostsValid()) // ssl 사용
        //.withBasicAuth(username, password)
        .build();
  }

  public static SSLContext disableSslVerification() {
    try {
      // ============================================
      // trust manager 생성(인증서 체크 전부 안함)
      TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
          return null;
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
      }};

      // trust manager 설치
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

      return sc;
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static HostnameVerifier allHostsValid() {

    // ============================================
    // host name verifier 생성(호스트 네임 체크안함)
    HostnameVerifier allHostsValid = (hostname, session) -> true;

    // host name verifier 설치
    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    return allHostsValid;

  }
}
