package lk.icbt.sunrise.dental.controller;

import lk.icbt.sunrise.dental.dto.BillResponse;
import lk.icbt.sunrise.dental.service.BillService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @GetMapping("/{appointmentNumber}")
    public BillResponse generateBill(@PathVariable String appointmentNumber) {
        return BillResponse.from(billService.generateBill(appointmentNumber));
    }
}
