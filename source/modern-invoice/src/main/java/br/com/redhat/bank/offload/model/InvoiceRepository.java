package br.com.redhat.bank.offload.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface InvoiceRepository extends CrudRepository<Invoice, Integer> {

    @Query("SELECT i FROM Invoice i where i.customerName = ?1")
    Iterable<Invoice> findInvoiceByCustomerName(String customerName);
    
}