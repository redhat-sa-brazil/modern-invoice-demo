package br.com.redhat.bank.offload.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.infinispan.InfinispanConstants;
import org.apache.camel.component.infinispan.InfinispanOperation;
import org.springframework.stereotype.Component;

@Component
public class ModernInvoiceRoute extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {
		
		from("timer://foo?period=10000")
			.setHeader(InfinispanConstants.OPERATION)
			.constant(InfinispanOperation.PUT).setHeader(InfinispanConstants.KEY).constant("1")
			.setHeader(InfinispanConstants.VALUE).constant("test").to("infinispan://default?cacheContainer=#remoteCacheContainer")
			.setBody().simple("Ramalho lindo")
			.log("Body: ${body}")
			.setHeader(InfinispanConstants.OPERATION).constant(InfinispanOperation.GET)
			.setHeader(InfinispanConstants.KEY).constant("1").to("infinispan://default?cacheContainer=#remoteCacheContainer")
			.log("Received body: ${body}");
	}
	
}
