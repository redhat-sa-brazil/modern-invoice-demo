package br.com.redhat.bank.offload.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.redhat.bank.offload.model.Invoice;
import br.com.redhat.bank.offload.model.InvoiceRepository;

@Service
public class InvoiceService {

    @Autowired
    InvoiceRepository invoiceRepository;

    public Iterable<Invoice> findInvoice(){
        return invoiceRepository.findAll();
    }

    public Invoice getInvoice(Integer id){
        return invoiceRepository.findOne(id);
    }

    public Iterable<Invoice> findInvoiceByCustomerName(String customerName){
        return invoiceRepository.findInvoiceByCustomerName(customerName);
    }
}