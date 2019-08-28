package br.com.redhat.bank.offload.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.infinispan.InfinispanConstants;
import org.apache.camel.component.infinispan.InfinispanOperation;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import br.com.redhat.bank.offload.model.Invoice;
import br.com.redhat.bank.offload.service.InvoiceService;

@Component
public class ModernInvoiceRoute extends RouteBuilder{

    @Autowired
    private Environment env;

    @Override
    public void configure() throws Exception {

        restConfiguration()
                .contextPath("/fuse").apiContextPath("/api-doc")
                    .apiProperty("api.title", "Modern Invoice API")
                    .apiProperty("api.version", "1.0")
                    .apiProperty("cors", "true")
                    .apiProperty("api.specification.contentType.json", "application/vnd.oai.openapi+json;version=2.0")
                    .apiProperty("api.specification.contentType.yaml", "application/vnd.oai.openapi;version=2.0")
                    .apiContextRouteId("doc-api")
                    .port(env.getProperty("server.port", "8080"))
                .component("servlet")
                .bindingMode(RestBindingMode.json);

        rest("/invoice")
            .post().type(Invoice.class).to("direct:newInvoice")
            .get("/").outType(Invoice[].class).to("direct:getInvoice")
            .get("/{id}").outType(Invoice.class).to("direct:getInvoiceById")
            .get("/{customerName}/customer").outType(Invoice[].class).to("direct:getInvoiceByCustomerName");

        from("direct:newInvoice")
            .log("Starting newInvoice with body: ${body}")
            .to("jpa:br.com.redhat.bank.offload.model.Invoice")
            .log("Inserted new order: ${body}");

         from("direct:getInvoice")
            .log("Starting direct:getInvoice")
            .bean(InvoiceService.class, "findInvoice")
            .log("Return from InvoiceService.getInvoice: ${body}");

        from("direct:getInvoiceById")
            .setProperty("id", simple("${header.id}"))
            .log("Starting getInvoiceById with id: ${exchangeProperty[id]}")
            .setHeader(InfinispanConstants.OPERATION).constant(InfinispanOperation.GET)
            .setHeader(InfinispanConstants.KEY).exchangeProperty("id")
            .convertBodyTo(String.class)
            .to("infinispan://default?cacheContainer=#remoteCacheContainer")
            .log("Data grid output: ${body}") 
            .choice()
                .when(body().isNull())
                    .log("Not found on Cache with id: ${exchangeProperty[id]}") 
                    .log("Going to invoke InvoiceService.getInvoice(${exchangeProperty[id]})") 
                    .bean(InvoiceService.class, "getInvoice(${exchangeProperty[id]})")
                    .log("Result after invoking InvoiceService.getInvoice(${exchangeProperty[id]}): ${body}")
                    .setHeader(InfinispanConstants.OPERATION).constant(InfinispanOperation.PUT)
                    .setHeader(InfinispanConstants.KEY).exchangeProperty("id")
                    .setHeader(InfinispanConstants.VALUE).simple("${body}")
                    .to("infinispan://default?cacheContainer=#remoteCacheContainer")
                    .setHeader(InfinispanConstants.OPERATION).constant(InfinispanOperation.GET)
                    .setHeader(InfinispanConstants.KEY).exchangeProperty("id")
                    .convertBodyTo(String.class)
                    .to("infinispan://default?cacheContainer=#remoteCacheContainer")         
            .end();
            
        from("direct:getInvoiceByCustomerName")
            .log("Starting getInvoiceByCustomerName with customerName: ${header.customerName}")
            .bean(InvoiceService.class, "findInvoiceByCustomerName(${header.customerName})")
            .log("Return from InvoiceService.findInvoiceByCustomerName: ${body}");
   
        // from("timer://foo?period=10000&repeatCount=1")
        //     .setHeader(InfinispanConstants.OPERATION)
        //     .constant(InfinispanOperation.PUT).setHeader(InfinispanConstants.KEY).constant("1")
        //     .setHeader(InfinispanConstants.VALUE).constant("test").to("infinispan://default?cacheContainer=#remoteCacheContainer")
        //     .setBody().simple("Ramalho lindo")
        //     .log("Body: ${body}")
        //     .setHeader(InfinispanConstants.OPERATION).constant(InfinispanOperation.GET)
        //     .setHeader(InfinispanConstants.KEY).constant("1").to("infinispan://default?cacheContainer=#remoteCacheContainer")
        //     .log("Received body: ${body}");
    }

}