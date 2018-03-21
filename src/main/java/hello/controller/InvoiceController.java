package hello.controller;

import hello.EmptyJsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import hello.model.*;
import hello.repository.*;

import javax.validation.Valid;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping(path = "invoice")
public class InvoiceController {
    @Autowired
    private InvoiceRepository invoiceRepository;

    @GetMapping(path = {"", "/all"})
    public @ResponseBody
    Iterable<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable(value = "id") Long invoiceId) {
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(invoiceId);
        if (!optionalInvoice.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Invoice invoice = optionalInvoice.get();
        return ResponseEntity.ok().body(invoice);
    }
}
