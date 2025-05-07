package com.fifa_app.league_manager.endpoint;


import com.fifa_app.league_manager.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController@RequiredArgsConstructor@RequestMapping("transfert")
public class TransferController {

    private final TransferService transferService;

    @GetMapping
    public Object getTransfer() {
        return  transferService.getAll();
    }
}
