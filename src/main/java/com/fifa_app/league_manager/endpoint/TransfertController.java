package com.fifa_app.league_manager.endpoint;


import com.fifa_app.league_manager.service.TransfertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController@RequiredArgsConstructor@RequestMapping("transfert")
public class TransfertController {

    private final TransfertService transfertService;

    @GetMapping
    public Object getTransfert() {
        return  transfertService.getAll();
    }
}
