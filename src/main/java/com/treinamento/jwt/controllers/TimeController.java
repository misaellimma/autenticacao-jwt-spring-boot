package com.treinamento.jwt.controllers;

import com.treinamento.jwt.entity.Time;
import com.treinamento.jwt.repository.TimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("time")
@CrossOrigin
public class TimeController {

    @Autowired
    private TimeRepository timeRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Time>> listarTime(@RequestParam(required = false) String nome){
        try{
            List<Time> times = new ArrayList<Time>();
            if(nome == null){
                timeRepository.findAll().forEach(times::add);
            }else{
                timeRepository.findByNomeContaining(nome).forEach(times::add);
            }

            if(times.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(times, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @GetMapping("/{id}")
    public ResponseEntity<Time> consultar(@PathVariable Integer id) {
        Time time =  timeRepository.findById(id).get();

        if(time == null){
            return new ResponseEntity<>(time, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("incluir")
    public ResponseEntity<String> incluirTime(@Validated @RequestBody Time time) {
        try{
            timeRepository.save(time);
            return new ResponseEntity<>("Criado com sucesso.", HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @PutMapping("alterar")
    public ResponseEntity<Time> alterarTime(@RequestBody @Validated Time time) {
        return timeRepository.findById(time.getId())
                .map(record -> {
                    record.setNome(time.getNome());
                    Time updated = timeRepository.save(record);
                    return ResponseEntity.ok().body(updated);
                }).orElse(ResponseEntity.notFound().build());
    }
}