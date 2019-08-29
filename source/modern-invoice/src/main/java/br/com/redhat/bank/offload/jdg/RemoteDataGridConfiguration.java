package br.com.redhat.bank.offload.jdg;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.SaslQop;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.commons.api.BasicCacheContainer;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.annotations.ProtoSchemaBuilder;
import org.infinispan.protostream.annotations.ProtoSchemaBuilderException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import br.com.redhat.bank.offload.model.Invoice;

@Configuration
public class RemoteDataGridConfiguration {

    private static final String CRT_PATH = "/var/run/secrets/kubernetes.io/serviceaccount/service-ca.crt";
	private static final char[] TRUSTSTORE_PASSWORD = "secret".toCharArray();
	private static final String TRUSTSTORE_PATH = "truststore.pkcs12";

    private static final String SERVICE_NAME = System.getenv().getOrDefault("APPLICATION_NAME", "cache-service");
	private static final String USER_NAME = System.getenv().getOrDefault("APPLICATION_USER", "cache");
	private static final String USER_PASSWORD = System.getenv().getOrDefault("APPLICATION_PASSWORD", "cache");
	
	@Bean(initMethod = "start", destroyMethod = "stop")
	public BasicCacheContainer remoteCacheContainer(Environment environment) throws ProtoSchemaBuilderException, IOException {
		createTruststoreFromCrtFile(CRT_PATH, TRUSTSTORE_PATH, TRUSTSTORE_PASSWORD);
	    ConfigurationBuilder builder = new ConfigurationBuilder();
	    	builder.addServer()
	        	.host(SERVICE_NAME)
				.port(11222)
				.marshaller(new ProtoStreamMarshaller())
	            .security().authentication().enable()
	         		.username(USER_NAME)
		            .password(USER_PASSWORD)
		            .realm("ApplicationRealm")
		            .serverName(SERVICE_NAME)
		            .saslMechanism("DIGEST-MD5")
		            .saslQop(SaslQop.AUTH)
	            .ssl().enable()
		            .trustStoreFileName(TRUSTSTORE_PATH)
		            .trustStorePassword(TRUSTSTORE_PASSWORD)
					.build();
		RemoteCacheManager remoteCacheManager = new RemoteCacheManager(builder.create(), true);
		SerializationContext ctx = ProtoStreamMarshaller.getSerializationContext(remoteCacheManager);
		RemoteCache<String, String> metadataCache = remoteCacheManager.getCache(org.infinispan.query.remote.client.ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
		ProtoSchemaBuilder protoSchemaBuilder = new ProtoSchemaBuilder();
		try {
			final String fileName = "invoice.proto";
			String protoFile = protoSchemaBuilder
				.fileName(fileName)
				.addClass(Invoice.class)
				.packageName("invoice")
				.build(ctx);
			metadataCache.put(fileName, protoFile);
			String filesWithErrors = metadataCache.get(org.infinispan.query.remote.client.ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX);
			if (filesWithErrors != null)
				throw new AssertionError("Error in proto file(s): " + filesWithErrors);
			else
				System.out.println("Added schema file: " + fileName);
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		return remoteCacheManager;
	}
	
	private static void createTruststoreFromCrtFile(String crtPath, String tsPath, char[] password) {
		createTruststore(parseCrtFile(crtPath), tsPath, password);
	}

	private static void createTruststore(List<String> certs, String path, char[] password) {
		try {
			try (FileOutputStream output = new FileOutputStream(path)) {
				KeyStore trustStore = KeyStore.getInstance("PKCS12");
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				trustStore.load(null, null);

				for (int i = 0; i < certs.size(); i++) {
					String alias = i < 10 ? "service-crt-0" : "service-crt-";
					String cert = certs.get(i);
					try (InputStream input = Base64.getDecoder().wrap(new ByteArrayInputStream(cert.getBytes(StandardCharsets.UTF_8)))) {
						Certificate certificate = cf.generateCertificate(input);
						trustStore.setCertificateEntry(alias + i, certificate);
					}
				}
				trustStore.store(output, password);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

   private static List<String> parseCrtFile(String path) {
      try {
         List<String> certs = new ArrayList<>();
         StringBuilder sb = new StringBuilder();
         for (String line : Files.readAllLines(Paths.get(path))) {
            if (line.isEmpty() || line.contains("BEGIN CERTIFICATE"))
               continue;
            if (line.contains("END CERTIFICATE")) {
               certs.add(sb.toString());
               sb.setLength(0);
            } else {
               sb.append(line);
            }
         }
         return certs;
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
    
}